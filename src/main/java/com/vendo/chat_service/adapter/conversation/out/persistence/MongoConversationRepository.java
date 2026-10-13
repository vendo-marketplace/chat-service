package com.vendo.chat_service.adapter.conversation.out.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MongoConversationRepository extends CrudRepository<MongoConversation, String> {

    Optional<MongoConversation> findByProductIdAndSellerIdAndBuyerId(String productId, String sellerId, String buyerId);

}
