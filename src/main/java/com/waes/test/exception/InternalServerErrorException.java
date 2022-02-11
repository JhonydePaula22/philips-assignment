package com.waes.test.exception;

import com.waes.test.model.event.EventEnum;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Getter
public class InternalServerErrorException extends BaseException {

    private String id;
    private EventEnum eventType;

    public InternalServerErrorException(String errorMessageDetail) {
        super(errorMessageDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerErrorException(String errorMessageDetail, String id, EventEnum eventType) {
        super(errorMessageDetail, HttpStatus.INTERNAL_SERVER_ERROR);
        this.id = id;
        this.eventType = eventType;
    }

    public boolean containsId() {
        return Objects.nonNull(id);
    }
}
