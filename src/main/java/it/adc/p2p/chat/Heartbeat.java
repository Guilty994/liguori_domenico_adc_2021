package it.adc.p2p.chat;

import it.adc.p2p.chat.exceptions.DNSException;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.FuturePut;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FuturePing;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.storage.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

public class Heartbeat {
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void checkDNS(Peer peer, PeerDHT _dht) {
        final Runnable heartbeat = () -> {
            boolean must_update = false;
            FutureGet futureGet = _dht.get(Number160.createHash("NETWORK_DNS")).start();
            futureGet.awaitUninterruptibly();
            if(futureGet.isSuccess()&&!futureGet.isEmpty()){
                try {
                    if (futureGet.dataMap().values().iterator().hasNext()) {
                        ArrayList<Integer> toRemove = new ArrayList<>();
                        HashMap<Integer, PeerAddress> tracked_dns = (HashMap<Integer, PeerAddress>) futureGet.dataMap().values().iterator().next().object();
                        for (HashMap.Entry<Integer, PeerAddress> dns_entry : tracked_dns.entrySet()) {
                            FuturePing fp = peer.ping().peerAddress(dns_entry.getValue()).tcpPing().start();
                            fp.awaitUninterruptibly();
                            if (fp.isFailed()) { // found a zombie
                                toRemove.add(dns_entry.getKey());
                                must_update = true;
                            }
                        }

                        for (Integer toRemoveID : toRemove) {
                            tracked_dns.remove(toRemoveID);
                        }

                        if (must_update) {
                            FuturePut futurePut = _dht.put(Number160.createHash("NETWORK_DNS")).data(new Data(tracked_dns)).start().awaitUninterruptibly();
                            if (futurePut.isFailed())
                                throw new DNSException("update");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        scheduler.scheduleAtFixedRate(heartbeat, 10, 30, SECONDS);

    }

    public void stop(){
        scheduler.shutdown();
    }
}
