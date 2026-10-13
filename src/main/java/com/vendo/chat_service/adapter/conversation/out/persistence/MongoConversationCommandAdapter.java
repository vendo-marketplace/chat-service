package com.vendo.chat_service.adapter.conversation.out.persistence;

import com.vendo.chat_service.adapter.conversation.out.mapper.ConversationMapper;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.port.conversation.ConversationCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoConversationCommandAdapter implements ConversationCommandPort {

    private final ConversationMapper mapper;
    private final MongoConversationRepository repository;

    @Override
    public String save(Conversation conversation) {
        return repository.save(mapper.toEntity(conversation)).getId();
    }
}
