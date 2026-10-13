package com.vendo.chat_service.port.conversation;

import com.vendo.chat_service.domain.conversation.Conversation;

public interface ConversationCommandPort {

    String save(Conversation conversation);

}
