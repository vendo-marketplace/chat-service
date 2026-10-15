package com.vendo.chat_service.adapter.conversation.in.dto;

import com.vendo.chat_service.domain.conversation.Conversation;

import java.util.List;

public record ConversationsResponse(List<Conversation> data) {
}
