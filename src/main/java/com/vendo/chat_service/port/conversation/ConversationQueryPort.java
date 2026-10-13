package com.vendo.chat_service.port.conversation;

public interface ConversationQueryPort {

    boolean exists(String productId, String sellerId, String buyerId);

}
