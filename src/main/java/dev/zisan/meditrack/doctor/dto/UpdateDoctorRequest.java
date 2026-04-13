package dev.zisan.meditrack.doctor.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDoctorRequest(
	@NotBlank(message = "Full name is required")
	String fullName,

	@NotBlank(message = "Specialization is required")
	String specialization,

	String hospitalAffiliation
) {
}
