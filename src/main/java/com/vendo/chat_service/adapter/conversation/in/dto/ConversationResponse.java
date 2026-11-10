package com.vendo.chat_service.adapter.conversation.in.dto;

import com.vendo.chat_service.adapter.user.in.dto.UserProfileResponse;
import lombok.Builder;

import java.time.Instant;

@Builder
public record ConversationResponse(

        String id,

        String productId,
        UserProfileResponse seller,
        UserProfileResponse buyer,

        String lastMessage,

        Instant createdAt

) {
}
