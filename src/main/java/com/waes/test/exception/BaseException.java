package com.waes.test.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {

    private final String errorMessageDetail;
    private final HttpStatus httpStatus;

    BaseException(String errorMessageDetail, HttpStatus httpStatus) {
        super(errorMessageDetail);
        this.errorMessageDetail = errorMessageDetail;
        this.httpStatus = httpStatus;
    }
}
