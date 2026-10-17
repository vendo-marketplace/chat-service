package com.vendo.chat_service.application.message;

import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.message.Message;
import com.vendo.chat_service.domain.message.event.MessageCreatedEvent;
import com.vendo.chat_service.domain.user.User;
import com.vendo.chat_service.port.conversation.ConversationCommandPort;
import com.vendo.chat_service.port.conversation.ConversationQueryPort;
import com.vendo.chat_service.port.message.MessageCommandPort;
import com.vendo.chat_service.port.message.MessageQueryPort;
import com.vendo.chat_service.port.message.MessageUseCase;
import com.vendo.chat_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService implements MessageUseCase {

    private final MessageQueryPort messageQueryPort;
    private final MessageCommandPort messageCommandPort;

    private final ConversationCommandPort conversationCommandPort;
    private final ConversationQueryPort conversationQueryPort;

    private final AuthUserPort authUserPort;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void send(String conversationId, String content) {
        var user = validateConversationOwner(conversationId);
        Message message = messageCommandPort.save(Message.from(conversationId, user.id(), content));
        conversationCommandPort.update(conversationId, Conversation.builder().lastMessage(content).build());
        eventPublisher.publishEvent(new MessageCreatedEvent(
                message.id(),
                conversationId,
                user.id(),
                message.content(),
                message.createdAt()
        ));
    }

    public Page<Message> findAll(String conversationId, Pageable pageable) {
        validateConversationOwner(conversationId);
        return messageQueryPort.findAll(conversationId, pageable);
    }

    private User validateConversationOwner(String conversationId) {
        var conversation = conversationQueryPort.findById(conversationId);
        return authUserPort.validateAnyOwner(conversation.buyerId(), conversation.sellerId());
    }

}
