package com.vendo.chat_service.adapter.message.out.mapper;

import com.vendo.chat_service.adapter.message.out.persistence.JpaMessage;
import com.vendo.chat_service.domain.message.Message;
import com.vendo.chat_service.infrastructure.MapStructConfig;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(config = MapStructConfig.class)
public interface MessageMapper {

    @Mapping(target = "conversation", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    JpaMessage toEntity(Message message);

    @BeanMapping(ignoreUnmappedSourceProperties = "updatedAt")
    @Mapping(target = "conversationId", source = "conversation.id")
    Message toMessage(JpaMessage entity);

    default Page<Message> toPageMessages(Page<JpaMessage> pageEntity) {
        return pageEntity.map(this::toMessage);
    }


}
