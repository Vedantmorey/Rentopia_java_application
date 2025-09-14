package com.Rentopia.Rentopia.Entity;

public class ChatMessage {
    private String content;
    private String sender;
    private String type;
    private Long propertyId; 


    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

}
