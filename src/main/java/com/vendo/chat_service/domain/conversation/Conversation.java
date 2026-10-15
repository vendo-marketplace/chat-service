package com.vendo.chat_service.domain.conversation;

import lombok.Builder;

import java.time.Instant;

@Builder
public record Conversation(

        String id,

        String productId,
        String sellerId,
        String buyerId,

        String lastMessage,

        Instant createdAt

) {

    public static Conversation from(String productId, String sellerId, String buyerId, String lastMessage) {
        return new Conversation(null, productId, sellerId, buyerId, lastMessage, null);
    }

}
