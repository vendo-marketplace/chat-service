package com.vendo.chat_service.adapter.conversation.in;

import com.vendo.chat_service.adapter.product.out.exception.ProductServiceUnavailableException;
import com.vendo.chat_service.adapter.user.out.exception.UserServiceUnavailableException;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.domain.conversation.exception.ConversationNotFoundException;
import com.vendo.chat_service.domain.message.Message;
import com.vendo.chat_service.domain.product.Product;
import com.vendo.chat_service.domain.product.exception.ProductNotFoundException;
import com.vendo.chat_service.test_utils.AbstractIntegrationTest;
import com.vendo.user_lib.exception.UserNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConversationControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String PRODUCT_ID = "product-id";

    @Nested
    class Create {

        private ResultActions performCreate(String body) throws Exception {
            return mockMvc.perform(post("/conversations")
                    .headers(authHeaders(BUYER))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body));
        }

        private String body(String firstMessage, String productId) {
            return """
                    {"firstMessage": "%s", "productId": "%s"}
                    """.formatted(firstMessage, productId);
        }

        @Test
        void createsConversationWithFirstMessage() throws Exception {
            when(productClient.findById(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, SELLER.id()));
            when(conversationQueryPort.find(PRODUCT_ID, SELLER.id(), BUYER.id())).thenReturn(Optional.empty());
            when(conversationCommandPort.save(any())).thenReturn("new-conversation-id");

            performCreate(body("Hello, is it available?", PRODUCT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.conversationId").value("new-conversation-id"))
                    .andExpect(jsonPath("$.created").value(true));

            ArgumentCaptor<Conversation> conversationCaptor = ArgumentCaptor.forClass(Conversation.class);
            verify(conversationCommandPort).save(conversationCaptor.capture());
            assertThat(conversationCaptor.getValue()).satisfies(conversation -> {
                assertThat(conversation.id()).isNull();
                assertThat(conversation.productId()).isEqualTo(PRODUCT_ID);
                assertThat(conversation.sellerId()).isEqualTo(SELLER.id());
                assertThat(conversation.buyerId()).isEqualTo(BUYER.id());
                assertThat(conversation.lastMessage()).isEqualTo("Hello, is it available?");
            });

            ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
            verify(messageCommandPort).save(messageCaptor.capture());
            assertThat(messageCaptor.getValue()).satisfies(message -> {
                assertThat(message.conversationId()).isEqualTo("new-conversation-id");
                assertThat(message.senderId()).isEqualTo(BUYER.id());
                assertThat(message.content()).isEqualTo("Hello, is it available?");
            });
        }

        @Test
        void returnsExistingConversationWithoutSaving() throws Exception {
            Conversation existing = conversation("existing-id", SELLER, BUYER, "first");
            when(productClient.findById(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, SELLER.id()));
            when(conversationQueryPort.find(PRODUCT_ID, SELLER.id(), BUYER.id())).thenReturn(Optional.of(existing));

            performCreate(body("Hello again", PRODUCT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.conversationId").value("existing-id"))
                    .andExpect(jsonPath("$.created").value(false));

            verifyNoInteractions(conversationCommandPort, messageCommandPort);
        }

        @Test
        void returnsNotFoundWhenProductDoesNotExist() throws Exception {
            when(productClient.findById(PRODUCT_ID)).thenThrow(new ProductNotFoundException("Product not found."));

            performCreate(body("Hello", PRODUCT_ID))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Product not found."))
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.path").value("/conversations"));

            verifyNoInteractions(conversationCommandPort, messageCommandPort);
        }

        @Test
        void returnsServiceUnavailableWhenProductServiceIsDown() throws Exception {
            when(productClient.findById(PRODUCT_ID)).thenThrow(new ProductServiceUnavailableException("product-service is unavailable."));

            performCreate(body("Hello", PRODUCT_ID))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.message").value("Service is unavailable."))
                    .andExpect(jsonPath("$.code").value(503));

            verifyNoInteractions(conversationCommandPort, messageCommandPort);
        }

        @Test
        void returnsBadRequestWhenUserIsProductOwner() throws Exception {
            when(productClient.findById(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, BUYER.id()));

            performCreate(body("Hello", PRODUCT_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Product owner isn't able to create conversation to itself."))
                    .andExpect(jsonPath("$.code").value(400));

            verifyNoInteractions(conversationCommandPort, messageCommandPort);
        }

        @Test
        void returnsValidationErrorsWhenFieldsAreBlank() throws Exception {
            performCreate(body("  ", ""))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation failed."))
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.errors.firstMessage").value("First message is required."))
                    .andExpect(jsonPath("$.errors.productId").value("Product ID is required."));

            verifyNoInteractions(productClient, conversationCommandPort, messageCommandPort);
        }

        @Test
        void returnsValidationErrorsWhenFieldsAreMissing() throws Exception {
            performCreate("{}")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors", hasKey("firstMessage")))
                    .andExpect(jsonPath("$.errors", hasKey("productId")));

            verifyNoInteractions(productClient, conversationCommandPort, messageCommandPort);
        }

        @Test
        void returnsValidationErrorWhenFirstMessageIsTooLong() throws Exception {
            performCreate(body("a".repeat(4001), PRODUCT_ID))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.firstMessage").value("First message must not exceed 4000 characters."));

            verifyNoInteractions(productClient, conversationCommandPort, messageCommandPort);
        }

        @Test
        void acceptsFirstMessageWithMaxLength() throws Exception {
            when(productClient.findById(PRODUCT_ID)).thenReturn(new Product(PRODUCT_ID, SELLER.id()));
            when(conversationQueryPort.find(PRODUCT_ID, SELLER.id(), BUYER.id())).thenReturn(Optional.empty());
            when(conversationCommandPort.save(any())).thenReturn("new-conversation-id");

            performCreate(body("a".repeat(4000), PRODUCT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.created").value(true));
        }

        @Test
        void returnsBadRequestWhenBodyIsMalformed() throws Exception {
            performCreate("{not json")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid body structure."));

            verifyNoInteractions(productClient, conversationCommandPort, messageCommandPort);
        }

        @Test
        void returnsUnauthorizedWithoutAuthHeaders() throws Exception {
            mockMvc.perform(post("/conversations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body("Hello", PRODUCT_ID)))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(productClient, conversationCommandPort, messageCommandPort);
        }

    }

    @Nested
    class FindAll {

        private ResultActions performFindAll() throws Exception {
            return mockMvc.perform(get("/conversations").headers(authHeaders(BUYER)));
        }

        @Test
        void returnsConversationsWithSellerAndBuyerProfiles() throws Exception {
            when(conversationQueryPort.findAll(BUYER.id()))
                    .thenReturn(List.of(conversation("c1", SELLER, BUYER, "last message")));
            stubUsers(SELLER, BUYER);

            performFindAll()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].id").value("c1"))
                    .andExpect(jsonPath("$.data[0].productId").value("product-c1"))
                    .andExpect(jsonPath("$.data[0].lastMessage").value("last message"))
                    .andExpect(jsonPath("$.data[0].createdAt").value(CREATED_AT.toString()))
                    .andExpect(jsonPath("$.data[0].seller.id").value(SELLER.id()))
                    .andExpect(jsonPath("$.data[0].seller.fullName").value(SELLER.fullName()))
                    .andExpect(jsonPath("$.data[0].buyer.id").value(BUYER.id()))
                    .andExpect(jsonPath("$.data[0].buyer.fullName").value(BUYER.fullName()))
                    .andExpect(jsonPath("$.data[0].sellerId").doesNotExist())
                    .andExpect(jsonPath("$.data[0].buyerId").doesNotExist());
        }

        @Test
        void returnsConversationsWhereUserIsBuyerOrSeller() throws Exception {
            when(conversationQueryPort.findAll(BUYER.id())).thenReturn(List.of(
                    conversation("as-buyer", SELLER, BUYER, "as buyer"),
                    conversation("as-seller", BUYER, STRANGER, "as seller")
            ));
            stubUsers(SELLER, BUYER, STRANGER);

            performFindAll()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[*].id", containsInAnyOrder("as-buyer", "as-seller")))
                    .andExpect(jsonPath("$.data[1].seller.fullName").value(BUYER.fullName()))
                    .andExpect(jsonPath("$.data[1].buyer.fullName").value(STRANGER.fullName()));
        }

        @Test
        void fetchesEachUserOnlyOnce() throws Exception {
            when(conversationQueryPort.findAll(BUYER.id())).thenReturn(List.of(
                    conversation("c1", SELLER, BUYER, "one"),
                    conversation("c2", SELLER, BUYER, "two")
            ));
            stubUsers(SELLER, BUYER);

            performFindAll()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(2)));

            verify(userClient, times(1)).getById(SELLER.id());
            verify(userClient, times(1)).getById(BUYER.id());
        }

        @Test
        void returnsEmptyDataWhenUserHasNoConversations() throws Exception {
            when(conversationQueryPort.findAll(BUYER.id())).thenReturn(List.of());

            performFindAll()
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data", hasSize(0)));

            verifyNoInteractions(userClient);
        }

        @Test
        void returnsNotFoundWhenParticipantDoesNotExist() throws Exception {
            when(conversationQueryPort.findAll(BUYER.id()))
                    .thenReturn(List.of(conversation("c1", SELLER, BUYER, "last message")));
            when(userClient.getById(SELLER.id())).thenThrow(new UserNotFoundException("User not found."));

            performFindAll()
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found."));
        }

        @Test
        void returnsServiceUnavailableWhenUserServiceIsDown() throws Exception {
            when(conversationQueryPort.findAll(BUYER.id()))
                    .thenReturn(List.of(conversation("c1", SELLER, BUYER, "last message")));
            when(userClient.getById(SELLER.id())).thenThrow(new UserServiceUnavailableException("user-service is unavailable."));

            performFindAll()
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.message").value("Service is unavailable."))
                    .andExpect(jsonPath("$.code").value(503));
        }

        @Test
        void returnsUnauthorizedWithoutAuthHeaders() throws Exception {
            mockMvc.perform(get("/conversations"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(conversationQueryPort, userClient);
        }

    }

    @Nested
    class FindById {

        @Test
        void returnsConversationWithSellerAndBuyerProfiles() throws Exception {
            stubConversation("c1", SELLER, BUYER, "last message");
            stubUsers(SELLER, BUYER);

            mockMvc.perform(get("/conversations/{id}", "c1").headers(authHeaders(SELLER)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value("c1"))
                    .andExpect(jsonPath("$.productId").value("product-c1"))
                    .andExpect(jsonPath("$.lastMessage").value("last message"))
                    .andExpect(jsonPath("$.createdAt").value(CREATED_AT.toString()))
                    .andExpect(jsonPath("$.seller.id").value(SELLER.id()))
                    .andExpect(jsonPath("$.seller.fullName").value(SELLER.fullName()))
                    .andExpect(jsonPath("$.buyer.id").value(BUYER.id()))
                    .andExpect(jsonPath("$.buyer.fullName").value(BUYER.fullName()))
                    .andExpect(jsonPath("$.sellerId").doesNotExist())
                    .andExpect(jsonPath("$.buyerId").doesNotExist());
        }

        @Test
        void returnsNotFoundWhenConversationDoesNotExist() throws Exception {
            stubMissingConversation("missing-id");

            mockMvc.perform(get("/conversations/{id}", "missing-id").headers(authHeaders(SELLER)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Conversation not found."))
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.path").value("/conversations/missing-id"));

            verifyNoInteractions(userClient);
        }

        @Test
        void returnsForbiddenWhenUserIsNotParticipant() throws Exception {
            stubConversation("c1", SELLER, BUYER, "last message");

            mockMvc.perform(get("/conversations/{id}", "c1").headers(authHeaders(STRANGER)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("You're not owner."))
                    .andExpect(jsonPath("$.code").value(403));

            verifyNoInteractions(userClient);
        }

        @Test
        void returnsUnauthorizedWithoutAuthHeaders() throws Exception {
            mockMvc.perform(get("/conversations/{id}", "c1"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(conversationQueryPort, userClient);
        }

    }

    @Nested
    class Delete {

        @Test
        void deletesMessagesThenConversation() throws Exception {
            stubConversation("c1", SELLER, BUYER, "last message");

            mockMvc.perform(delete("/conversations/{id}", "c1").headers(authHeaders(BUYER)))
                    .andExpect(status().isOk());

            var order = inOrder(messageCommandPort, conversationCommandPort);
            order.verify(messageCommandPort).deleteAll("c1");
            order.verify(conversationCommandPort).delete("c1");
        }

        @Test
        void returnsNotFoundWhenConversationDoesNotExist() throws Exception {
            stubMissingConversation("missing-id");

            mockMvc.perform(delete("/conversations/{id}", "missing-id").headers(authHeaders(BUYER)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Conversation not found."));

            verifyNoInteractions(messageCommandPort, conversationCommandPort);
        }

        @Test
        void returnsNotFoundWhenConversationRemovedDuringDelete() throws Exception {
            stubConversation("c1", SELLER, BUYER, "last message");
            doThrow(new ConversationNotFoundException("Conversation not found."))
                    .when(conversationCommandPort).delete("c1");

            mockMvc.perform(delete("/conversations/{id}", "c1").headers(authHeaders(BUYER)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Conversation not found."));
        }

        @Test
        void returnsForbiddenWhenUserIsNotParticipant() throws Exception {
            stubConversation("c1", SELLER, BUYER, "last message");

            mockMvc.perform(delete("/conversations/{id}", "c1").headers(authHeaders(STRANGER)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("You're not owner."));

            verifyNoInteractions(messageCommandPort, conversationCommandPort);
        }

        @Test
        void returnsUnauthorizedWithoutAuthHeaders() throws Exception {
            mockMvc.perform(delete("/conversations/{id}", "c1"))
                    .andExpect(status().isUnauthorized());

            verifyNoInteractions(conversationQueryPort, messageCommandPort, conversationCommandPort);
        }

    }

}
