package com.shopMe.demo.exceptions;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorInfo AccessDeniedException(HttpServletRequest request, HttpServletResponse response,
                                           Exception exception){

        return new ErrorInfo(request,exception);
    }

    @ExceptionHandler(AuthenticationFailException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorInfo AuthenticationFailException(HttpServletRequest request, HttpServletResponse response,
                                           Exception exception){

        return new ErrorInfo(request,exception);
    }

//
//    @ExceptionHandler(ResponseStatusException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)  // 404
//    public ErrorInfo NotFound(HttpServletRequest request, HttpServletResponse response,
//                                           Exception exception){
//
//
//        return new ErrorInfo(request,exception);
//    }
}
