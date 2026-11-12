package com.vendo.chat_service.domain.message;

import java.time.Instant;

public record LastMessage(
        String id,
        String senderId,
        String content,
        Instant createdAt
) {

    public static LastMessage from(String id, String senderId, String content) {
        return new LastMessage(id, senderId, content, Instant.now());
    }

}
