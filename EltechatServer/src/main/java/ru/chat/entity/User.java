package ru.chat.entity;

import lombok.Getter;
import lombok.Setter;
import ru.chat.entity.interfaces.UserInterface;
import ru.chat.server.SocketServer;
import ru.chat.utils.JSONUtils;

import javax.websocket.Session;
import java.util.concurrent.atomic.AtomicLong;

public class User implements UserInterface {

    private static final AtomicLong messsageId = new AtomicLong();

    @Getter
    private String name;

    @Getter
    private Session session;

    @Setter
    private boolean mute;

    public User(String name, Session session) {
        this.name = name;
        this.session = session;
        this.mute = false;
    }

    public void sendMessage(String message) {
        if (!mute) {
            SocketServer.sendJSONtoAll(JSONUtils.sendMessage(messsageId.longValue(), name, message));
            messsageId.incrementAndGet();
        } else
            SocketServer.sendJSON(session, JSONUtils.muteUser(name));
    }

    public void connect() {
            SocketServer.sendJSON(session, JSONUtils.loginSucess());
            SocketServer.sendJSONtoAll(JSONUtils.connectUser(name));
    }

    public void disconnect() {
        SocketServer.removeUser(this);
        SocketServer.sendJSONtoAll(JSONUtils.disconnectUser(name));
    }
}
