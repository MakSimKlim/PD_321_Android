package ru.mkits.mygpsweatherapp;

import java.util.List;
public class ChatResponseAI {
    public List<Choise>choices;

    public class Choise{
        public Message message;
    }

    public static class Message{
        String role;
        String content;
    }
}