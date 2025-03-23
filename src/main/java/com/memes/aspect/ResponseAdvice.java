package com.memes.aspect;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.memes.model.common.ResultData;

import lombok.NonNull;

@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    @SuppressWarnings("null")
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class converterType) {
        return true;
    }

    @SuppressWarnings({"null"})
    @Override
    public Object beforeBodyWrite(Object o,
        @NonNull MethodParameter returnType,
        @NonNull MediaType selectedContentType,
        @NonNull Class selectedConverterType,
        @NonNull ServerHttpRequest request,
        @NonNull ServerHttpResponse response) {
        if (o == null || o instanceof ResultData<?>) {
            return o;
        }
        return ResultData.success(o);
    }
}
