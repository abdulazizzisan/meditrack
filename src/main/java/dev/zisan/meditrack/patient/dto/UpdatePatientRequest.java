package dev.zisan.meditrack.patient.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdatePatientRequest(
	@NotBlank(message = "Full name is required")
	String fullName,

	@Past(message = "Date of birth must be in the past")
	LocalDate dateOfBirth,

	String gender,
	String bloodType,

	@NotBlank(message = "Phone is required")
	@Size(max = 30, message = "Phone must be at most 30 characters")
	String phone,

	String address
) {
}
