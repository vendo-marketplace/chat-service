package com.vendo.chat_service.adapter.conversation.out.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface JpaConversationRepository extends CrudRepository<JpaConversation, String> {

    Optional<JpaConversation> findByProductIdAndSellerIdAndBuyerId(String productId, String sellerId, String buyerId);

    List<JpaConversation> findAllByBuyerIdOrSellerId(String buyerId, String sellerId);

    @Query(nativeQuery = true, value = """
                SELECT count(*) FROM conversations
                WHERE (seller_id = :userId OR buyer_id = :userId)
                  AND last_message_sender_id <> :userId
                  AND (
                    (seller_id = :userId AND (seller_last_read_message_id IS NULL OR seller_last_read_message_id <> last_message_id))
                    OR
                    (buyer_id = :userId AND (buyer_last_read_message_id IS NULL OR buyer_last_read_message_id <> last_message_id))
                  )
            """)
    int countUnreadMessages(String userId);

}
