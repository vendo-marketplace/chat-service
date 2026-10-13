package com.vendo.chat_service.port.product;

import com.vendo.chat_service.domain.product.Product;

public interface ProductQueryPort {

    Product findById(String id);

}
