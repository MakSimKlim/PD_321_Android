package ru.mkits.openaiapp;

import java.util.List;
public class ChatRequest {
    String model;
    List<Message> messages;

    double temp;
    int max_tokens;

    public ChatRequest(String model, List<Message> messages, double temp, int max_tokens) {
        this.model = model;
        this.messages = messages;
        this.temp = temp;
        this.max_tokens = max_tokens;
    }

    public static class Message{
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}

