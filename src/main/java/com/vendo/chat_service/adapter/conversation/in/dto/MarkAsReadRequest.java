package com.vendo.chat_service.adapter.conversation.in.dto;

import jakarta.validation.constraints.NotBlank;

public record MarkAsReadRequest(
        @NotBlank(message = "Last read message ID is required.")
        String lastReadMessageId
) {
}
