package com.Rentopia.Rentopia.Entity;

public class ChatMessage {
    private String content;
    private String sender;
    private String type;
    private Long propertyId; // <-- ADD THIS FIELD

    // --- Getters and Setters ---
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // ADD GETTER AND SETTER FOR propertyId
    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
}