package com.rhotels.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    private final ChatClient chatClient;
 
    public BookingController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                // Gắn cố định MessageChatMemoryAdvisor với Bean Database Chat Memory
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                .build();
    }
 
    @GetMapping("/chat")
    public Map<String, String> chat(
            // Đón conversationId từ client. Request đầu tiên có thể để trống.
            @RequestParam(required = false) String conversationId,
            @RequestParam String message) {
        
        // Logic sinh Session ID phòng thủ
        String activeSessionId = (conversationId == null || conversationId.isBlank()) 
                                 ? UUID.randomUUID().toString() 
                                 : conversationId;
        
        // Gọi ChatClient và tiêm ID phiên chat
        String aiResponse = this.chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, activeSessionId))
                .call()
                .content();
                
        // Trả về cả câu trả lời của AI và mã Session để Frontend lưu lại cho lượt gọi tiếp theo
        return Map.of(
                "conversationId", activeSessionId,
                "reply", aiResponse
        );
    }
}
