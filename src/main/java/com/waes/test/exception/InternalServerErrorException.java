package com.waes.test.exception;

import com.waes.test.model.event.ActionEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

/**
 * InternalServerErrorException that extends {@link BaseException}.
 *
 * @author jonathanadepaula
 */
@Getter
public class InternalServerErrorException extends BaseException {

    private String id;
    private ActionEnum action;

    public InternalServerErrorException(String errorMessageDetail) {
        super(errorMessageDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerErrorException(String errorMessageDetail, String id, ActionEnum action) {
        super(errorMessageDetail, HttpStatus.INTERNAL_SERVER_ERROR);
        this.id = id;
        this.action = action;
    }

    public boolean containsId() {
        return Objects.nonNull(id);
    }
}
