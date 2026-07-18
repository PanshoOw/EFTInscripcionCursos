package com.duoc.cloudnative.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String ROL_ESTUDIANTE = "ESTUDIANTE";
    private static final String ROL_INSTRUCTOR = "INSTRUCTOR";

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${app.security.azure.audience}")
    private String audience;

    @Value("${app.security.azure.role-claim:consultaRole}")
    private String roleClaim;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                /*
                 * API REST sin sesiones de servidor.
                 */
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Permite abrir la consola H2 dentro de un frame
                 * durante las pruebas locales.
                 */
                .headers(headers ->
                        headers.frameOptions(frame -> frame.sameOrigin())
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * Rutas técnicas permitidas sin autenticación.
                         */
                        .requestMatchers(
                                "/h2-console/**",
                                "/error"
                        ).permitAll()

                        /*
                         * CURSOS
                         *
                         * Estudiante e instructor pueden consultar.
                         * Solo el instructor puede crear cursos.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/cursos/**"
                        )
                        .hasAnyRole(
                                ROL_ESTUDIANTE,
                                ROL_INSTRUCTOR
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/cursos"
                        )
                        .hasRole(ROL_INSTRUCTOR)

                        /*
                         * INSCRIPCIONES
                         *
                         * Solo el estudiante puede registrar
                         * una inscripción.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/inscripciones"
                        )
                        .hasRole(ROL_ESTUDIANTE)

                        /*
                         * RESÚMENES DE INSCRIPCIÓN
                         *
                         * Ambos roles pueden descargar o consultar
                         * los resúmenes generados.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/inscripciones/*/resumen/archivo"
                        )
                        .hasAnyRole(
                                ROL_ESTUDIANTE,
                                ROL_INSTRUCTOR
                        )

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/inscripciones/*/resumen/s3"
                        )
                        .hasAnyRole(
                                ROL_ESTUDIANTE,
                                ROL_INSTRUCTOR
                        )

                        /*
                         * Solo el instructor puede crear,
                         * reemplazar o eliminar resúmenes en S3.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/inscripciones/*/resumen/s3"
                        )
                        .hasRole(ROL_INSTRUCTOR)

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/inscripciones/*/resumen/s3"
                        )
                        .hasRole(ROL_INSTRUCTOR)

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/inscripciones/*/resumen/s3"
                        )
                        .hasRole(ROL_INSTRUCTOR)

                        /*
                         * ENDPOINTS AUXILIARES DE S3
                         *
                         * Ambos roles pueden consultar y descargar.
                         * Solo el instructor puede subir o eliminar.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/s3/**"
                        )
                        .hasAnyRole(
                                ROL_ESTUDIANTE,
                                ROL_INSTRUCTOR
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/s3/**"
                        )
                        .hasRole(ROL_INSTRUCTOR)

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/s3/**"
                        )
                        .hasRole(ROL_INSTRUCTOR)

                        /*
                         * Publicación manual de un mensaje en RabbitMQ.
                         */
                        .requestMatchers(
                                HttpMethod.POST,
                                "/rabbit/enviar"
                        )
                        .hasRole(ROL_INSTRUCTOR)

                        /*
                         * Toda ruta no declarada queda bloqueada.
                         */
                        .anyRequest().denyAll()
                )

                /*
                 * Configura el backend como OAuth2 Resource Server.
                 */
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                );

        return http.build();
    }

    /**
     * Convierte el atributo personalizado consultaRole
     * en autoridades de Spring Security.
     *
     * ESTUDIANTE -> ROLE_ESTUDIANTE
     * INSTRUCTOR -> ROLE_INSTRUCTOR
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                new AzureAuthoritiesConverter(roleClaim)
        );

        return converter;
    }

    /**
     * Valida:
     *
     * - Firma del token.
     * - Emisor.
     * - Fechas de vigencia.
     * - Audiencia correspondiente a esta API.
     */
    @Bean
    public JwtDecoder jwtDecoder() {

        NimbusJwtDecoder jwtDecoder =
                NimbusJwtDecoder
                        .withIssuerLocation(issuerUri)
                        .build();

        OAuth2TokenValidator<Jwt> issuerValidator =
                JwtValidators.createDefaultWithIssuer(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator =
                new AudienceValidator(audience);

        OAuth2TokenValidator<Jwt> completeValidator =
                new DelegatingOAuth2TokenValidator<>(
                        issuerValidator,
                        audienceValidator
                );

        jwtDecoder.setJwtValidator(completeValidator);

        return jwtDecoder;
    }

    /**
     * Validador personalizado de audiencia.
     */
    private static class AudienceValidator
            implements OAuth2TokenValidator<Jwt> {

        private final String expectedAudience;

        private AudienceValidator(String expectedAudience) {
            this.expectedAudience = expectedAudience;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {

            List<String> audiences = jwt.getAudience();

            if (audiences != null
                    && audiences.contains(expectedAudience)) {

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