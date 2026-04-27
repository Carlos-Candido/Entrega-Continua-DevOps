package br.com.fatecads.fatecads.controller;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalWebExceptionHandler {

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
                BindException.class,
            IllegalArgumentException.class
    })
    public String handleBadRequest(Exception ex) {
        return "redirect:/";
    }
}
