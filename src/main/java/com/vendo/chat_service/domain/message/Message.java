package com.vendo.chat_service.domain.message;

import java.time.Instant;

public record Message(

        String id,

        String conversationId,
        String senderId,
        String content,

        Instant createdAt

) {
}
