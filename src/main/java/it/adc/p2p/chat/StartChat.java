package it.adc.p2p.chat;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class StartChat {

    @Option(name="-m", aliases="--masterip", usage="the master peer ip address", required=true)
    private static String master;

    @Option(name="-id", aliases="--identifierpeer", usage="the unique identifier for this peer", required=true)
    private static int id;

    public static void main(String[] args) throws Exception {

        StartChat example = new StartChat();
        final CmdLineParser parser = new CmdLineParser(example);
        try {
            parser.parseArgument(args);

            //TEXTIO
            TextIO textIO = TextIoFactory.getTextIO();
            TextTerminal terminal = textIO.getTextTerminal();

            AnonymousChatImpl peer = new AnonymousChatImpl(id, master, new MessageListener(id), 4000);

            //TEXTIO
            terminal.getProperties().setPromptColor("cyan");
            terminal.println("v 1.1");
            terminal.println("Peer ID: "+id+" - MASTER NODE ADDRESS: "+master);
            terminal.getProperties().setPromptColor("green");


            printMenu(terminal);

            while(true) {
                String recev;
                boolean is_int;
                do {
                    recev = "✓";

                    while (recev.contains("✓")) {
                        recev = textIO.newStringInputReader()
                                .read(">>");
                    }
                    try{
                        if(Integer.parseInt(recev) > 0 && Integer.parseInt(recev) < 6){
                            is_int = true;
                        }else{
                            is_int = false;
                            terminal.println("Expected an integer value between 1 and 5.");
                        }
                    }catch (NumberFormatException e){
                        terminal.println("Expected an integer value between 1 and 5.");
                        is_int = false;
                    }
                }while(!is_int);

                int option = Integer.parseInt(recev);

                if(!(option>0 && option <6)){
                    option= textIO.newIntInputReader()
                            .withMinVal(1)
                            .withMaxVal(5)
                            .read(">>");
                }


                switch (option) {

                    // Create room
                    case 1:
                        String name = textIO.newStringInputReader()
                                .read("Room name:");
                        if(peer.createRoom(name))
                            terminal.println("Room ["+name+"] successfully created and joined");
                        else{
                            terminal.executeWithPropertiesConfigurator(
                                    properties -> terminal.getProperties().setPromptColor("red"),
                                    textTerminal -> terminal.println("Error in room creation"));
                            terminal.getProperties().setPromptColor("green");
                        }

                        break;

                    // Join room
                    case 2:
                        String sname = textIO.newStringInputReader()
                                .read("Room name:");
                        if(peer.joinRoom(sname))
                            terminal.println("Successfully joined ["+sname+"]");
                        else{
                            terminal.executeWithPropertiesConfigurator(
                                    properties -> terminal.getProperties().setPromptColor("red"),
                                    textTerminal -> terminal.println("Error in joining room ["+sname+"]"));
                            terminal.getProperties().setPromptColor("green");
                        }

                        break;

                    // Leave room
                    case 3:
                        String uname = textIO.newStringInputReader()
                                .read("Room Name:");
                        if(peer.leaveRoom(uname))
                            terminal.println("Successfully left room ["+uname+"]");
                        else{
                            terminal.executeWithPropertiesConfigurator(
                                    properties -> terminal.getProperties().setPromptColor("red"),
                                    textTerminal -> terminal.println("Error in leaving room ["+uname+"]"));
                            terminal.getProperties().setPromptColor("green");
                        }

                        break;

                    // Send message
                    case 4:
                        String tname = textIO.newStringInputReader()
                                .read("Room name:");
                        String message = textIO.newStringInputReader()
                                .read(" Message:");

                        if(peer.sendMessage(tname,message)){
                            terminal.println("Sent ✓");
                        }
                        else{
                            terminal.executeWithPropertiesConfigurator(
                                    properties -> terminal.getProperties().setPromptColor("red"),
                                    textTerminal -> terminal.println("Not sent ❌"));
                            terminal.getProperties().setPromptColor("green");
                        }
                        break;

                    // Leave network
                    case 5:
                        terminal.println("\nARE YOU SURE TO LEAVE THE NETWORK?");
                        boolean exit = textIO.newBooleanInputReader()
                                .withDefaultValue(false)
                                .read("Exit?");
                        if(exit) {
                            peer.leaveNetwork();
                            System.exit(0);
                        }
                        break;

                    default:
                        break;
                }
            }
        } catch(CmdLineException clEx) {
            System.err.println("ERROR: Unable to parse command-line options: " + clEx);
        }
    }

    public static void printMenu(TextTerminal terminal) {
        terminal.getProperties().setPromptColor("magenta");
        terminal.println("\n1 - CREATE ROOM");
        terminal.println("\n2 - JOIN ROOM");
        terminal.println("\n3 - LEAVE ROOM");
        terminal.println("\n4 - SEND MESSAGE");
        terminal.println("\n5 - LEAVE NETWORK");
        terminal.getProperties().setPromptColor("green");
    }

}

