package it.adc.p2p.chat;

public class Example {
    private static final int MODE = 1; // 0 -> ExampleSimple, 1 -> AnonymousChatImpl

    public static void main(String[] args) throws NumberFormatException, Exception {

        if(MODE == 0){
            ExampleSimple dns = new ExampleSimple(Integer.parseInt(args[0]));
            if (args.length == 3) {
                dns.store(args[1], args[2]);
            }
            if (args.length == 2) {
                System.out.println("Name:" + args[1] + " IP:" + dns.get(args[1]));
            }
        }else{
            System.out.println("Nothing here yet :( ");
        }

    }

}
