package com.membership.freelancehamza.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
// cette classe permet dr gerer les erreurs qui vient de l'api dans le front et des les avoir avec un message claire
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({RuntimeException.class, IllegalArgumentException.class})
    public ResponseEntity<?> handle(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
