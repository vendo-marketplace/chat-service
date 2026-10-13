package com.vendo.chat_service.adapter.conversation.out.persistence;

import com.vendo.chat_service.adapter.conversation.out.mapper.ConversationMapper;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.port.conversation.ConversationQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class MongoConversationQueryAdapter implements ConversationQueryPort {

    private final ConversationMapper mapper;
    private final MongoConversationRepository repository;

    @Override
    public Optional<Conversation> find(String productId, String sellerId, String buyerId) {
         return repository.findByProductIdAndSellerIdAndBuyerId(productId, sellerId, buyerId).map(mapper::toConversation);
    }

}
