package com.midtern.SpringCommerce.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAlreadyExistException extends RuntimeException {
    private String message;

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Message handleUserAlreadyExistException(UserAlreadyExistException ex) {
        return Message.builder()
                .message(ex.getMessage())
                .build();
    }
}
