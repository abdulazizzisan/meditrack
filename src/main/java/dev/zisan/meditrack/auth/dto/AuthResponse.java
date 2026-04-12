package dev.zisan.meditrack.auth.dto;

import dev.zisan.meditrack.user.entity.Role;
import lombok.Builder;

@Builder
public record AuthResponse(
	Long userId,
	String fullName,
	String email,
	Role role,
	String accessToken,
	String refreshToken,
	String tokenType,
	long expiresIn
) {
}
