package com.vendo.chat_service.adapter.product.in.exception;

import com.vendo.chat_service.adapter.product.out.exception.ProductServiceUnavailableException;
import com.vendo.chat_service.domain.product.exception.ProductNotFoundException;
import com.vendo.chat_service.domain.product.exception.ProductOwnerException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleProductNotFoundException(ProductNotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(ProductOwnerException.class)
    public ResponseEntity<ExceptionResponse> handleProductOwnerException(ProductOwnerException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(ProductServiceUnavailableException.class)
    public ResponseEntity<ExceptionResponse> handleProductServiceUnavailableException(ProductServiceUnavailableException e, HttpServletRequest request) {
        log.error(e.getMessage());
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Service is unavailable.")
                .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exceptionResponse);
    }

}
