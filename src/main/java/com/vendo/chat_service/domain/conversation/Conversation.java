package com.vendo.chat_service.domain.conversation;

import com.vendo.chat_service.domain.message.LastMessage;
import lombok.Builder;

import java.time.Instant;

@Builder
public record Conversation(

        String id,

        String productId,
        String sellerId,
        String buyerId,

        LastMessage lastMessage,

        ReadState sellerReadState,
        ReadState buyerReadState,

        Instant createdAt

) {

    public static Conversation from(String productId, String sellerId, String buyerId, LastMessage lastMessage) {
        return Conversation.builder()
                .productId(productId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .lastMessage(lastMessage)
                .build();
    }

    public static Conversation from(String productId, String sellerId, String buyerId) {
        return Conversation.builder()
                .productId(productId)
                .sellerId(sellerId)
                .buyerId(buyerId)
                .build();
    }

    public ReadState readStateOf(String userId) {
        if (userId.equals(buyerId)) {
            return buyerReadState;
        }

        if (userId.equals(sellerId)) {
            return sellerReadState;
        }

        return null;
    }

    public Conversation readStateUpdate(String userId, ReadState readState) {
        if (userId.equals(buyerId)) {
            return Conversation.builder().buyerReadState(readState).build();
        }

        if (userId.equals(sellerId)) {
            return Conversation.builder().sellerReadState(readState).build();
        }

        throw new IllegalArgumentException("User is not a participant of the conversation.");
    }

}
