package dev.zisan.meditrack.admin.dto;

import dev.zisan.meditrack.user.entity.Role;
import java.time.Instant;
import lombok.Builder;

@Builder
public record AdminUserResponse(
	Long id,
	String fullName,
	String email,
	Role role,
	boolean enabled,
	Instant createdAt,
	Instant updatedAt
) {
}
