package com.vendo.chat_service.adapter.message.out.persistence;

import com.vendo.chat_service.adapter.conversation.out.persistence.JpaConversation;
import com.vendo.chat_service.adapter.conversation.out.persistence.JpaConversationRepository;
import com.vendo.chat_service.adapter.message.out.mapper.MessageMapper;
import com.vendo.chat_service.domain.conversation.exception.ConversationNotFoundException;
import com.vendo.chat_service.domain.message.Message;
import com.vendo.chat_service.port.message.MessageCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaMessageCommandAdapter implements MessageCommandPort {

    private final MessageMapper mapper;
    private final JpaMessageRepository repository;
    private final JpaConversationRepository conversationRepository;

    @Override
    public void save(Message message) {
        JpaMessage entity = mapper.toEntity(message);
        JpaConversation conversation = conversationRepository.findById(message.conversationId())
                .orElseThrow(() -> new ConversationNotFoundException("Conversation not found."));
        entity.setConversation(conversation);
        repository.save(entity);
    }

    @Override
    public void deleteAll(String conversationId) {
        repository.deleteAllByConversation_Id(conversationId);
    }
}
