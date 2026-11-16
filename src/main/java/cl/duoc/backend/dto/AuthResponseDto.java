package cl.duoc.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {
    private final String accessToken;
    private final String tokenType;
    private final long expiresIn;
    private final String userId;
    private final String username;
}
