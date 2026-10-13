package com.vendo.chat_service.adapter.security.out.jwt;

import com.vendo.core_lib.types.ServiceName;

public interface InternalTokenGenerationPort {

    String generate(ServiceName audience);

}
