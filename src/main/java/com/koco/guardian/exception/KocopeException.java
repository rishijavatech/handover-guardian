package com.koco.guardian.exception;

import com.koco.guardian.enums.ApplicationCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class KocopeException extends Exception {

	private final HttpStatus httpStatus;
	private final String errorMessage;
    private final Integer code;

    public KocopeException(HttpStatus httpStatus, ApplicationCode applicationCode) {
        super(applicationCode.getMessage());
        this.httpStatus = httpStatus;
        this.errorMessage = applicationCode.getMessage();
        this.code = applicationCode.getCode();
    }

    public KocopeException(HttpStatus httpStatus, String errorMessage) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
        this.code = null;
    }
}
