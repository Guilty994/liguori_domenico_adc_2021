package it.adc.p2p.chat;

import it.adc.p2p.chat.exceptions.DNSException;
import it.adc.p2p.chat.exceptions.DuplicatePeer;
import it.adc.p2p.chat.exceptions.FailedMasterPeerBootstrap;
import it.adc.p2p.chat.exceptions.NetworkError;
import net.tomp2p.dht.*;
import net.tomp2p.futures.*;
import net.tomp2p.p2p.*;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class AnonymousChatImpl implements AnonymousChat{

    final private Peer peer;
    final private PeerDHT _dht;
    final private int MASTER_PORT;
    final private Integer id;

    final private Heartbeat schedule;


    // List of the room this peer joined
    final private ArrayList<String> room_list = new ArrayList<>();

    public AnonymousChatImpl(int _id, String _master_peer, final MessageListener _listener, int _master_port) throws Exception {
        this.id = _id;
        MASTER_PORT = _master_port;

        //Create the peer, set his port to 4000+_id, add that peer to the DHT
        peer= new PeerBuilder(Number160.createHash(_id)).ports(MASTER_PORT+_id).start();
        _dht = new PeerBuilderDHT(peer).start();

        //Bootstrap of the peers on master address
        FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(_master_peer)).ports(MASTER_PORT).start();
        fb.awaitUninterruptibly();

        // If bootstrap is successful, start discover network
        if(fb.isSuccess()) {

            // discover network
            peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();


            // Check if a peer with same id already in the network by checking DNS
            FutureGet futureGet = _dht.get(Number160.createHash("NETWORK_DNS")).start();
            futureGet.awaitUninterruptibly();

            if(futureGet.isSuccess()){
                if(futureGet.isEmpty()){
                    // Master node initialize the dns
                    HashMap<Integer, PeerAddress> dns = new HashMap<>();
                    dns.put(_id, peer.peerAddress());
                    FuturePut futurePut = _dht.put(Number160.createHash("NETWORK_DNS")).data(new Data(dns)).start().awaitUninterruptibly();
                    if(futurePut.isFailed())
                        throw new DNSException("creation");
                }else{
                    // Normal peers check if they're already in the network
                    HashMap<Integer, PeerAddress> dns = (HashMap<Integer, PeerAddress>) futureGet.dataMap().values().iterator().next().object();
                    if(dns.containsKey(_id)){
                        // Duplicate peer found
                        throw new DuplicatePeer(_id);
                    }else{
                        // The peer add itself in the network dns
                        dns.put(_id, peer.peerAddress());
                        FuturePut futurePut = _dht.put(Number160.createHash("NETWORK_DNS")).data(new Data(dns)).start().awaitUninterruptibly();
                        if(futurePut.isFailed()){
                            throw new DNSException("update");
                        }
                    }
                }
            }else{
                throw new NetworkError();
            }

        }else {
            throw new FailedMasterPeerBootstrap();
        }
        // Update listener info
        _listener.setHash(peer.peerID());



        // Periodically check the network for peers failure and restore consistency
        schedule = new Heartbeat();
        schedule.checkDNS(peer, _dht);


        //Wait for messages to be received
        peer.objectDataReply(_listener::parseMessage);
    }

    @Override
    public boolean createRoom(String _room_name) {
        if(!_room_name.equals("NETWORK_DNS")){
            try {
                FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
                futureGet.awaitUninterruptibly();
                if (futureGet.isSuccess()){
                    if(futureGet.isEmpty()){
                        FuturePut futurePut = _dht.put(Number160.createHash(_room_name)).data(new Data(new HashSet<PeerAddress>())).start().awaitUninterruptibly();
                        if(futurePut.isSuccess()){
                            return joinRoom(_room_name);
                        }else{
                            throw new NetworkError();
                        }
                    }else{
                        HashSet<PeerAddress> peers_in_room = (HashSet<PeerAddress>) futureGet.dataMap().values().iterator().next().object();

                        boolean must_update = false;
                        // Check for zombies -> peers that are listed in the room but no more active in the network
                        for (PeerAddress pa:peers_in_room){
                            FuturePing fp = peer.ping().peerAddress(pa).tcpPing().start();
                            fp.awaitUninterruptibly();
                            if(fp.isFailed()){ // found a zombie
                                peers_in_room.remove(pa);
                                must_update = true;
                            }
                        }
                        if(must_update){
                            if(peers_in_room.isEmpty()){
                                FutureRemove fr = _dht.remove(Number160.createHash(_room_name)).start().awaitUninterruptibly();
                                if(fr.isSuccess())
                                    return createRoom(_room_name); // -> room already exist, all the peers crashed. now the dht is up-to-date
                            }else{
                                _dht.put(Number160.createHash(_room_name)).data(new Data(peers_in_room)).start().awaitUninterruptibly();
                                return false; // -> room already exist, there is someone inside but some peer crashed. now the dht is up-to-date
                            }
                        }else
                            return false; // -> room already exist, there is someone inside and all the peers were active
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean joinRoom(String _room_name) {
        if(!_room_name.equals("NETWORK_DNS")){
            try {
                FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
                futureGet.awaitUninterruptibly();
                if (futureGet.isSuccess()) {
                    if(futureGet.isEmpty() ) return false; // room doesn't exist
                    HashSet<PeerAddress> peers_in_room;
                    peers_in_room = (HashSet<PeerAddress>) futureGet.dataMap().values().iterator().next().object();
                    peers_in_room.add(_dht.peer().peerAddress());
                    _dht.put(Number160.createHash(_room_name)).data(new Data(peers_in_room)).start().awaitUninterruptibly();
                    room_list.add(_room_name);
                    return true;
                }
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean leaveRoom(String _room_name) {
        if(!_room_name.equals("NETWORK_DNS")){
            try {
                FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
                futureGet.awaitUninterruptibly();
                if (futureGet.isSuccess()) {
                    if(futureGet.isEmpty() ) return false;
                    HashSet<PeerAddress> peers_in_room;
                    peers_in_room = (HashSet<PeerAddress>) futureGet.dataMap().values().iterator().next().object();

                    if(peers_in_room.contains(_dht.peer().peerAddress())){
                        if(room_list.contains(_room_name)){
                            peers_in_room.remove(_dht.peer().peerAddress());
                            _dht.put(Number160.createHash(_room_name)).data(new Data(peers_in_room)).start().awaitUninterruptibly();
                            room_list.remove(_room_name);
                            if(peers_in_room.isEmpty()){
                                return removeRoom(_room_name);
                            }
                            return true;
                        }else{
                            // Fix possible network error (peer in dht but not in local room_list)
                            peers_in_room.remove(_dht.peer().peerAddress());
                            _dht.put(Number160.createHash(_room_name)).data(new Data(peers_in_room)).start().awaitUninterruptibly();
                            if(peers_in_room.isEmpty()){
                                removeRoom(_room_name);
                            }
                            return false;
                        }
                    }else{
                        room_list.remove(_room_name);
                        return false;
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return false;
    }

    private boolean removeRoom(String _room_name){
        FutureRemove futureRemove = _dht.remove(Number160.createHash(_room_name)).start();
        futureRemove.awaitUninterruptibly();
        return futureRemove.isSuccess();
    }

    @Override
    public boolean sendMessage(String _room_name, String _text_message) {
        if(!_room_name.equals("NETWORK_DNS")){
            try {
                if(room_list.contains(_room_name)){
                    FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
                    futureGet.awaitUninterruptibly();
                    if (futureGet.isSuccess()) {
                        HashSet<PeerAddress> peers_in_room;
                        peers_in_room = (HashSet<PeerAddress>) futureGet.dataMap().values().iterator().next().object();
                        for(PeerAddress peer:peers_in_room){
                            FutureDirect futureDirect = _dht.peer().sendDirect(peer).object(_room_name+"\n"+_text_message).start();
                            futureDirect.awaitUninterruptibly();
                        }
                        return true;
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean leaveNetwork(){
        // Leave all the rooms
        for(String room: new ArrayList<>(room_list)) leaveRoom(room);

        try{
            // Update dns info
            FutureGet futureGet = _dht.get(Number160.createHash("NETWORK_DNS")).start();
            futureGet.awaitUninterruptibly();
            if(futureGet.isSuccess()) {
                if (!futureGet.isEmpty()) {
                    // Peer check if he's in the DNS correctly
                    HashMap<Integer, PeerAddress> dns = (HashMap<Integer, PeerAddress>) futureGet.dataMap().values().iterator().next().object();
                    if (dns.containsKey(id)) {
                        // Remove the peer from the dns table
                        dns.remove(id);
                        FuturePut futurePut = _dht.put(Number160.createHash("NETWORK_DNS")).data(new Data(dns)).start().awaitUninterruptibly();
                        if(futurePut.isFailed()){
                            throw new DNSException("update");
                        }
                    }
                }
            }

            // Stop the scheduled task -- there is no need to stop it in case of forced exit
            schedule.stop();

            // Peer shutdown
            _dht.peer().announceShutdown().start().awaitUninterruptibly();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}