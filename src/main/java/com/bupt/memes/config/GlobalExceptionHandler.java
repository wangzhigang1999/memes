package com.bupt.memes.config;

import com.bupt.memes.model.common.ResultData;
import com.bupt.memes.model.common.ReturnCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultData<?> exceptionHandler(HttpServletRequest request, NullPointerException e) {
        logger.error("发生空指针异常！原因是:", e);
        return ResultData.fail(ReturnCode.RC500);
    }

    /**
     * 处理其他异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultData exceptionHandler(HttpServletRequest request, Exception e) {
        logger.error("未知异常！原因是:", e);
        return ResultData.fail(ReturnCode.RC500);
    }
}