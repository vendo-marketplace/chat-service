package com.vendo.chat_service.domain.conversation.event;

import java.time.Instant;

public record MessagesReadEvent(
        String type,
        String conversationId,
        String readerId,
        String lastReadMessageId,
        Instant readAt
) {

    public static final String TYPE = "MESSAGES_READ";

    public static MessagesReadEvent of(String conversationId, String readerId, String lastReadMessageId, Instant readAt) {
        return new MessagesReadEvent(TYPE, conversationId, readerId, lastReadMessageId, readAt);
    }

}
