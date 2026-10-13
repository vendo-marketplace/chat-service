package com.vendo.chat_service.adapter.conversation.out.mapper;

import com.vendo.chat_service.adapter.conversation.in.dto.ConversationResponse;
import com.vendo.chat_service.application.conversation.command.ConversationCreatedCommand;
import com.vendo.chat_service.infrastructure.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface ConversationDtoMapper {

    ConversationResponse toResponse(ConversationCreatedCommand command);

}
