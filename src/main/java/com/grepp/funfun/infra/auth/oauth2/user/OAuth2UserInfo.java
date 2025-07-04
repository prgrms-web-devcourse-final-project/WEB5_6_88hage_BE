package com.grepp.funfun.infra.auth.oauth2.user;

public interface OAuth2UserInfo {

    String getProviderId();
    String getProvider();
    String getName();
    String getEmail();

}
