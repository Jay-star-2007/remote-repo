package com.example.hello.exception;

import com.example.hello.dto.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        MessageResponse message = new MessageResponse(ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<MessageResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        MessageResponse message = new MessageResponse(ex.getMessage());
        // Return 401 Unauthorized to be consistent with bad password
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<MessageResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        MessageResponse message = new MessageResponse("密码错误");
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    // You can add more exception handlers here for other types of exceptions
} 