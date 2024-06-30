package com.memes.exception;

import com.memes.model.common.ResultData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(value = AppException.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, HttpServletResponse response, AppException e) {
        String requestUrl = request.getRequestURL().toString();
        String method = request.getMethod();
        String message = e.getMessage();
        AppException.ErrorType errorType = e.getErrorType();
        log.error("Request Method:{}  URL:{}, ErrorType:{}, Message:{}", method, requestUrl, errorType, message);
        response.setStatus(e.getErrorType().getCode());
        return ResultData.fail(e);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = NoResourceFoundException.class)
    @SneakyThrows
    public Object exceptionHandler(HttpServletRequest request, HttpServletResponse response, NoResourceFoundException e) {
        String requestUrl = request.getRequestURL().toString();
        log.error("Request URL:{}, Message:{}", requestUrl, e.getMessage());
        response.setStatus(e.getStatusCode().value());
        return null;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, HttpServletResponse response, IllegalArgumentException e) {
        String requestUrl = request.getRequestURL().toString();
        String method = request.getMethod();
        String message = e.getMessage();
        response.setStatus(400);
        log.error("Request Method:{}  URL:{}, Message:{}", method, requestUrl, message);
        return ResultData.fail(AppException.invalidParam(message));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        String requestUrl = request.getRequestURL().toString();
        String method = request.getMethod();
        String message = e.getMessage();
        log.error("Request Method:{}  URL:{}, Message:{}", method, requestUrl, message, e);
        response.setStatus(500);
        return ResultData.fail(AppException.internalError(message));
    }

}