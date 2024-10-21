package ru.bikbaev.keycloak_service.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.Response;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bikbaev.keycloak_service.exeption.UserAlreadyExistsException;
import ru.bikbaev.keycloak_service.model.dto.UpdateEmailDTO;
import ru.bikbaev.keycloak_service.model.dto.UpdatePasswordDTO;
import ru.bikbaev.keycloak_service.model.dto.UserRegistrationRecord;
import ru.bikbaev.keycloak_service.service.UserService;


@RestController
@RequestMapping()
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * TODO: Сделать return  dto под нужные поля
     *
     * @param authHeader
     * @return User
     * @throws VerificationException
     */
    @Tag(name = "Информация о пользователе")
    @GetMapping("/user")
    public ResponseEntity<UserRepresentation> getUser(@RequestHeader("Authorization") String authHeader) throws VerificationException {
        String token = authHeader.substring(7);
        return ResponseEntity.ok(userService.getUser(token));
    }


    @Tag(name = "Обновление пароля")
    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestHeader("Authorization") String auth, @RequestBody UpdatePasswordDTO update) throws VerificationException {
        String token = auth.substring(7);
        userService.updatePassword(token, update.password());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Tag(name = "Регистрация пользователя")
    @PostMapping("/signup")
    ResponseEntity<Response> registration(@RequestBody UserRegistrationRecord registration) {
        try (Response response = userService.registration(registration)) {
            return new ResponseEntity<>(HttpStatus.valueOf(response.getStatus()));
        }
    }

    @Tag(name = "Обновление почты")
    @PutMapping("/update-email")
    public ResponseEntity<Void> updateEmail(@RequestHeader("Authorization") String auth, @RequestBody UpdateEmailDTO update) throws VerificationException {
        String token = auth.substring(7);
        userService.updateEmail(token, update.email());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> ex2(UserAlreadyExistsException ex) {

        return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<ErrorMessage> ex2(VerificationException ex) {
        return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));
    }


}
