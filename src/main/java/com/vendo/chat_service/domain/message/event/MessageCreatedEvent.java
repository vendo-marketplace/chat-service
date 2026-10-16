package com.vendo.chat_service.domain.message.event;

import java.time.Instant;

public record MessageCreatedEvent(
        String id,
        String conversationId,
        String content,
        Instant createdAt
) {
}
