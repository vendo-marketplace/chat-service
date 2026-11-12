package com.vendo.chat_service.adapter.message.in;

import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.message.Message;
import com.vendo.chat_service.test_utils.AbstractIntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessageControllerIntegrationTest extends AbstractIntegrationTest {

    @Nested
    class Send {

        private ResultActions performSend(String conversationId, String body) throws Exception {
            return mockMvc.perform(post("/conversations/{id}/messages", conversationId)
                    .headers(authHeaders(SELLER))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));
        }

        @Test
        void savesMessageAndUpdatesLastMessage() throws Exception {
            stubConversation("c1", SELLER, BUYER, "first");
            stubSavedMessage("message-id");

            performSend("c1", "{\"message\": \"Yes, it is available\"}")
                    .andExpect(status().isOk());

            ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
            verify(messageCommandPort).save(messageCaptor.capture());
            assertThat(messageCaptor.getValue()).satisfies(message -> {
                assertThat(message.id()).isNull();
                assertThat(message.conversationId()).isEqualTo("c1");
                assertThat(message.senderId()).isEqualTo(SELLER.id());
                assertThat(message.content()).isEqualTo("Yes, it is available");
            });

            ArgumentCaptor<Conversation> conversationCaptor = ArgumentCaptor.forClass(Conversation.class);
            verify(conversationCommandPort).update(eq("c1"), conversationCaptor.capture());
            assertThat(conversationCaptor.getValue()).satisfies(update -> {
                assertThat(update.lastMessage()).isNotNull();
                assertThat(update.lastMessage().id()).isEqualTo("message-id");
                assertThat(update.lastMessage().senderId()).isEqualTo(SELLER.id());
                assertThat(update.lastMessage().content()).isEqualTo("Yes, it is available");
                assertThat(update.lastMessage().createdAt()).isNotNull();
                assertThat(update.sellerId()).isNull();
                assertThat(update.buyerId()).isNull();
                assertThat(update.productId()).isNull();
            });
        }

        @Test
        void returnsValidationErrorWhenMessageIsBlank() throws Exception {
            performSend("c1", "{\"message\": \"   \"}")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed."))
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.errors.message").value("Message is required."));

            verifyNoInteractions(conversationQueryPort, messageCommandPort, conversationCommandPort);
        }

        @Test
        void returnsValidationErrorWhenMessageIsMissing() throws Exception {
            performSend("c1", "{}")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.message").value("Message is required."));

            verifyNoInteractions(conversationQueryPort, messageCommandPort, conversationCommandPort);
        }

        @Test
        void returnsValidationErrorWhenMessageIsTooLong() throws Exception {
            performSend("c1", "{\"message\": \"%s\"}".formatted("a".repeat(4001)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.message").value("Message must not exceed 4000 characters."));

            verifyNoInteractions(conversationQueryPort, messageCommandPort, conversationCommandPort);
        }

        @Test
        void acceptsMessageWithMaxLength() throws Exception {
            stubConversation("c1", SELLER, BUYER, "first");
            stubSavedMessage("message-id");

            performSend("c1", "{\"message\": \"%s\"}".formatted("a".repeat(4000)))
                    .andExpect(status().isOk());

            verify(messageCommandPort).save(any());
        }

        @Test
        void returnsBadRequestWhenBodyIsMalformed() throws Exception {
            performSend("c1", "{not json")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid body structure."));

            verifyNoInteractions(conversationQueryPort, messageCommandPort, conversationCommandPort);
        }

        @Test
        void returnsNotFoundWhenConversationDoesNotExist() throws Exception {
            stubMissingConversation("missing-id");

            performSend("missing-id", "{\"message\": \"Hello\"}")
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Conversation not found."))
                    .andExpect(jsonPath("$.path").value("/conversations/missing-id/messages"));

            verifyNoInteractions(messageCommandPort, conversationCommandPort);
        }

        @Test
        void returnsForbiddenWhenUserIsNotParticipant() throws Exception {
            stubConversation("c1", SELLER, BUYER, "first");

            mockMvc.perform(post("/conversations/{id}/messages", "c1")
                            .headers(authHeaders(STRANGER))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"message\": \"Hello\"}"))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("You're not owner."));

            verifyNoInteractions(messageCommandPort, conversationCommandPort);
        }

        @Test
        void returnsUnauthorizedWithoutAuthHeaders() throws Exception {
            mockMvc.perform(post("/conversations/{id}/messages", "c1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"message\": \"Hello\"}"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(conversationQueryPort, messageCommandPort, conversationCommandPort);
        }

    }

    @Nested
    class FindAll {

        private void stubMessages(List<Message> messages, long total) {
            when(messageQueryPort.findAll(eq("c1"), any())).thenAnswer(invocation ->
                    new PageImpl<>(messages, invocation.<Pageable>getArgument(1), total));
        }

        @Test
        void returnsConversationMessagesPage() throws Exception {
            stubConversation("c1", SELLER, BUYER, "second");
            stubMessages(List.of(
                    new Message("m1", "c1", BUYER.id(), "first", CREATED_AT),
                    new Message("m2", "c1", SELLER.id(), "second", CREATED_AT.plusSeconds(1))
            ), 2);

            mockMvc.perform(get("/conversations/{id}/messages", "c1").headers(authHeaders(BUYER)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id").value("m1"))
                    .andExpect(jsonPath("$.content[0].conversationId").value("c1"))
                    .andExpect(jsonPath("$.content[0].senderId").value(BUYER.id()))
                    .andExpect(jsonPath("$.content[0].content").value("first"))
                    .andExpect(jsonPath("$.content[0].createdAt").value(CREATED_AT.toString()))
                    .andExpect(jsonPath("$.content[1].id").value("m2"))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.size").value(30));
        }

        @Test
        void usesDefaultPagination() throws Exception {
            stubConversation("c1", SELLER, BUYER, "first");
            stubMessages(List.of(), 0);

            mockMvc.perform(get("/conversations/{id}/messages", "c1").headers(authHeaders(BUYER)))
                    .andExpect(status().isOk());

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(messageQueryPort).findAll(eq("c1"), pageableCaptor.capture());
            assertThat(pageableCaptor.getValue())
                    .isEqualTo(PageRequest.of(0, 30, Sort.by(Sort.Direction.DESC, "createdAt")));
        }

        @Test
        void appliesRequestedPagination() throws Exception {
            stubConversation("c1", SELLER, BUYER, "third");
            stubMessages(List.of(new Message("m3", "c1", BUYER.id(), "third", CREATED_AT)), 3);

            mockMvc.perform(get("/conversations/{id}/messages", "c1")
                            .headers(authHeaders(BUYER))
                            .param("page", "1")
                            .param("size", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.totalElements").value(3))
                    .andExpect(jsonPath("$.totalPages").value(2))
                    .andExpect(jsonPath("$.number").value(1));

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(messageQueryPort).findAll(eq("c1"), pageableCaptor.capture());
            assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(1);
            assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(2);
        }

        @Test
        void returnsEmptyPageWhenConversationHasNoMessages() throws Exception {
            stubConversation("c1", SELLER, BUYER, null);
            stubMessages(List.of(), 0);

            mockMvc.perform(get("/conversations/{id}/messages", "c1").headers(authHeaders(SELLER)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        void returnsNotFoundWhenConversationDoesNotExist() throws Exception {
            stubMissingConversation("missing-id");

            mockMvc.perform(get("/conversations/{id}/messages", "missing-id").headers(authHeaders(BUYER)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Conversation not found."));

            verifyNoInteractions(messageQueryPort);
        }

        @Test
        void returnsForbiddenWhenUserIsNotParticipant() throws Exception {
            stubConversation("c1", SELLER, BUYER, "first");

            mockMvc.perform(get("/conversations/{id}/messages", "c1").headers(authHeaders(STRANGER)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("You're not owner."));

            verifyNoInteractions(messageQueryPort);
        }

        @Test
        void returnsUnauthorizedWithoutAuthHeaders() throws Exception {
            mockMvc.perform(get("/conversations/{id}/messages", "c1"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(conversationQueryPort, messageQueryPort);
        }

    }

}
