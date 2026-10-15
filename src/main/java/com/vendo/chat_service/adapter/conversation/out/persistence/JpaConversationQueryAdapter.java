package com.vendo.chat_service.adapter.conversation.out.persistence;

import com.vendo.chat_service.adapter.conversation.out.mapper.ConversationMapper;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.conversation.exception.ConversationNotFoundException;
import com.vendo.chat_service.port.conversation.ConversationQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class JpaConversationQueryAdapter implements ConversationQueryPort {

    private final ConversationMapper mapper;
    private final JpaConversationRepository repository;

    @Override
    public Optional<Conversation> find(String productId, String sellerId, String buyerId) {
         return repository.findByProductIdAndSellerIdAndBuyerId(productId, sellerId, buyerId).map(mapper::toConversation);
    }

    @Override
    public Conversation findById(String id) {
        return repository.findById(id).map(mapper::toConversation)
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found."));
    }

    @Override
    public List<Conversation> findAll(String userId) {
        List<JpaConversation> entities = repository.findAllByBuyerIdOrSellerId(userId, userId);
        return mapper.toConversations(entities);
    }

}
