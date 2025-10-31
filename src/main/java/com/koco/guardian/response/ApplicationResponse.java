package com.koco.guardian.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.koco.guardian.enums.ApplicationCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ApplicationResponse<T> {

    @JsonProperty("response_code")
    private int code;

    @JsonProperty("response_message")
    private String message;

    @JsonProperty("result")
    private T result;

    public ApplicationResponse(int code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public ApplicationResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApplicationResponse() {
    }

    public static <T> ApplicationResponse<T> getSuccessResponse(T payload) {
        return new ApplicationResponse<T>(ApplicationCode.SUCCESS.getCode(), ApplicationCode.SUCCESS.getMessage(), payload);
    }

    public static <T> ApplicationResponse<T> getFailureResponse(T payload) {
        return new ApplicationResponse<T>(ApplicationCode.FAILURE.getCode(), ApplicationCode.FAILURE.getMessage(), payload);
    }

    public static <T> ApplicationResponse<T> getSuccessResponse() {
        return getSuccessResponse(null);
    }

    public static <T> ApplicationResponse<T> getSuccessResponse(String message, T payload) {
        return new ApplicationResponse<T>(ApplicationCode.SUCCESS.getCode(), message, payload);
    }
}
