package com.koco.guardian.enums;

import lombok.Getter;

@Getter
public enum ApplicationCode {
    SUCCESS(0, "Success"),
    FAILURE(1, "Failure"),
    USER_ALREADY_REGISTERED_IN_SYSTEM(1001, "User already registered in system"),
    USER_NOT_FOUND_IN_SYSTEM(1002, "User not found in system"),
    USER_ALREADY_DELETED(1003, "User already deleted"),
    USER_ALREADY_INACTIVE(1004, "User already inactive");

    private final int code;
    private final String message;

    ApplicationCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
