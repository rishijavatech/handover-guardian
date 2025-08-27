package com.koco.guardian.enums;

import lombok.Getter;

@Getter
public enum ApplicationCode {
    SUCCESS(0, "Success"),
    FAILURE(1, "Failure"),
    USER_ALREADY_REGISTERED_IN_SYSTEM(1001, "User already registered in system");

    private final int code;
    private final String message;

    ApplicationCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
