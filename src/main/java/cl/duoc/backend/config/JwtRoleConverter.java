package cl.duoc.backend.config;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Leemos el claim "roles" que enviamos en el token
        List<String> roles = jwt.getClaimAsStringList("roles");

        // Spring Security requiere que los roles empiecen con "ROLE_"
        List<SimpleGrantedAuthority> authorities = roles == null ? List.of()
                : roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .collect(Collectors.toList());

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}