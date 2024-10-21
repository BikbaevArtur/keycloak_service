package ru.bikbaev.keycloak_service.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.Response;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.bikbaev.keycloak_service.model.dto.AuthRequestDto;
import ru.bikbaev.keycloak_service.service.TokenService;

@RestController
@RequestMapping("/auth")
public class TokenController {

    private final TokenService keycloakTokenService;

    public TokenController(TokenService keycloakTokenService) {
        this.keycloakTokenService = keycloakTokenService;
    }


    @Tag(name = "Вход в сервис")
    @PostMapping("/login")
    ResponseEntity<AccessTokenResponse> access(@RequestBody AuthRequestDto authRequestDto) {
        return ResponseEntity.ok(keycloakTokenService.authenticate(authRequestDto));
    }


//    /**
//     * Тестовый вариант с json
//     *
//     * @param refreshTokenRequest
//     * @return
//     */
//
//    @PostMapping("/refresh")
//    ResponseEntity<AccessTokenResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
//        return ResponseEntity.ok(keycloakTokenService.refreshToken(refreshTokenRequest.refreshToken()));
//    }



    @Tag(name = "Обновление токена")
    @PostMapping("/refresh")
    ResponseEntity<AccessTokenResponse> refresh(@RequestHeader("refresh-token") String refreshToken) {
        return ResponseEntity.ok(keycloakTokenService.refreshToken(refreshToken));
    }


    @Tag(name = "Выход с сервиса",description = "Удаление действующего рефреш токена")
    @PostMapping("/logout")
    Response logout(@RequestHeader("refresh-token") String refreshToken) {
        return keycloakTokenService.logout(refreshToken);
    }


}
