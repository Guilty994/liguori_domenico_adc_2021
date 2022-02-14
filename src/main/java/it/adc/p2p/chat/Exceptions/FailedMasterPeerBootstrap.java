package it.adc.p2p.chat.Exceptions;

public class FailedMasterPeerBootstrap extends Exception{
    public FailedMasterPeerBootstrap() {
        super("Error in peer bootstrapping");
    }
}
