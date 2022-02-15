package it.adc.p2p.chat;

import it.adc.p2p.chat.exceptions.DNSException;
import it.adc.p2p.chat.exceptions.DuplicatePeer;
import it.adc.p2p.chat.exceptions.FailedMasterPeerBootstrap;
import it.adc.p2p.chat.exceptions.NetworkError;
import net.tomp2p.dht.*;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;

// TODO DNS refresh when peer leave the network

public class AnonymousChatImpl implements AnonymousChat{

    final private Peer peer;
    final private PeerDHT _dht;
    private int MASTER_PORT;


    // List of the room this peer joined
    final private ArrayList<String> room_list = new ArrayList<>();

    public AnonymousChatImpl(int _id, String _master_peer, final MessageListener _listener, int _master_port) throws Exception {
        MASTER_PORT = _master_port;

        //Create the peer, set his port to 4000+_id, add that peer to the DHT
        peer= new PeerBuilder(Number160.createHash(_id)).ports(MASTER_PORT+_id).start();
        _dht = new PeerBuilderDHT(peer).start();

        //Bootstrap of the peers on master address
        FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(_master_peer)).ports(MASTER_PORT).start();
        fb.awaitUninterruptibly();

        // If bootstrap is successful, start discover network
        if(fb.isSuccess()) {
            FutureDiscover fd = peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();

            // Check if we're trying to start 2 peers on the same socket
            /*if(fd.isFailed() && !fd.isDiscoveredTCP() && !fd.isDiscoveredUDP() && _id!= 0){
                throw new DuplicatePeer(_id); // DOESN'T WORK ON DOCKER
            }*/

            // Check if a peer with same id already in the network
            FutureGet futureGet = _dht.get(Number160.createHash("NETWORK_DNS")).start();
            futureGet.awaitUninterruptibly();

            if(futureGet.isSuccess()){
                if(futureGet.isEmpty()){
                    // Master node initialize the dns
                    HashSet<String> dns = new HashSet<>();
                    dns.add("id:"+_id);
                    FuturePut futurePut = _dht.put(Number160.createHash("NETWORK_DNS")).data(new Data(dns)).start().awaitUninterruptibly();
                    if(futurePut.isFailed())
                        throw new DNSException("creation");
                }else{
                    // Normal peers check if they're already in the network
                    HashSet<String> dns = (HashSet<String>) futureGet.dataMap().values().iterator().next().object();
                    if(dns.contains("id:"+_id)){
                        // Duplicate peer found
                        throw new DuplicatePeer(_id);
                    }else{
                        // The peer add itself in the network dns
                        dns.add("id:"+_id);
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

        //Wait for messages to be received
        peer.objectDataReply(_listener::parseMessage);
    }

    @Override
    public boolean createRoom(String _room_name) {
        if(!_room_name.equals("NETWORK_DNS")){
            try {
                FutureGet futureGet = _dht.get(Number160.createHash(_room_name)).start();
                futureGet.awaitUninterruptibly();
                if (futureGet.isSuccess() && futureGet.isEmpty()){
                    FuturePut futurePut = _dht.put(Number160.createHash(_room_name)).data(new Data(new HashSet<PeerAddress>())).start().awaitUninterruptibly();
                    if(futurePut.isSuccess()){
                        joinRoom(_room_name);
                        return true;
                    }else{
                        //check if the nodes in the room are dead TODO
                        return false;
                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
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
                    if(futureGet.isEmpty() ) return false;
                    HashSet<PeerAddress> peers_in_room;
                    peers_in_room = (HashSet<PeerAddress>) futureGet.dataMap().values().iterator().next().object();
                    peers_in_room.add(_dht.peer().peerAddress());
                    _dht.put(Number160.createHash(_room_name)).data(new Data(peers_in_room)).start().awaitUninterruptibly();
                    room_list.add(_room_name);
                    return true;
                }
            }catch (Exception e) {
                e.printStackTrace();
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
                            // Fix network error
                            peers_in_room.remove(_dht.peer().peerAddress());
                            _dht.put(Number160.createHash(_room_name)).data(new Data(peers_in_room)).start().awaitUninterruptibly();
                            if(peers_in_room.isEmpty()){
                                return removeRoom(_room_name);
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
                        for(PeerAddress peer:peers_in_room)
                        {
                            FutureDirect futureDirect = _dht.peer().sendDirect(peer).object(_room_name+"\n"+_text_message).start();
                            futureDirect.awaitUninterruptibly();
                        }

                        return true;
                    }
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public boolean leaveNetwork(){
        for(String room: new ArrayList<>(room_list)) leaveRoom(room);
        _dht.peer().announceShutdown().start().awaitUninterruptibly();

        return true;
    }
}