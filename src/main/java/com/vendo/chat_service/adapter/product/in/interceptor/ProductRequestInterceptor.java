package com.vendo.chat_service.adapter.product.in.interceptor;

import com.vendo.chat_service.adapter.security.out.jwt.InternalTokenGenerationPort;
import com.vendo.core_lib.types.ServiceName;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.vendo.security_lib.http.HttpUtils.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.http.HttpUtils.BEARER_PREFIX;

@Configuration
@RequiredArgsConstructor
public class ProductRequestInterceptor {

    private final InternalTokenGenerationPort internalGenerationPort;

    @Bean
    RequestInterceptor internalProductInfoInterceptor() {
        return request -> request.header(
                AUTHORIZATION_HEADER,
                BEARER_PREFIX + internalGenerationPort.generate(ServiceName.PRODUCT_SERVICE)
        );
    }

}
