package com.bupt.memes.exception;

import com.bupt.memes.model.common.ResultData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

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
        if (errorType == AppException.ErrorType.UNAUTHORIZED) {
            response.setStatus(401);
        }
        return ResultData.fail(e);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, Exception e) {
        String requestUrl = request.getRequestURL().toString();
        String method = request.getMethod();
        String message = e.getMessage();
        log.error("Request Method:{}  URL:{}, Message:{}", method, requestUrl, message);
        return ResultData.fail(AppException.internalError(message));
    }

}