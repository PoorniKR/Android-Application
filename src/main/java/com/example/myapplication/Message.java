package com.example.myapplication;

public class Message {
    private String messageId;
    private String messageText;
    private String senderUsername;
    private String receiverUsername;

    public Message() {
        // Required default constructor for Firebase
    }

    public Message(String messageId, String messageText, String senderUsername, String receiverUsername) {
        this.messageId = messageId;
        this.messageText = messageText;
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }
}
