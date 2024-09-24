package com.fanmix.api.common.response;

public record Response<T>(
	String status,
	String customCode,
	T data,
	String message
) {
	private static final String SUCCESS_STATUS = "SUCCESS";
	private static final String FAIL_STATUS = "FAIL";

	public static <T> Response<T> success() {
		return new Response<>(SUCCESS_STATUS, null, null, null);
	}

	public static <T> Response<T> success(T data) {
		return new Response<>(SUCCESS_STATUS, null, data, null);
	}

	public static <T> Response<T> success(T data, String message) {
		return new Response<>(SUCCESS_STATUS, null, data, message);
	}

	public static Response<String> fail(String customCode, String message) {
		return new Response<>(FAIL_STATUS, customCode, null, message);
	}

}
