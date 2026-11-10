package com.vendo.chat_service.adapter.user.out;

import com.vendo.chat_service.adapter.user.out.config.UserFeignConfig;
import com.vendo.chat_service.domain.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(
        name = "user-service",
        path = "/internal/users",
        configuration = UserFeignConfig.class)
public interface UserClient {

    @GetMapping(params = "id")
    User getById(@RequestParam("id") String id);

}
