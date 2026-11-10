package com.vendo.chat_service.adapter.product.out.config;

import com.vendo.chat_service.adapter.product.out.exception.ProductServiceErrorDecoder;
import com.vendo.chat_service.adapter.security.out.jwt.InternalTokenGenerationPort;
import com.vendo.core_lib.types.ServiceName;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

import static com.vendo.security_lib.http.HttpUtils.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.http.HttpUtils.BEARER_PREFIX;

public class ProductFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ProductServiceErrorDecoder();
    }

    @Bean
    public RequestInterceptor internalProductInfoInterceptor(InternalTokenGenerationPort internalTokenGenerationPort) {
        return request -> request.header(
                AUTHORIZATION_HEADER,
                BEARER_PREFIX + internalTokenGenerationPort.generate(ServiceName.PRODUCT_SERVICE)
        );
    }

}
