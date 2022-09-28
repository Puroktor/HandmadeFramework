package ru.vsu.csf.skofenko.testapplication.controller;

import ru.vsu.csf.framework.di.ExceptionHandler;
import ru.vsu.csf.framework.http.ExceptionMapping;
import ru.vsu.csf.framework.http.HttpStatus;
import ru.vsu.csf.framework.http.ResponseStatus;
import ru.vsu.csf.skofenko.testapplication.dto.ErrorDto;

import java.util.NoSuchElementException;

@ExceptionHandler
public class ExceptionController {

    @ExceptionMapping(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNoSuchElementException(NoSuchElementException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionMapping(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleNoSuchElementException(IllegalArgumentException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionMapping(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleIllegalStateException(IllegalStateException e) {
        return new ErrorDto(e.getMessage());
    }
}
