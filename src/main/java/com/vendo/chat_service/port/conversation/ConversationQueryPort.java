package com.vendo.chat_service.port.conversation;

import com.vendo.chat_service.domain.conversation.Conversation;

import java.util.List;
import java.util.Optional;

public interface ConversationQueryPort {

    Optional<Conversation> find(String productId, String sellerId, String buyerId);
    Conversation findById(String id);
    List<Conversation> findAll(String userId);

}
