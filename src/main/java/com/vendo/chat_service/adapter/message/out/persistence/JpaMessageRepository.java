package com.vendo.chat_service.adapter.message.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

public interface JpaMessageRepository extends ListPagingAndSortingRepository<JpaMessage, String>, CrudRepository<JpaMessage, String> {

    Page<JpaMessage> findAllByConversation_IdOrderByCreatedAtAsc(String conversationId, Pageable pageable);

    void deleteAllByConversation_Id(String conversationId);
}
