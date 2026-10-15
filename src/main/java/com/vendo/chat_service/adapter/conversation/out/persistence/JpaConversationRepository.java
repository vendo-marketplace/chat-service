package com.vendo.chat_service.adapter.conversation.out.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface JpaConversationRepository extends CrudRepository<JpaConversation, String> {

    Optional<JpaConversation> findByProductIdAndSellerIdAndBuyerId(String productId, String sellerId, String buyerId);

    List<JpaConversation> findAllByBuyerIdOrSellerId(String buyerId, String sellerId);
}
