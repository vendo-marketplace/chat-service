package com.vendo.chat_service.port.user;

import com.vendo.chat_service.domain.user.User;

public interface UserQueryPort {

    User getById(String id);

}
