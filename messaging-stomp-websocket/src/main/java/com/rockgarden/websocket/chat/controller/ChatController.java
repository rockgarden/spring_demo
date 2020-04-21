package com.rockgarden.websocket.chat.controller;

import com.rockgarden.websocket.WebSocketConsts;
import com.rockgarden.websocket.chat.model.ChatMessage;
import com.rockgarden.websocket.chat.model.Greeting;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

/**
 * Controller for sending and receiving messages Created by rockgarden on
 * 01/03/20.
 */
@Controller
public class ChatController {

    /*
     * message with destination /app/chat.sendMessage will be routed to the
     * sendMessage() method
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo(WebSocketConsts.CHAT_SERVER)
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    /*
     * message with destination /app/chat.addUser will be routed to the addUser()
     * method.
     */
    @MessageMapping("/chat.addUser")
    @SendTo(WebSocketConsts.CHAT_SERVER)
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(ChatMessage chatMessage) throws Exception {
        Thread.sleep(500); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(chatMessage.getName()) + "!");
    }

}
