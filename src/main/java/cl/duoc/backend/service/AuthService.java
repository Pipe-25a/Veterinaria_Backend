package cl.duoc.backend.service;

import cl.duoc.backend.dto.AuthResponseDto;
import cl.duoc.backend.dto.LoginRequestDto;
import cl.duoc.backend.dto.RegisterRequestDto;
import cl.duoc.backend.entity.Role;
import cl.duoc.backend.entity.User;
import cl.duoc.backend.repository.RoleRepository;
import cl.duoc.backend.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository; 
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.expiration-seconds}")
    private long expirationSeconds;

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())
                || userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario o correo ya está registrado");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        //  ASIGNACIÓN DEL ROL POR DEFECTO (por el servidor)
        Role defaultRole = roleRepository.findByName("CLIENTE")
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Rol por defecto 'CLIENTE' no configurado en la base de datos"));
        user.setRole(defaultRole);

        return createToken(userRepository.save(user));
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsernameOrEmail(request.getIdentifier(), request.getIdentifier())
                .filter(candidate -> candidate.isActive()
                        && passwordEncoder.matches(request.getPassword(), candidate.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        return createToken(user);
    }

    private AuthResponseDto createToken(User user) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getIdUser())
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(expirationSeconds))
                .claim("username", user.getUsername())
                //  Mejora opcional: incluir rol en el JWT para usar @PreAuthorize
                .claim("roles", user.getRole() == null
                        ? java.util.List.of()
                        : java.util.List.of(user.getRole().getName()))
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();

        return new AuthResponseDto(token, "Bearer", expirationSeconds, user.getIdUser(), user.getUsername());
    }
}