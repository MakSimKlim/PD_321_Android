package ru.mkits.openaiapp;

import java.util.List;
public class ChatResponse {
    public List<Choise>choices;

    public class Choise{
        public Message message;
    }

    public static class Message{
        String role;
        String content;
    }
}

