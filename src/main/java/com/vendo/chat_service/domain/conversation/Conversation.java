package com.vendo.chat_service.domain.conversation;

import java.time.Instant;

public record Conversation(

        String id,

        String productId,
        String sellerId,
        String buyerId,
        String lastMessage,

        Instant createdAt

) {
}
