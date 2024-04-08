package com.bupt.memes.config;

import com.bupt.memes.model.common.ResultData;
import com.bupt.memes.model.common.ReturnCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, NullPointerException e) {
        String requestUrl = request.getRequestURL().toString();
        logger.error("请求地址：" + requestUrl + "发生空指针异常！", e.getMessage());
        return ResultData.fail(ReturnCode.RC500);
    }

    // IllegalArgumentException
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, IllegalArgumentException e) {
        String requestUrl = request.getRequestURL().toString();
        logger.error("请求地址：" + requestUrl + "发生非法参数异常！", e.getMessage());
        return ResultData.fail(ReturnCode.RC500);
    }

    // ClassCastException
    @ExceptionHandler(value = ClassCastException.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, ClassCastException e) {
        String requestUrl = request.getRequestURL().toString();
        logger.error("请求地址：" + requestUrl + "发生类型转换异常！", e.getMessage());
        return ResultData.fail(ReturnCode.RC500);
    }

    // HttpMediaTypeNotAcceptableException
    @ExceptionHandler(value = HttpMediaTypeNotAcceptableException.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, HttpMediaTypeNotAcceptableException e) {
        String requestUrl = request.getRequestURL().toString();
        logger.error("请求地址：" + requestUrl + "发生媒体类型不可接受异常！", e.getMessage());
        return ResultData.fail(ReturnCode.RC500);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        String requestUrl = request.getRequestURL().toString();
        logger.error("请求地址：" + requestUrl, e.getMessage());
        return ResultData.fail(ReturnCode.RC405);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, Exception e) {
        String requestUrl = request.getRequestURL().toString();
        logger.error("请求地址：" + requestUrl + "发生异常！", e.getMessage());
        return ResultData.fail(ReturnCode.RC500);
    }


}