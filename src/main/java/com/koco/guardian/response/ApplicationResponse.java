package com.koco.guardian.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.koco.guardian.enums.ResponseCode;
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

    @JsonProperty("payload")
    private T payload;

    public ApplicationResponse(int code, String message, T payload) {
        this.code = code;
        this.message = message;
        this.payload = payload;
    }

    public ApplicationResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApplicationResponse() {
    }

    public static <T> ApplicationResponse<T> getSuccessResponse(T payload) {
        return new ApplicationResponse<T>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), payload);
    }

    public static <T> ApplicationResponse<T> getFailureResponse(T payload) {
        return new ApplicationResponse<T>(ResponseCode.FAILURE.getCode(), ResponseCode.FAILURE.getMessage(), payload);
    }

    public static <T> ApplicationResponse<T> getSuccessResponse() {
        return getSuccessResponse(null);
    }

    public static <T> ApplicationResponse<T> getSuccessResponse(String message, T payload) {
        return new ApplicationResponse<T>(ResponseCode.SUCCESS.getCode(), message, payload);
    }
}
