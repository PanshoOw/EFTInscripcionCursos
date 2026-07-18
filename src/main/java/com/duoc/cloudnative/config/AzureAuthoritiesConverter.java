package com.duoc.cloudnative.config;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

/**
 * Convierte los scopes y el rol personalizado recibido desde Microsoft Entra
 * en autoridades reconocidas por Spring Security.
 *
 * Ejemplos:
 *
 * consultaRole = "ESTUDIANTE"
 * Resultado: ROLE_ESTUDIANTE
 *
 * extension_xxxxx_consultaRole = "INSTRUCTOR"
 * Resultado: ROLE_INSTRUCTOR
 */
public class AzureAuthoritiesConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String ROLE_PREFIX = "ROLE_";

    private final String roleClaimName;

    /*
     * Conserva también los scopes normales del token, por ejemplo:
     * SCOPE_access_as_user
     */
    private final JwtGrantedAuthoritiesConverter scopeAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    public AzureAuthoritiesConverter(String roleClaimName) {

        if (roleClaimName == null || roleClaimName.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del claim de rol no puede estar vacío."
            );
        }

        this.roleClaimName = roleClaimName.trim();
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Set<GrantedAuthority> authorities = new LinkedHashSet<>();

        Collection<GrantedAuthority> scopeAuthorities =
                scopeAuthoritiesConverter.convert(jwt);

        if (scopeAuthorities != null) {
            authorities.addAll(scopeAuthorities);
        }

        Object roleClaimValue = findRoleClaim(jwt.getClaims());

        extractRoles(roleClaimValue).stream()
                .map(this::normalizeRole)
                .filter(role -> !role.isBlank())
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .forEach(authorities::add);

        return authorities;
    }

    /**
     * Busca primero el claim exacto "consultaRole".
     *
     * Si Azure entrega el atributo con nombre extendido, también reconoce
     * formatos como:
     *
     * extension_123456789_consultaRole
     */
    private Object findRoleClaim(Map<String, Object> claims) {

        Object exactClaim = claims.get(roleClaimName);

        if (exactClaim != null) {
            return exactClaim;
        }

        String normalizedClaimName =
                roleClaimName.toLowerCase(Locale.ROOT);

        String expectedSuffix = "_" + normalizedClaimName;

        return claims.entrySet()
                .stream()
                .filter(entry -> {

                    String currentClaimName =
                            entry.getKey().toLowerCase(Locale.ROOT);

                    return currentClaimName.equals(normalizedClaimName)
                            || currentClaimName.endsWith(expectedSuffix);
                })
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Admite que Azure entregue el rol como:
     *
     * - String
     * - Lista
     * - Arreglo
     * - Valores separados por coma, punto y coma o espacio
     */
    private Set<String> extractRoles(Object claimValue) {

        Set<String> roles = new LinkedHashSet<>();

        if (claimValue == null) {
            return roles;
        }

        if (claimValue instanceof Collection<?> collection) {

            collection.forEach(value -> addRoleValue(roles, value));

            return roles;
        }

        if (claimValue.getClass().isArray()) {

            int length = Array.getLength(claimValue);

            for (int index = 0; index < length; index++) {
                addRoleValue(roles, Array.get(claimValue, index));
            }

            return roles;
        }

        addRoleValue(roles, claimValue);

        return roles;
    }

    private void addRoleValue(Set<String> roles, Object value) {

        if (value == null) {
            return;
        }

        String rawValue = value.toString().trim();

        if (rawValue.isBlank()) {
            return;
        }

        String[] separatedRoles = rawValue.split("[,;\\s]+");

        for (String role : separatedRoles) {

            if (!role.isBlank()) {
                roles.add(role);
            }
        }
    }

    private String normalizeRole(String role) {

        String normalizedRole =
                role.trim().toUpperCase(Locale.ROOT);

        if (normalizedRole.startsWith(ROLE_PREFIX)) {
            normalizedRole =
                    normalizedRole.substring(ROLE_PREFIX.length());
        }

        /*
         * Evita caracteres inválidos dentro de una autoridad de Spring.
         */
        return normalizedRole.replaceAll("[^A-Z0-9_]", "_");
    }
}