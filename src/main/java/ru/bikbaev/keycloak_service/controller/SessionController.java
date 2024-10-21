package ru.bikbaev.keycloak_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bikbaev.keycloak_service.service.SessionService;

import java.util.List;

@RestController
@RequestMapping
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }


    @Tag(name = "Активные сессии")
    @GetMapping("/session")
    ResponseEntity<List<UserSessionRepresentation>> getSession(@RequestHeader("Authorization") String authHeader) throws VerificationException {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(sessionService.getSession(token));
    }

    @Tag(name = "Выход из всех активных сессий")
    @DeleteMapping("/session")
    ResponseEntity<Void> deleteSession(@RequestHeader("Authorization") String authHeader) throws VerificationException {
        String token = authHeader.substring(7);
        sessionService.killOfflineSession(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Tag(name = "Выход из сессий по id")
    @DeleteMapping("/session/{id}")
    ResponseEntity<Boolean> killSessionId(@RequestHeader("Authorization") String authHeader, @PathVariable String id) throws VerificationException {
        String token  = authHeader.substring(7);
        boolean result =  sessionService.killSessionId(token,id);
        if(result){
            return ResponseEntity.ok(true);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
