package it.adc.p2p.chat;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Example {

    @Option(name="-m", aliases="--masterip", usage="the master peer ip address", required=true)
    private static String master;

    @Option(name="-id", aliases="--identifierpeer", usage="the unique identifier for this peer", required=true)
    private static int id;

    public static void main(String[] args) throws NumberFormatException, Exception {

        Example example = new Example();
        final CmdLineParser parser = new CmdLineParser(example);
        try {
            parser.parseArgument(args);

            //TEXTIO
            TextIO textIO = TextIoFactory.getTextIO();
            TextTerminal terminal = textIO.getTextTerminal();

            AnonymousChatImpl peer =
                    new AnonymousChatImpl(id, master, new MessageListener(id));

            //TEXTIO
            terminal.getProperties().setPromptColor("cyan");
            terminal.println("Peer ID: "+id+" - MASTER NODE ADDRESS: "+master);
            terminal.getProperties().setPromptColor("green");


            printMenu(terminal);

            while(true) {

                String recev = textIO.newStringInputReader()
                        .read(">>");

                while(recev.contains("✓")){
                    recev = textIO.newStringInputReader()
                            .read(">>");
                }

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
                        terminal.println("Room name:");
                        String uname = textIO.newStringInputReader()
                                .read("Name:");
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

