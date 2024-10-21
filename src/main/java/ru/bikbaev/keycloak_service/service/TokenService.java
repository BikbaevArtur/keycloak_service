package ru.bikbaev.keycloak_service.service;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.bikbaev.keycloak_service.model.UrlencodedKeyValue;
import ru.bikbaev.keycloak_service.model.dto.AuthRequestDto;
import ru.bikbaev.keycloak_service.strategy.x_www_form_urlencoded.LogoutUrlencodedStrategy;
import ru.bikbaev.keycloak_service.strategy.x_www_form_urlencoded.RefreshTokenUrlencodedStrategy;
import ru.bikbaev.keycloak_service.strategy.x_www_form_urlencoded.UrlencodedStrategy;


@Service
public class TokenService {

    @Value("${keycloak.adminClientId}")
    private String adminClientId;
    @Value("${keycloak.adminClientSecret}")
    private String adminClientSecret;
    @Value("${keycloak.urls.auth}")
    private String authServerUrl;
    @Value("${keycloak.realm}")
    private String realm;


    private final RestTemplate restTemplate = new RestTemplate();

    private String REFRESH_TOKEN_ENDPOINT;

    private String LOGOUT_ENDPOINT;


    @PostConstruct
    public void initEndpoints() {
        this.REFRESH_TOKEN_ENDPOINT = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        this.LOGOUT_ENDPOINT = authServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";
    }


    //__
    public AccessTokenResponse authenticate(AuthRequestDto authRequestDto) {
        try (Keycloak keycloak = keycloakLoginBuild(authRequestDto.username(), authRequestDto.password())) {
            return keycloak.tokenManager().getAccessToken();
        }
    }

    private Keycloak keycloakLoginBuild(String username, String password) {
        return KeycloakBuilder.builder().serverUrl(authServerUrl).realm(realm).grantType(OAuth2Constants.PASSWORD).clientId(adminClientId).clientSecret(adminClientSecret).username(username).password(password).build();
    }
    //__


    public AccessTokenResponse refreshToken(String refreshToken) {

        HttpHeaders httpHeaders = creatHttpHeaders();

        UrlencodedStrategy urlencodedStrategy = new RefreshTokenUrlencodedStrategy();

        MultiValueMap<String, String> body = urlencodedStrategy.creatRequestBody(buildRefreshTokenParams(refreshToken));

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, httpHeaders);


        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(REFRESH_TOKEN_ENDPOINT, HttpMethod.POST, httpEntity, AccessTokenResponse.class);

        return response.getBody();
    }


    public Response logout(String refreshToken) {

        HttpHeaders httpHeaders = creatHttpHeaders();

        UrlencodedStrategy logoutUrlencoded = new LogoutUrlencodedStrategy();

        UrlencodedKeyValue keyValue = buildRefreshTokenParams(refreshToken);

        MultiValueMap<String, String> body = logoutUrlencoded.creatRequestBody(keyValue);


        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<Response> response = restTemplate.exchange(LOGOUT_ENDPOINT, HttpMethod.POST, httpEntity, Response.class);

        return response.getBody();

    }


    private HttpHeaders creatHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return httpHeaders;
    }


    private UrlencodedKeyValue buildRefreshTokenParams(String refreshToken) {
        return new UrlencodedKeyValue.Builder()
                .clientId(adminClientId)
                .clientSecret(adminClientSecret)
                .refreshToken(refreshToken)

                .build();
    }

}
