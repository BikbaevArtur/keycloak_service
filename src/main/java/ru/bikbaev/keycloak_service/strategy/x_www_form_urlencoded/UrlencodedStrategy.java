package ru.bikbaev.keycloak_service.strategy.x_www_form_urlencoded;

import org.springframework.util.MultiValueMap;
import ru.bikbaev.keycloak_service.model.UrlencodedKeyValue;

public interface UrlencodedStrategy {

    MultiValueMap<String, String> creatRequestBody(UrlencodedKeyValue urlencodedKeyValue);
}
