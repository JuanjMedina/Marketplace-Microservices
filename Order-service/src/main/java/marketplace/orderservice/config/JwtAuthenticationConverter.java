package marketplace.orderservice.config;

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
import java.util.stream.Stream;

/**
 * JWT to Spring Security authentication token converter.
 */
@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.principal-attribute}")
    private String principalAttribute;

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = Stream
                .concat(jwtGrantedAuthoritiesConverter.convert(source).stream(), extractResourceRoles(source).stream())
                .toList();
        return new JwtAuthenticationToken(source, authorities, getPrincipalName(source));
    }

    private String getPrincipalName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;

        if (principalAttribute != null && !principalAttribute.isEmpty()) {
            claimName = principalAttribute;
        }

        Object claimValue = jwt.getClaim(claimName);
        if (claimValue == null) {
            // Fallback to 'sub' claim if configured claim doesn't exist
            claimValue = jwt.getClaim(JwtClaimNames.SUB);
        }

        return claimValue != null ? claimValue.toString() : "unknown";
    }

    /**
     * Extracts resource-specific roles from JWT token.
     * Looks for roles in 'resource_access.{resourceId}.roles' structure.
     */
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt source) {
        Object resourceAccessClaim = source.getClaim("resource_access");
        if (resourceAccessClaim == null || !(resourceAccessClaim instanceof Map)) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> resourceAccess = (Map<String, Object>) resourceAccessClaim;

        Object resourceClaim = resourceAccess.get(resourceId);
        if (resourceClaim == null || !(resourceClaim instanceof Map)) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> resource = (Map<String, Object>) resourceClaim;

        Object rolesClaim = resource.get("roles");
        if (rolesClaim == null || !(rolesClaim instanceof Collection)) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        Collection<String> resourceRoles = (Collection<String>) rolesClaim;

        // Convert each role to authority with 'ROLE_' prefix
        return resourceRoles.stream()
                .filter(role -> role != null && !role.trim().isEmpty())
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role)))
                .toList();
    }
}
