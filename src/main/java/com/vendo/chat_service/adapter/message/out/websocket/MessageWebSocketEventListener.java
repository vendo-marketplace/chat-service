package com.vendo.chat_service.adapter.message.out.websocket;

import com.vendo.chat_service.domain.message.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MessageWebSocketEventListener {

    @Value("${websocket.conversation.path}")
    private String conversationPath;

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent event) {
        messagingTemplate.convertAndSend(
                conversationPath + event.conversationId(),
                event
        );
    }
}
