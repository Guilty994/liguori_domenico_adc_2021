package it.adc.p2p.chat;

public class AnonymousChatImpl implements AnonymousChat{
    @Override
    public boolean createRoom(String _room_name) {
        return false;
    }

    @Override
    public boolean joinRoom(String _room_name) {
        return false;
    }

    @Override
    public boolean leaveRoom(String _room_name) {
        return false;
    }

    @Override
    public boolean sendMessage(String _room_name, String _text_message) {
        return false;
    }
}
