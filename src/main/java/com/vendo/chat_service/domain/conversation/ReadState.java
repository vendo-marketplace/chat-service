package com.vendo.chat_service.domain.conversation;

import java.time.Instant;

public record ReadState(
        String lastReadMessageId,
        Instant lastReadAt
) {
}
