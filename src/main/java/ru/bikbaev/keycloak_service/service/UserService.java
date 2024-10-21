package ru.bikbaev.keycloak_service.service;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.bikbaev.keycloak_service.exeption.UserAlreadyExistsException;
import ru.bikbaev.keycloak_service.model.dto.UserRegistrationRecord;

import java.util.Collections;

@Slf4j
@Service
public class UserService {

    @Value("${keycloak.adminClientId}")
    private String clientId;
    @Value("${keycloak.adminClientSecret}")
    private String clientSecret;
    @Value("${keycloak.urls.auth}")
    private String authServerUrl;
    @Value("${keycloak.realm}")
    private String realm;


    public Response registration(UserRegistrationRecord userRegistrationRecord) {

        UserRepresentation user = creatUser(userRegistrationRecord);
        try (Keycloak keycloak = keycloak()) {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            Response response = usersResource.create(user);
            if (response.getStatus() == 201) {
                return response;
            } else if (response.getStatus() == 409) {
                throw new UserAlreadyExistsException("Username или email уже занят");
            } else return response;
        }
    }


    public void updatePassword(String token, String password) throws VerificationException {

        try (Keycloak keycloak = keycloak()) {
            AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();

            RealmResource resource = keycloak.realm(realm);

            UserResource userResource = resource.users().get(accessToken.getSubject());
            CredentialRepresentation updatePassword = new CredentialRepresentation();
            updatePassword.setValue(password);
            updatePassword.setTemporary(false);
            updatePassword.setType(CredentialRepresentation.PASSWORD);

            userResource.resetPassword(updatePassword);
        }

    }


    public void updateEmail(String token, String email) throws VerificationException {

        AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();

        try (Keycloak keycloak = keycloak()) {
            RealmResource resource = keycloak.realm(realm);

            UserResource userResource = resource.users().get(accessToken.getSubject());

            UserRepresentation user = userResource.toRepresentation();
            user.setEmail(email);

            userResource.update(user);

        }


    }


    public UserRepresentation getUser(String token) throws VerificationException {
        try (Keycloak keycloak = keycloak()) {

            AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();
            return keycloak
                    .realm(realm)
                    .users()
                    .get(accessToken.getSubject())
                    .toRepresentation();

        }
    }


    private UserRepresentation creatUser(UserRegistrationRecord userRegistrationRecord) {
        UserRepresentation user = new UserRepresentation();

        user.setEnabled(true);
        user.setUsername(userRegistrationRecord.username());
        user.setEmail(userRegistrationRecord.email());
        user.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRegistrationRecord.password());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        user.setCredentials(Collections.singletonList(credentialRepresentation));
        return user;
    }


    private Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }


}
