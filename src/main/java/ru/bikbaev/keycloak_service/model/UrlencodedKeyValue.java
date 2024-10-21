package ru.bikbaev.keycloak_service.model;


import lombok.Getter;

@Getter
public class UrlencodedKeyValue {

    private final String clientId;

    private final String clientSecret;

    private final String refreshToken;


    private UrlencodedKeyValue(Builder builder) {
        this.clientId = builder.clientId;
        this.clientSecret = builder.clientSecret;
        this.refreshToken = builder.refreshToken;
    }


    public static class Builder {

        private String clientId;

        private String clientSecret;

        private String refreshToken;

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }


        public UrlencodedKeyValue build() {
            return new UrlencodedKeyValue(this);
        }

    }


}
