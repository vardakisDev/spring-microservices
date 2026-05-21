package com.interview.cartservice.infrastructure.config;

public interface ServiceTokenProvider {

    String createToken(String audience, String scope);
}