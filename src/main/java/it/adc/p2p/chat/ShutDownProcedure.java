package it.adc.p2p.chat;

public class ShutDownProcedure extends Thread {

    final private AnonymousChatImpl peer;

    public ShutDownProcedure(AnonymousChatImpl peer) {
        this.peer = peer;
    }

    public void run() {
        peer.leaveNetwork();
    }
}
