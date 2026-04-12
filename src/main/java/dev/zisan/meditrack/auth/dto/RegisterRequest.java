package dev.zisan.meditrack.auth.dto;

import dev.zisan.meditrack.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record RegisterRequest(
	@NotBlank(message = "Full name is required")
	String fullName,

	@NotBlank(message = "Email is required")
	@Email(message = "Email must be valid")
	String email,

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must be at least 8 characters")
	String password,

	@NotNull(message = "Role is required")
	Role role,

	LocalDate dateOfBirth,
	String gender,
	String bloodType,
	String phone,
	String address,
	String specialization,
	String licenseNumber,
	String hospitalAffiliation
) {
}
