package com.vendo.chat_service.adapter.conversation.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateConversationRequest(
        @NotBlank(message = "First message is required.")
        @Size(max = 4000, message = "First message must not exceed 4000 characters.")
        String firstMessage,
        @NotBlank(message = "Product ID is required.")
        String productId
) {
}
