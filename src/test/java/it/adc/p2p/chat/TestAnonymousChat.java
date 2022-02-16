package it.adc.p2p.chat;

import it.adc.p2p.chat.exceptions.DuplicatePeer;
import it.adc.p2p.chat.exceptions.FailedMasterPeerBootstrap;
import org.junit.jupiter.api.*;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;


public class TestAnonymousChat {


    @Test
    @DisplayName("Duplicate peers")
    void testCase_DuplicatePeers() throws Exception {

        AnonymousChatImpl peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 10000);
        AnonymousChatImpl peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 10000);
        AnonymousChatImpl peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 10000);

        assertThrows(DuplicatePeer.class, ()->new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 10000));
        assertThrows(DuplicatePeer.class, ()->new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 10000));

        AnonymousChatImpl[] peers = {peer0, peer1, peer2};

        // Invoking network closing procedure
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.leaveNetwork());
        }

    }

    @Test
    @DisplayName("Master bootstrapping")
    void testCase_MasterBootstrapping() {

        assertAll("Bootstrapping master to an invalid IP",
                () -> assertThrows(FailedMasterPeerBootstrap.class,
                        () -> new AnonymousChatImpl(0, "1.1.1.1", new MessageListener(0), 9000)),
                () -> assertThrows(FailedMasterPeerBootstrap.class,
                        () -> new AnonymousChatImpl(0, "2.2.2.2", new MessageListener(0), 9000))
        );

        assertAll("Bootstrapping to an invalid master IP",
                () -> assertThrows(FailedMasterPeerBootstrap.class,
                        () -> new AnonymousChatImpl(1, "1.1.1.1", new MessageListener(1), 9000)),
                () -> assertThrows(FailedMasterPeerBootstrap.class,
                        () -> new AnonymousChatImpl(2, "1.1.1.1", new MessageListener(2), 9000)),
                () -> assertThrows(FailedMasterPeerBootstrap.class,
                        () -> new AnonymousChatImpl(3, "1.1.1.1", new MessageListener(3), 9000))
        );

    }


    @Test
    @DisplayName("Creation of room that doesn't exist yet")
    void testCase_CreateNonExistingRoom() throws Exception {


        // Generating peers
        AnonymousChatImpl peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 4000);
        AnonymousChatImpl peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 4000);
        AnonymousChatImpl peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 4000);
        AnonymousChatImpl peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 4000);

        AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
        String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};

        // Each peer create and join a single different room
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
        }

        // Invoking network closing procedure
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.leaveNetwork());
        }

    }

    @Test
    @DisplayName("Creation of rooms that already exist")
    void testCase_CreateExistingRoom() throws Exception {


        // Generating peers
        AnonymousChatImpl peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 5000);
        AnonymousChatImpl peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 5000);
        AnonymousChatImpl peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 5000);
        AnonymousChatImpl peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 5000);

        AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
        String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};

        // peer0 create the rooms and other peers try to create the same rooms
        for (String room : rooms) {
            assertTrue(peer0.createRoom(room));
            for (AnonymousChatImpl peer : peers) {
                assertFalse(peer.createRoom(room));
            }
        }

        // Invoking network closing procedure
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.leaveNetwork());
        }


    }


    @Test
    @DisplayName("Joining existing rooms and joining non-existing rooms")
    void testCase_JoinRoom() throws Exception {


        // Generating peers
        AnonymousChatImpl peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 6000);
        AnonymousChatImpl peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 6000);
        AnonymousChatImpl peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 6000);
        AnonymousChatImpl peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 6000);

        AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
        String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};
        String[] non_existing_rooms = {"TEST_ROOM_99", "TEST_ROOM_999", "TEST_ROOM_9999", "TEST_ROOM_99999"};

        // Each peer create and join a single different room
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
        }

        // Each peer join all the rooms that have been created on the network
        for (AnonymousChatImpl peer : peers) {
            for (String room : rooms) {
                assertTrue(peer.joinRoom(room));
            }
        }

        // Each peer try to join a list of non-existing rooms
        for (AnonymousChatImpl peer : peers) {
            for (String room : non_existing_rooms) {
                assertFalse(peer.joinRoom(room));
            }
        }

        // Invoking network closing procedure
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.leaveNetwork());
        }


    }


    @Test
    @DisplayName("Message sending")
    void testCase_SendMessage() throws Exception {


        // Generating peers
        AnonymousChatImpl peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 7000);
        AnonymousChatImpl peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 7000);
        AnonymousChatImpl peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 7000);
        AnonymousChatImpl peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 7000);

        AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
        String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};
        String[] non_existing_rooms = {"TEST_ROOM_99", "TEST_ROOM_999", "TEST_ROOM_9999", "TEST_ROOM_99999"};

        // Each peer create and join a single different room
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
        }


        for (AnonymousChatImpl peer : peers) {
            // Each peer try to send a message in a room that he hasn't joined yet, then join the room and send another message
            for (String room : rooms) {
                if (Arrays.asList(peers).indexOf(peer) != Arrays.asList(rooms).indexOf(room)) {
                    assertAll("Sending message in existing room",
                            () -> assertFalse(peer.sendMessage(room, "THIS MESSAGE SHOULDN'T BE SENT from: " + peer.hashCode() + " in room: " + room)),
                            () -> assertTrue(peer.joinRoom(room)),
                            () -> assertTrue(peer.sendMessage(room, "Message from: " + peer.hashCode() + " in room: " + room))
                    );
                } else
                    assertTrue(peer.sendMessage(room, "Message from: " + peer.hashCode() + " in room: " + room));
            }
            // Each peer try to send a message in a room that doesn't exist
            for (String room : non_existing_rooms) {
                assertAll("Sending message in non existing room",
                        () -> assertFalse(peer.sendMessage(room, "THIS MESSAGE SHOULDN'T BE SENT from: " + peer.hashCode() + " in room: " + room)),
                        () -> assertFalse(peer.joinRoom(room)),
                        () -> assertFalse(peer.sendMessage(room, "THIS MESSAGE SHOULDN'T BE SENT from: " + peer.hashCode() + " in room: " + room))
                );
            }
        }

        // Invoking network closing procedure
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.leaveNetwork());
        }
    }


    @Test
    @DisplayName("Room leaving procedure")
    void testCase_LeaveRoom() throws Exception {

        // Generating peers
        AnonymousChatImpl peer0 = new AnonymousChatImpl(0, "127.0.0.1", new MessageListener(0), 8000);
        AnonymousChatImpl peer1 = new AnonymousChatImpl(1, "127.0.0.1", new MessageListener(1), 8000);
        AnonymousChatImpl peer2 = new AnonymousChatImpl(2, "127.0.0.1", new MessageListener(2), 8000);
        AnonymousChatImpl peer3 = new AnonymousChatImpl(3, "127.0.0.1", new MessageListener(3), 8000);

        AnonymousChatImpl[] peers = {peer0, peer1, peer2, peer3};
        String[] rooms = {"TEST_ROOM_0", "TEST_ROOM_1", "TEST_ROOM_2", "TEST_ROOM_3"};

        // Each peer create and join a single different room
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.createRoom(rooms[Arrays.asList(peers).indexOf(peer)]));
        }

        // Each peer join all the rooms that have been created on the network
        for (AnonymousChatImpl peer : peers) {
            for (String room : rooms) {
                assertTrue(peer.joinRoom(room));
            }
        }

        // Each peer leave all the room they joined
        for (AnonymousChatImpl peer : peers) {
            for (String room : rooms) {
                assertTrue(peer.leaveRoom(room));
            }
        }

        // Each peer try to leave all the room they joined and left
        for (AnonymousChatImpl peer : peers) {
            for (String room : rooms) {
                assertFalse(peer.leaveRoom(room));
            }
        }

        // Invoking network closing procedure
        for (AnonymousChatImpl peer : peers) {
            assertTrue(peer.leaveNetwork());
        }

    }
}
