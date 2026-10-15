package com.vendo.chat_service.adapter.message.out.persistence;

import com.vendo.chat_service.adapter.message.out.mapper.MessageMapper;
import com.vendo.chat_service.domain.message.Message;
import com.vendo.chat_service.port.message.MessageQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaMessageQueryAdapter implements MessageQueryPort {

    private final MessageMapper mapper;
    private final JpaMessageRepository repository;

    @Override
    public Page<Message> findAll(String conversationId, Pageable pageable) {
        Page<JpaMessage> entities = repository.findAllByConversation_IdOrderByCreatedAtAsc(conversationId, pageable);
        return mapper.toPageMessages(entities);
    }
    
}
