package com.memes.model.common;

import com.memes.exception.AppException;

import lombok.Data;

@Data
public class ResultData<T> {
    /**
     * 结果状态 ,具体状态码参见 ResultData.java
     */
    private int status;
    private String message;
    private T data;
    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success(T data) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(200);
        resultData.setMessage("success");
        resultData.setData(data);
        return resultData;
    }

    public static <T> ResultData<T> fail(AppException exception) {
        ResultData<T> resultData = new ResultData<>();
        resultData.setStatus(exception.getErrorType().getCode());
        resultData.setMessage(exception.getMessage());
        return resultData;
    }

}
