package com.vendo.chat_service.adapter.conversation.in.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateConversationRequest(
        @NotBlank(message = "First message is required.")
        String firstMessage,
        @NotBlank(message = "Product ID is required.")
        String productId
) {
}
