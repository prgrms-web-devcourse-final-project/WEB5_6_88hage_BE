package com.grepp.funfun.app.infra.auth.oauth2.user;

public interface OAuth2UserInfo {

    String getProviderId();
    String getProvider();
    String getName();
    String getEmail();

}
