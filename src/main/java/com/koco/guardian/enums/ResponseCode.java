package com.koco.guardian.enums;

public enum ResponseCode {
    SUCCESS(0, "Success"),
    FAILURE(1, "Failure"),
    USER_ALREADY_REGISTERED_IN_SYSTEM(1001, "User already registered in system");

    private int code;
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
