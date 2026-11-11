package com.vendo.chat_service.adapter.conversation.in.dto;

import com.vendo.chat_service.adapter.user.in.dto.UserProfileResponse;
import com.vendo.chat_service.domain.conversation.ReadState;
import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record ConversationResponse(

        String id,

        String productId,
        UserProfileResponse seller,
        UserProfileResponse buyer,

        String lastMessage,

        Map<String, ReadState> readStates,

        Instant createdAt

) {
}
