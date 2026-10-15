package com.vendo.chat_service.adapter.conversation.out.mapper;

import com.vendo.chat_service.adapter.conversation.out.persistence.JpaConversation;
import com.vendo.chat_service.domain.conversation.Conversation;
import com.vendo.chat_service.infrastructure.MapStructConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ConversationMapper {

    @BeanMapping(ignoreUnmappedSourceProperties = {"messages", "updatedAt"})
    Conversation toConversation(JpaConversation entity);

    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    JpaConversation toEntity(Conversation conversation);

    List<Conversation> toConversations(List<JpaConversation> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget JpaConversation entity, Conversation conversation);

}
