package it.adc.p2p.chat.exceptions;

public class DNSException extends Exception{
    public DNSException(String type) {
        super("DNS "+type+" exception");
    }
}
