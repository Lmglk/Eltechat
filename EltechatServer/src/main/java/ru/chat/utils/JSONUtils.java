package ru.chat.utils;

import org.json.JSONObject;
import ru.chat.server.SocketServer;

public abstract class JSONUtils {

    private static final String FLAG_LOGIN_SUCCESS = "loginSuccess",
            FLAG_LOGIN_FAILURE_NICKNAME = "loginFailureNickname",
            FLAG_LOGIN_FAILURE_SERVER = "loginServerFailure",
            FLAG_NEW_USER = "newUserConnect",
            FLAG_LEFT_USER = "userDisconnect",
            FLAG_MESSAGE = "message",
            FLAG_KICK = "kick",
            FLAG_MUTE = "mute",
            FLAG_DELETE_MESSAGE = "deleteMessage";

    public JSONUtils() {
    }

    public static String loginSucess() {
        String json;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("flag", FLAG_LOGIN_SUCCESS);
        json = jsonObject.toString();

        return json;
    }

    public static String loginFailureNickname() {
        String json;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("flag", FLAG_LOGIN_FAILURE_NICKNAME);
        json = jsonObject.toString();

        return json;
    }

    public static String loginServerFailure() {
        String json;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("flag", FLAG_LOGIN_FAILURE_SERVER);
        json = jsonObject.toString();

        return json;
    }

    public static String connectUser(String name) {
        String json;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("flag", FLAG_NEW_USER);
        jsonObject.put("name", name);
        jsonObject.put("online", SocketServer.getOnline());
        json = jsonObject.toString();

        return json;
    }

    public static String disconnectUser(String name) {
        String json;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("flag", FLAG_LEFT_USER);
        jsonObject.put("name", name);
        jsonObject.put("online", SocketServer.getOnline());
        json = jsonObject.toString();

        return json;
    }

    public static String sendMessage(Long messageId, String name, String message) {
        String json;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("flag", FLAG_MESSAGE);
        jsonObject.put("messageId", messageId);
        jsonObject.put("name", name);
        jsonObject.put("message", message);
        json = jsonObject.toString();

        return json;
    }

    public static String kickUser(String name) {
        String json;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("flag", FLAG_KICK);
        jsonObject.put("name", name);
        json = jsonObject.toString();

        return json;
    }

    public static String muteUser(String name) {
        String json;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("flag", FLAG_MUTE);
        jsonObject.put("name", name);
        json = jsonObject.toString();

        return json;
    }

    public static String deleteMessage(Long messageId) {
        String json;
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("flag", FLAG_DELETE_MESSAGE);
        jsonObject.put("messageId", messageId);
        json = jsonObject.toString();

        return json;
    }
}