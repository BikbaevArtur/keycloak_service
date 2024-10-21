package ru.bikbaev.keycloak_service.service;

import org.keycloak.OAuth2Constants;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    @Value("${keycloak.adminClientId}")
    private String clientId;
    @Value("${keycloak.adminClientSecret}")
    private String clientSecretKey;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.urls.auth}")
    private String serverUrl;


    public List<UserSessionRepresentation> getSession(String token) throws VerificationException {
        try (Keycloak keycloak = keycloakBuild()) {
            return getSession(token, keycloak);
        }
    }


    public boolean killSessionId(String token, String idSession) throws VerificationException {
        try (Keycloak keycloak = keycloakBuild()) {
            return killSessionId(token, idSession, keycloak);
        }
    }


    public void killOfflineSession(String token) throws VerificationException {
        try (Keycloak keycloak = keycloakBuild()) {
            killOfflineSession(token, keycloak);
        }
    }


    private List<UserSessionRepresentation> getSession(String token, Keycloak keycloak) throws VerificationException {

        AccessToken accessToken = TokenVerifier.create(token, AccessToken.class).getToken();

        String idUser = accessToken.getSubject();

        return keycloak.realm(realm).users().get(idUser).getUserSessions();
    }

    private boolean killSessionId(String token, String idSession, Keycloak keycloak) throws VerificationException {

        List<UserSessionRepresentation> sessions = getSession(token, keycloak);


        Optional<UserSessionRepresentation> sessionKill = sessions
                .stream()
                .filter(session -> idSession.equals(session.getId()))
                .findFirst();

        if (sessionKill.isPresent()) {
            keycloak.realm(realm).deleteSession(idSession, true);
            return true;
        }
        return false;

    }


    private void killOfflineSession(String token, Keycloak keycloak) throws VerificationException {
        List<UserSessionRepresentation> sessions = getSession(token, keycloak);
        RealmResource resource = keycloak.realm(realm);
        for (UserSessionRepresentation session : sessions) {
            resource.deleteSession(session.getId(), true);
        }
    }


    private Keycloak keycloakBuild() {
        return KeycloakBuilder.builder()
                .clientId(clientId)
                .clientSecret(clientSecretKey)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .serverUrl(serverUrl)
                .build();
    }


}
