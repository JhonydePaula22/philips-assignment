package com.waes.test.exception;

import org.springframework.http.HttpStatus;

/**
 * BadRequestException that extends {@link BaseException}.
 *
 * @author jonathanadepaula
 */
public class BadRequestException extends BaseException {

    public BadRequestException(String errorMessageDetail) {
        super(errorMessageDetail, HttpStatus.BAD_REQUEST);
    }
}
