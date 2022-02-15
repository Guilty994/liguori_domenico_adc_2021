package it.adc.p2p.chat.exceptions;

public class NetworkError extends Exception{
    public NetworkError() {
        super("Network error occurred, please try again.");
    }
}
