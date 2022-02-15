package it.adc.p2p.chat.exceptions;

public class DuplicatePeer extends Exception{
    public DuplicatePeer(int id) {
        super("Duplicate peer with id: "+id);
    }
}
