package ru.chat.entity.interfaces;

public interface UserInterface {
    void sendMessage(String message);
    void connect();
    void disconnect();
}
