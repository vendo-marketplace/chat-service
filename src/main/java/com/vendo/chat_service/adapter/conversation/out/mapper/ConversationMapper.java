package com.vendo.chat_service.adapter.conversation.out.mapper;

import com.vendo.chat_service.adapter.conversation.out.persistence.MongoConversation;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.infrastructure.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ConversationMapper {

    Conversation toConversation(MongoConversation entity);
    MongoConversation toEntity(Conversation conversation);

}
