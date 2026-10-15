package com.vendo.chat_service.adapter.conversation.out.persistence;

import com.vendo.chat_service.adapter.conversation.out.mapper.ConversationMapper;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.conversation.exception.ConversationNotFoundException;
import com.vendo.chat_service.port.conversation.ConversationCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaConversationCommandAdapter implements ConversationCommandPort {

    private final ConversationMapper mapper;
    private final JpaConversationRepository repository;

    @Override
    public String save(Conversation conversation) {
        return repository.save(mapper.toEntity(conversation)).getId();
    }

    @Override
    public void update(String id, Conversation conversation) {
        JpaConversation entity = findById(id);
        mapper.updateEntity(entity, conversation);
        repository.save(entity);
    }

    @Override
    public void delete(String id) {
        repository.delete(findById(id));
    }

    private JpaConversation findById(String id) {
        return repository.findById(id).orElseThrow(() -> new ConversationNotFoundException("Conversation not found."));
    }
}
