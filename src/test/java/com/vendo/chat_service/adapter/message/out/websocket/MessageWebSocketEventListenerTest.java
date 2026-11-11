package com.vendo.chat_service.adapter.message.out.websocket;

import com.vendo.chat_service.domain.conversation.event.MessagesReadEvent;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MessageWebSocketEventListenerTest {

    private final SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
    private final MessageWebSocketEventListener listener = new MessageWebSocketEventListener(messagingTemplate);

    @Test
    void sendsMessagesReadEventToConversationReadTopic() {
        ReflectionTestUtils.setField(listener, "conversationPath", "/topic/conversations/");
        MessagesReadEvent event = MessagesReadEvent.of("c1", "buyer-id", "m10", Instant.parse("2026-11-11T12:00:00Z"));

        listener.onMessagesRead(event);

        verify(messagingTemplate).convertAndSend("/topic/conversations/c1/read", event);
    }

}
