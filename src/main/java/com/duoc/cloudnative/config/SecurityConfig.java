package com.duoc.cloudnative.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${app.security.azure.audience}")
    private String audience;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API REST stateless: no se usa sesión ni formulario tradicional.
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Permite visualizar la consola H2 en desarrollo local.
                .headers(headers ->
                        headers.frameOptions(frame -> frame.sameOrigin())
                )

                .authorizeHttpRequests(auth -> auth
                        // Consola H2 solo para pruebas locales.
                        .requestMatchers("/h2-console/**").permitAll()

                        // Permite que Spring muestre respuestas de error controladas.
                        .requestMatchers("/error").permitAll()

                        // Endpoints principales del proyecto protegidos con JWT.
                        .requestMatchers("/api/**").authenticated()

                        // Endpoints auxiliares S3 del proyecto, si se usan, también protegidos.
                        .requestMatchers("/s3/**").authenticated()

                        // Endpoint auxiliar para enviar mensajes a RabbitMQ.
                        .requestMatchers("/rabbit/enviar").permitAll()

                        // Cualquier otra ruta queda bloqueada.
                        .anyRequest().denyAll()
                )

                // El microservicio actúa como Resource Server y valida JWT.
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withIssuerLocation(issuerUri)
                .build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator =
                new AudienceValidator(audience);

        OAuth2TokenValidator<Jwt> validator =
                new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator);

        jwtDecoder.setJwtValidator(validator);

        return jwtDecoder;
    }

    private static class AudienceValidator implements OAuth2TokenValidator<Jwt> {

        private final String audience;

        private AudienceValidator(String audience) {
            this.audience = audience;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            List<String> audiences = jwt.getAudience();

            if (audiences != null && audiences.contains(audience)) {
                return OAuth2TokenValidatorResult.success();
            }

            OAuth2Error error = new OAuth2Error(
                    "invalid_token",
                    "El token JWT no contiene la audiencia esperada.",
                    null
            );

            return OAuth2TokenValidatorResult.failure(error);
        }
    }
}