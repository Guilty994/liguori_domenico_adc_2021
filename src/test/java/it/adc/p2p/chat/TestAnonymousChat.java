package it.adc.p2p.chat;


import org.junit.jupiter.api.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAnonymousChat {

    @Test
    @DisplayName("Testing creation of room that doesn't exist yet")
    void testCase_CreateNonExistingRoom() {
        AnonymousChatImpl peer0, peer1, peer2, peer3;

        try{
            // Generating peers
            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 4000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 4000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 4000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 4000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};

            // Each peer create and join a single different room
            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
            }

            // Invoking network closing procedure
            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.leaveNetwork());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Testing creation of rooms that already exist")
    void testCase_CreateExistingRoom(){
        AnonymousChatImpl peer0, peer1, peer2, peer3;
        try{
            // Generating peers
            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 5000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 5000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 5000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 5000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};

            // peer0 create the rooms and other peers try to create the same rooms
            for (String room:rooms) {
                assertTrue(peer0.createRoom(room));
                for (AnonymousChatImpl peer:peers) {
                    assertFalse(peer.createRoom(room));
                }
            }

            // Invoking network closing procedure
            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.leaveNetwork());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("Testing joining existing rooms and joining non-existing rooms")
    void testCase_JoinRoom(){
        AnonymousChatImpl peer0, peer1, peer2, peer3;

        try {
            // Generating peers
            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 6000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 6000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 6000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 6000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};
            String[] non_existing_rooms = {"TEST_ROOM_99", "TEST_ROOM_999", "TEST_ROOM_9999", "TEST_ROOM_99999"};

            // Each peer create and join a single different room
            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
            }

            // Each peer join all the rooms that have been created on the network
            for (AnonymousChatImpl peer:peers) {
                for (String room:rooms) {
                    assertTrue(peer.joinRoom(room));
                }
            }

            // Each peer try to join a list of non-existing rooms
            for (AnonymousChatImpl peer:peers) {
                for (String room:non_existing_rooms) {
                    assertFalse(peer.joinRoom(room));
                }
            }

            // Invoking network closing procedure
            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.leaveNetwork());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Test
    @DisplayName("Testing message sending")
    void testCase_SendMessage(){
        AnonymousChatImpl peer0, peer1, peer2, peer3;

        try{
            // Generating peers
            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 7000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 7000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 7000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 7000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};
            String[] non_existing_rooms = {"TEST_ROOM_99", "TEST_ROOM_999", "TEST_ROOM_9999", "TEST_ROOM_99999"};

            // Each peer create and join a single different room
            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
            }


            for (AnonymousChatImpl peer:peers) {
                // Each peer try to send a message in a room that he hasn't joined yet, then join the room and send another message
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
                // Each peer try to send a message in a room that doesn't exist
                for (String room:non_existing_rooms){
                    assertAll("Sending message in non existing room",
                        ()->assertFalse(peer.sendMessage(room, "THIS MESSAGE SHOULDN'T BE SENT from: "+peer.hashCode()+" in room: "+room)),
                        ()->assertFalse(peer.joinRoom(room)),
                        ()->assertFalse(peer.sendMessage(room, "THIS MESSAGE SHOULDN'T BE SENT from: "+peer.hashCode()+" in room: "+room))
                    );
                }
            }

            // Invoking network closing procedure
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
            // Generating peers
            peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 8000);
            peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 8000);
            peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 8000);
            peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 8000);

            AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
            String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};

            // Each peer create and join a single different room
            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
            }

            // Each peer join all the rooms that have been created on the network
            for (AnonymousChatImpl peer:peers) {
                for (String room:rooms) {
                    assertTrue(peer.joinRoom(room));
                }
            }

            // Each peer leave all the room they joined
            for (AnonymousChatImpl peer:peers) {
                for (String room:rooms) {
                    assertTrue(peer.leaveRoom(room));
                }
            }

            // Each peer try to leave all the room they joined and left
            for (AnonymousChatImpl peer:peers) {
                for (String room:rooms) {
                    assertFalse(peer.leaveRoom(room));
                }
            }

            // Invoking network closing procedure
            for (AnonymousChatImpl peer:peers) {
                assertTrue(peer.leaveNetwork());
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
