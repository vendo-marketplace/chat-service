package com.vendo.chat_service.adapter.user.out.config;

import com.vendo.chat_service.adapter.security.out.jwt.InternalTokenGenerationPort;
import com.vendo.chat_service.adapter.user.out.exception.UserServiceErrorDecoder;
import com.vendo.core_lib.types.ServiceName;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import static com.vendo.security_lib.http.HttpUtils.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.http.HttpUtils.BEARER_PREFIX;

public class UserFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new UserServiceErrorDecoder();
    }

    @Bean
    public RequestInterceptor internalUserInfoInterceptor(InternalTokenGenerationPort internalTokenGenerationPort) {
        return request -> request.header(
                AUTHORIZATION_HEADER,
                BEARER_PREFIX + internalTokenGenerationPort.generate(ServiceName.USER_SERVICE)
        );
    }

}
