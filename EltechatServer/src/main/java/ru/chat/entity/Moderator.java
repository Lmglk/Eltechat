package ru.chat.entity;

import ru.chat.server.SocketServer;
import ru.chat.utils.JSONUtils;

import javax.websocket.Session;

public class Moderator extends User {

    public Moderator(String name, Session session) {
        super(name, session);
    }

    public void muteUser(User user) {
        if (user != null && !(user instanceof Moderator)) {
            user.setMute(true);
            SocketServer.sendJSON(user.getSession(), JSONUtils.muteUser(user.getName()));
        }
    }

    public void unmuteUser(User user) {
        if (user != null && !(user instanceof Moderator))
            user.setMute(false);
    }

    public void deleteMessage(Long messageId) {
        SocketServer.sendJSONtoAll(JSONUtils.deleteMessage(messageId));
    }
}
