package com.rhotels.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseChatMemoryConfig {
    
    // Khởi tạo Bean ChatMemory kết nối database
    @Bean
    public ChatMemory jdbcChatMemory(JdbcTemplate jdbcTemplate) {
        // (Giả định lớp JdbcChatMemory đã được team tự implement interface ChatMemory của Spring AI 
        // sử dụng JdbcTemplate để INSERT/SELECT vào bảng chat_history)
        // Trong thực tế, bạn có thể implement nó bằng cách map List<Message> sang JSON để lưu MySQL.
        return new JdbcChatMemory(jdbcTemplate);
    }
}

// Dummy class to represent the concept
class JdbcChatMemory implements ChatMemory {
    public JdbcChatMemory(JdbcTemplate jdbcTemplate) { }
    // Implement methods from ChatMemory interface (add, get, clear, etc.)
    @Override
    public void add(String conversationId, java.util.List<org.springframework.ai.chat.messages.Message> messages) {}
    @Override
    public java.util.List<org.springframework.ai.chat.messages.Message> get(String conversationId, int lastN) { return java.util.List.of(); }
    @Override
    public void clear(String conversationId) {}
}
