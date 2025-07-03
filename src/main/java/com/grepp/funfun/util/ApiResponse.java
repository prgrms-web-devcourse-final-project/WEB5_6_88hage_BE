package com.grepp.funfun.util;

import com.grepp.funfun.infra.response.ResponseCode;

public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String code;
    private final String message;

    private ApiResponse(boolean success, T data, String code, String message) {
        this.success = success;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, ResponseCode.OK.code(), ResponseCode.OK.message());
    }

    public static <T> ApiResponse<T> success(T data, String messageCode) {
        return new ApiResponse<>(true, data, ResponseCode.OK.code(), WebUtils.getMessage(messageCode));
    }

    public static <T> ApiResponse<T> error(ResponseCode code) {
        return new ApiResponse<>(false, null, code.code(), code.message());
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
