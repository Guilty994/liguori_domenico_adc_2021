package it.adc.p2p.chat;


import org.junit.jupiter.api.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAnonymousChat {

    @Test
    void testCase_CreateNonExistingRoom() {
        AnonymousChatImpl peer0, peer1, peer2, peer3;

        try{

            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 4000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 4000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 4000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 4000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};

            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
            }

            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.leaveNetwork());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void testCase_CreateExistingRoom(){
        AnonymousChatImpl peer0, peer1, peer2, peer3;
        try{

            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 5000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 5000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 5000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 5000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};

            for (String room:rooms) {
                assertTrue(peer0.createRoom(room));
                for (AnonymousChatImpl peer:peers) {
                    assertFalse(peer.createRoom(room));
                }
            }

            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.leaveNetwork());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Test
    void testCase_JoinRoom(){
        AnonymousChatImpl peer0, peer1, peer2, peer3;

        try {
            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 6000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 6000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 6000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 6000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};
            String[] non_existing_rooms = {"TEST_ROOM_99", "TEST_ROOM_999", "TEST_ROOM_9999", "TEST_ROOM_99999"};

            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
            }

            for (AnonymousChatImpl peer:peers) {
                for (String room:rooms) {
                    assertTrue(peer.joinRoom(room));
                }
            }

            for (AnonymousChatImpl peer:peers) {
                for (String room:non_existing_rooms) {
                    assertFalse(peer.joinRoom(room));
                }
            }

            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.leaveNetwork());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Test
    void testCase_SendMessage(){
        AnonymousChatImpl peer0, peer1, peer2, peer3;

        try{
            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 7000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 7000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 7000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 7000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};
            String[] non_existing_rooms = {"TEST_ROOM_99", "TEST_ROOM_999", "TEST_ROOM_9999", "TEST_ROOM_99999"};

            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
            }

            for (AnonymousChatImpl peer:peers) {
                for (String room:rooms) {
                    if(Arrays.asList(peers).indexOf(peer) != Arrays.asList(rooms).indexOf(room)){
                        assertAll("Sending message in existing room",
                            ()->assertFalse(peer.sendMessage(room, "THIS MESSAGE SHOULDN'T BE SENT from: "+peer.hashCode()+" in room: "+room)),
                            ()->assertTrue(peer.joinRoom(room)),
                            ()->assertTrue(peer.sendMessage(room, "Message from: "+peer.hashCode()+" in room: "+room))
                        );
                    }else
                        assertTrue(peer.sendMessage(room, "Message from: "+peer.hashCode()+" in room: "+room));
                }
                for (String room:non_existing_rooms){
                    assertAll("Sending message in non existing room",
                        ()->assertFalse(peer.sendMessage(room, "THIS MESSAGE SHOULDN'T BE SENT from: "+peer.hashCode()+" in room: "+room)),
                        ()->assertFalse(peer.joinRoom(room)),
                        ()->assertFalse(peer.sendMessage(room, "THIS MESSAGE SHOULDN'T BE SENT from: "+peer.hashCode()+" in room: "+room))
                    );
                }
            }

            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.leaveNetwork());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Test
    void testCase_LeaveRoom(){

        AnonymousChatImpl peer0, peer1, peer2, peer3;

        try {
            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 8000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 8000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 8000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 8000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};

            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
            }

            for (AnonymousChatImpl peer:peers) {
                for (String room:rooms) {
                    assertTrue(peer.joinRoom(room));
                }
            }

            for (AnonymousChatImpl peer:peers) {
                for (String room:rooms) {
                    assertTrue(peer.leaveRoom(room));
                }
            }

            for (AnonymousChatImpl peer:peers) {
                for (String room:rooms) {
                    assertFalse(peer.leaveRoom(room));
                }
            }

            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.leaveNetwork());
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
