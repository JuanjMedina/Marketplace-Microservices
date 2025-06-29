package com.marketplace.userservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Conversor de JWT a token de autenticación de Spring Security.
 * Esta clase se encarga de transformar los tokens JWT recibidos en objetos
 * de autenticación que Spring Security puede entender y utilizar para autorización.
 */
@Component
public class JwtAutheticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    /**
     * Conversor estándar de Spring Security para extraer autoridades básicas del JWT.
     * Este conversor extrae los roles del claim 'scope' o 'scp' por defecto.
     */
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    /**
     * Nombre del atributo que se usará como identificador principal del usuario.
     * Configurable a través de application.properties.
     */
    @Value("${jwt.auth.converter.principal-attribute}")
    private String principalAttribute;

    /**
     * ID del recurso/cliente para extraer roles específicos.
     * Configurable a través de application.properties.
     */
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    /**
     * Convierte un token JWT en un token de autenticación de Spring Security.
     * Combina las autoridades estándar con los roles específicos del recurso.
     *
     * @param source El token JWT a convertir
     * @return Un token de autenticación con las autoridades y nombre principal
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = Stream
                .concat(jwtGrantedAuthoritiesConverter.convert(source).stream(), extractResourceRoles(source).stream())
                .toList();
        return new JwtAuthenticationToken(source, authorities, getPrincipalName(source));
    }

    /**
     * Extrae el nombre principal (username) del token JWT.
     * Por defecto usa el claim 'sub', pero puede configurarse para usar otro claim.
     *
     * @param jwt El token JWT
     * @return El nombre principal del usuario
     */
    private String getPrincipalName(Jwt jwt) {

        String claimName = JwtClaimNames.SUB;

        if (principalAttribute != null && !principalAttribute.isEmpty()) {
            claimName = principalAttribute;
        }

        return jwt.getClaim(claimName).toString();

    }

    /**
     * Extrae los roles específicos del recurso desde el token JWT.
     * Busca en la estructura 'resource_access.{resourceId}.roles' del token.
     * Añade el prefijo 'ROLE_' a cada rol para que sea compatible con Spring Security.
     *
     * @param source El token JWT
     * @return Colección de autoridades basadas en los roles del recurso
     */
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt source) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        // Si no existe el claim 'resource_access', retorna una lista vacía
        if (source.getClaim("resource_access") == null) {
            return List.of();
        }

        resourceAccess = source.getClaim("resource_access");

        // Si no existe información para el resourceId configurado, retorna lista vacía
        if (resourceAccess.get(resourceId) == null) {
            return List.of();
        }

        resource = (Map<String, Object>) resourceAccess.get(resourceId);

        // Si no hay roles definidos, retorna lista vacía
        if (resource.get("roles") == null) {
            return List.of();
        }

        resourceRoles = (Collection<String>) resource.get("roles");

        // Convierte cada rol a una autoridad con prefijo 'ROLE_'
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role)))
                .toList();

    }
}
