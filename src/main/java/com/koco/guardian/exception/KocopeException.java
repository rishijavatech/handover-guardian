package com.koco.guardian.exception;

import com.koco.guardian.enums.ResponseCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class KocopeException extends Exception {

	private final HttpStatus httpStatus;
	private final String errorMessage;
    private final Integer code;

    public KocopeException(HttpStatus httpStatus, ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.httpStatus = httpStatus;
        this.errorMessage = responseCode.getMessage();
        this.code = responseCode.getCode();
    }
}
