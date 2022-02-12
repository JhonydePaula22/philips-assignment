package com.waes.test.controller;

import com.waes.test.exception.BaseException;
import com.waes.test.exception.InternalServerErrorException;
import com.waes.test.model.ErrorDTO;
import com.waes.test.model.event.ActionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * {@link ControllerAdvice} class to handle Exceptions during Requests.
 *
 * @author jonathanadepaula
 */
@ControllerAdvice
@Slf4j
public class RestExceptionHandlerController extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "Something went wrong. We are are working to fix it.";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "It seems that there is an error happening internally. We are are working to fix it. Please try to access the resource later. ";
    private static final String INTERNAL_SERVER_ERROR_CREATE_COMPLEMENT_MESSAGE = "If you are trying to CREATE a resource it will be reprocessed later with the ID %s.";
    private static final String INTERNAL_SERVER_ERROR_DELETE_UPDATE_COMPLEMENT_MESSAGE = "If you are trying to UPDATE or DELETE a resource it will be reprocessed later.";
    private static final String DEFAULT_LOG_ERROR_MESSAGE = "Message: {} | StackTrace: {}";

    /**
     * Handles all {@link BaseException} that may be thrown.
     *
     * @param ex {@link BaseException}
     * @return {@link ResponseEntity<ErrorDTO>}
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorDTO> handleException(BaseException ex) {
        log.error(DEFAULT_LOG_ERROR_MESSAGE, ex.getMessage(), ex.getStackTrace());
        final ErrorDTO error = new ErrorDTO().message(ex.getErrorMessageDetail());
        return ResponseEntity.status(ex.getHttpStatus()).body(error);
    }

    /**
     * Handles all {@link Exception} that may be thrown.
     *
     * @param ex {@link Exception}
     * @return {@link ResponseEntity<ErrorDTO>}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleException(Exception ex) {
        log.error(DEFAULT_LOG_ERROR_MESSAGE, ex.getMessage(), ex.getStackTrace());
        final ErrorDTO error = new ErrorDTO().message(DEFAULT_ERROR_MESSAGE);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handles all {@link InternalServerErrorException} that may be thrown.
     *
     * @param ex {@link InternalServerErrorException}
     * @return {@link ResponseEntity<ErrorDTO>}
     */
    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<ErrorDTO> handleException(InternalServerErrorException ex) {
        log.error(DEFAULT_LOG_ERROR_MESSAGE, ex.getMessage(), ex.getStackTrace());
        StringBuilder message = new StringBuilder(INTERNAL_SERVER_ERROR_MESSAGE);
        if (ex.containsId()) {
            if (ActionEnum.CREATE.equals(ex.getAction())) {
                message.append(String.format(INTERNAL_SERVER_ERROR_CREATE_COMPLEMENT_MESSAGE, ex.getId()));
            } else {
                message.append(INTERNAL_SERVER_ERROR_DELETE_UPDATE_COMPLEMENT_MESSAGE);
            }
        }
        final ErrorDTO error = new ErrorDTO().message(message.toString());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
    }
}
