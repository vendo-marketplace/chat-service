package com.vendo.chat_service.adapter.product.out.config;

import com.vendo.chat_service.adapter.product.out.exception.ProductServiceErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new ProductServiceErrorDecoder();
    }

}
