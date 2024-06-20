package com.bupt.memes.model.common;

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
		resultData.setStatus(ReturnCode.RC100.getCode());
		resultData.setMessage(ReturnCode.RC100.getMessage());
		resultData.setData(data);
		return resultData;
	}

	public static <T> ResultData<T> fail(ReturnCode returnCode) {
		ResultData<T> resultData = new ResultData<>();
		resultData.setStatus(returnCode.getCode());
		resultData.setMessage(returnCode.getMessage());
		return resultData;
	}

}