package br.dev.danielrl.server.protocol;

import com.google.gson.Gson;

public class Message {
    private String verb;
    private String path;
    private Gson content;

    public Message(String verb, String path, Gson content) {
        this.verb = verb;
        this.path = path;
        this.content = content;
    }

    public Message fromString(String messageString) {
        return new Gson().fromJson(messageString, Message.class);
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(verb).append(" ").append(path).append("\n");
        sb.append(content.toString());
        return sb.toString();
    }
    
}
