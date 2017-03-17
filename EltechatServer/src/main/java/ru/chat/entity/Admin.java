package ru.chat.entity;

import ru.chat.server.SocketServer;
import ru.chat.utils.JSONUtils;

import javax.websocket.Session;

public class Admin extends Moderator {

    public Admin(String name, Session session) {
        super(name, session);
    }

    public void kickUser(User user) {
        if (user != null && !(user instanceof Admin)) {
            user.disconnect();
            SocketServer.sendJSON(user.getSession(), JSONUtils.kickUser(user.getName()));
        }
    }
}
