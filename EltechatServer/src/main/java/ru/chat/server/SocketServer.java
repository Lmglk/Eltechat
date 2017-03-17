package ru.chat.server;

import com.google.common.collect.Maps;
import org.json.JSONObject;
import ru.chat.entity.Admin;
import ru.chat.entity.Moderator;
import ru.chat.entity.User;
import ru.chat.utils.JSONUtils;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ServerEndpoint("/chat")
public class SocketServer {
    private static final ArrayList<User> users = new ArrayList<>();
    private static final HashMap<String, String> admins = new HashMap<>();
    private static final HashMap<String, String> moderator = new HashMap<>();

    private boolean deleteUser = true;

    static {
        admins.put("komdosh", "202cb962ac59075b964b07152d234b70");
        admins.put("megalok", "202cb962ac59075b964b07152d234b70");

        moderator.put("moder", "202cb962ac59075b964b07152d234b70");
    }

    private static Map<String, String> getQueryMap(String query) {
        Map<String, String> map = Maps.newHashMap();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] nameval = param.split("=");
                map.put(nameval[0], nameval[1]);
            }
        }
        return map;
    }

    public static void sendJSONtoAll(String json) {
        users.forEach(user -> {
            try {
                user.getSession().getBasicRemote().sendText(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void sendJSON(Session session, String json) {
        try {
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getOnline() {
        return users.size();
    }

    public static void removeUser(User user) {
        users.remove(user);
    }

    @OnOpen
    public void onOpen(Session session) {
        Map<String, String> queryParams = getQueryMap(session.getQueryString());

        String tempName = null;
        String tempPassword = null;

        try {
            if (queryParams.containsKey("name") && !queryParams.get("name").equals("null"))
                tempName = URLDecoder.decode(queryParams.get("name"), "UTF-8");
            if (queryParams.containsKey("password") && !queryParams.get("password").equals("null"))
                tempPassword = URLDecoder.decode(queryParams.get("password"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final String name = tempName;
        final String password = tempPassword;

        if ((name != null)) {
            if (users.parallelStream().noneMatch(user -> user.getName().toLowerCase().equals(name.toLowerCase()))) {
                if (password != null) {
                    if (admins.containsKey(name.toLowerCase()) && admins.get(name.toLowerCase()).equals(password)) {
                        Admin user = new Admin(name, session);
                        users.add(user);
                        user.connect();
                    } else if (moderator.containsKey(name.toLowerCase()) && moderator.get(name.toLowerCase()).equals(password)) {
                        Moderator moderator = new Moderator(name, session);
                        users.add(moderator);
                        moderator.connect();
                    } else {
                        deleteUser = false;
                        sendJSON(session, JSONUtils.loginFailureNickname());
                    }
                } else {
                    if (!admins.containsKey(name.toLowerCase()) && !moderator.containsKey(name.toLowerCase())) {
                        User user = new User(name, session);
                        users.add(user);
                        user.connect();
                    } else {
                        deleteUser = false;
                        sendJSON(session, JSONUtils.loginFailureNickname());
                    }
                }
            } else
                sendJSON(session, JSONUtils.loginFailureNickname());
        } else
            sendJSON(session, JSONUtils.loginServerFailure());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        User user = users.parallelStream()
                .filter(tempUser -> tempUser.getSession().equals(session))
                .findFirst()
                .orElse(null);
        if (user != null) {
            JSONObject jsonObject = new JSONObject(message);
            switch (jsonObject.getString("flag")) {
                case "message":
                    user.sendMessage(jsonObject.getString("message"));
                    break;
                case "mute":
                    if (user instanceof Moderator) {
                        User muteUser = users.parallelStream()
                                .filter(tempUser -> tempUser.getName().equals(jsonObject.getString("name")))
                                .findFirst()
                                .orElse(null);
                        ((Moderator) user).muteUser(muteUser);
                    }
                    break;
                case "unmute":
                    if (user instanceof Moderator) {
                        User unmuteUser = users.parallelStream()
                                .filter(tempUser -> tempUser.getName().equals(jsonObject.getString("name")))
                                .findFirst()
                                .orElse(null);
                        ((Moderator) user).unmuteUser(unmuteUser);
                    }
                    break;
                case "deleteMessage":
                    if (user instanceof Moderator)
                        ((Moderator) user).deleteMessage(jsonObject.getLong("messageId"));
                    break;
                case "kick":
                    if (user instanceof Admin) {
                        User removeUser = users.parallelStream()
                                .filter(tempUser -> tempUser.getName().equals(jsonObject.getString("name")))
                                .findFirst()
                                .orElse(null);
                        ((Admin) user).kickUser(removeUser);
                    }
                    break;
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (deleteUser) {
            User user = users.parallelStream()
                    .filter(tempUser -> tempUser.getSession().equals(session))
                    .findFirst()
                    .orElse(null);
            user.disconnect();
        }
    }
}