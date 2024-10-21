package ru.bikbaev.keycloak_service.controller;

import jakarta.ws.rs.NotAuthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ExceptionController {


    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ErrorMessage>  notAuthorization(NotAuthorizedException ex){
      return ResponseEntity.status(401).body(new ErrorMessage(ex.getMessage()));
    }




}
