package ru.bikbaev.keycloak_service.strategy.x_www_form_urlencoded;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.bikbaev.keycloak_service.model.UrlencodedKeyValue;

public class RefreshTokenUrlencodedStrategy implements UrlencodedStrategy {

    @Override
    public MultiValueMap<String, String> creatRequestBody(UrlencodedKeyValue urlencodedKeyValue) {

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", urlencodedKeyValue.getClientId());
        body.add("client_secret", urlencodedKeyValue.getClientSecret());
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", urlencodedKeyValue.getRefreshToken());

        return body;
    }
}
