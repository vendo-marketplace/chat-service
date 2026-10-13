package com.vendo.chat_service.adapter.product.out.exception;

import com.vendo.chat_service.domain.product.exception.ProductNotFoundException;
import com.vendo.core_lib.types.ServiceName;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductServiceErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        if (HttpStatus.valueOf(response.status()).is5xxServerError()) {
            return new ProductServiceUnavailableException(ServiceName.PRODUCT_SERVICE + " is unavailable.");
        }

        if (HttpStatus.valueOf(response.status()).isSameCodeAs(HttpStatus.NOT_FOUND)) {
            throw new ProductNotFoundException("Product not found.");
        }

        log.error(response.toString());
        return new IllegalArgumentException("Unhandled product exception.");
    }

}
