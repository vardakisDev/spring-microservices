package com.interview.cartservice.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Configuration
public class LocalJwtSecurityConfiguration {

    @Bean
    ServiceTokenProvider serviceTokenProvider(JwtEncoder jwtEncoder,
                                              @Value("${app.security.jwt.issuer}") String issuer,
                                              @Value("${app.security.jwt.token-ttl}") Duration tokenTtl) {
        return new LocalServiceTokenProvider(jwtEncoder, issuer, tokenTtl);
    }

    private static final class LocalServiceTokenProvider implements ServiceTokenProvider {

        private final JwtEncoder jwtEncoder;
        private final String issuer;
        private final Duration tokenTtl;

        private LocalServiceTokenProvider(JwtEncoder jwtEncoder, String issuer, Duration tokenTtl) {
            this.jwtEncoder = jwtEncoder;
            this.issuer = issuer;
            this.tokenTtl = tokenTtl;
        }

        @Override
        public String createToken(String audience, String scope) {
            Instant issuedAt = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer(issuer)
                    .issuedAt(issuedAt)
                    .expiresAt(issuedAt.plus(tokenTtl))
                    .subject("cart-service")
                    .audience(List.of(audience))
                    .claim("scope", scope)
                    .claim("client_id", "cart-service")
                    .build();
            return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                    .getTokenValue();
        }
    }
}