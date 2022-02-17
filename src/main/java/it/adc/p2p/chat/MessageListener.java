package it.adc.p2p.chat;

import net.tomp2p.peers.Number160;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

public class MessageListener {

    int peer_id;
    Number160 peer_addr;

    public MessageListener(int peer_id)
    {
        this.peer_id=peer_id;
        this.peer_addr = null;
    }

    protected void setHash(Number160 peer_addr){this.peer_addr = peer_addr;}

    public Object parseMessage(Object sender, Object _request) {

        // Chat message received

        String[] room_and_message = _request.toString().split("\n");

        if(room_and_message.length==2){

            if(sender.hashCode() != peer_addr.hashCode()){
                TextIO textIO = TextIoFactory.getTextIO();
                TextTerminal terminal = textIO.getTextTerminal();
                terminal.println("<From: "+sender.hashCode() + " In: " + room_and_message[0] + "> " + room_and_message[1]+" ✓");
            }

            return "success";
        }else{
            // Reserved for different kind of messages
            return "failure";
        }


    }
}
