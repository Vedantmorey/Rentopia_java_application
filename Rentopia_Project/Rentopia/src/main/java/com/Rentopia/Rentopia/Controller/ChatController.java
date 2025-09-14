package com.Rentopia.Rentopia.Controller;

import com.Rentopia.Rentopia.Entity.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage/{propertyId}")
    @SendTo("/topic/property/{propertyId}")
    public ChatMessage sendMessage(@DestinationVariable String propertyId, @Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser/{propertyId}")
    @SendTo("/topic/property/{propertyId}")
    public ChatMessage addUser(@DestinationVariable String propertyId,
                               @Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // Add username to the WebSocket session attributes
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}