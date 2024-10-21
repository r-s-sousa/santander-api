package com.santander.address.api.handlers;

import com.santander.address.api.exceptions.*;
import com.santander.address.api.responses.ErrorFieldResponse;
import com.santander.address.api.responses.ErrorResponse;
import com.santander.address.api.utils.Message;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class FailureExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<?> badRequestExceptionHandler(final BadRequestException badRequestException) {
        return new ResponseEntity<>(new ErrorResponse(null, badRequestException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<Object> forbiddenExceptionHandler(final ForbiddenException forbiddenException) {
        return new ResponseEntity<>(new ErrorResponse(null, forbiddenException.getMessage()), HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException httpMessageNotReadableException, @NonNull final HttpHeaders httpHeaders, @NonNull final HttpStatusCode httpStatusCode, @NonNull final WebRequest webRequest) {
        return new ResponseEntity<>(new ErrorResponse(null, httpMessageNotReadableException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException methodArgumentNotValidException, @NonNull final HttpHeaders httpHeaders, @NonNull final HttpStatusCode httpStatusCode, @NonNull final WebRequest webRequest) {
        List<ErrorFieldResponse> errors = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream().map(error -> new ErrorFieldResponse(error.getField(), error.getDefaultMessage())).collect(Collectors.toList());
        return new ResponseEntity<>(new ErrorResponse(errors, Message.VALIDATION_ERROR), HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException noHandlerFoundException, @NonNull final HttpHeaders httpHeaders, @NonNull final HttpStatusCode httpStatusCode, @NonNull final WebRequest webRequest) {
        return new ResponseEntity<>(new ErrorResponse(null, noHandlerFoundException.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> illegalArgumentExceptionHandler(final IllegalArgumentException illegalArgumentException) {
        return new ResponseEntity<>(new ErrorResponse(null, illegalArgumentException.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InternalServerErrorException.class})
    public ResponseEntity<Object> internalServerErrorExceptionHandler(final InternalServerErrorException internalServerErrorException) {
        return new ResponseEntity<>(new ErrorResponse(null, internalServerErrorException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Object> notFoundExceptionHandler(final NotFoundException notFoundException) {
        return new ResponseEntity<>(new ErrorResponse(null, notFoundException.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Object> unauthorizedExceptionHandler(final UnauthorizedException unauthorizedException) {
        return new ResponseEntity<>(new ErrorResponse(null, unauthorizedException.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
