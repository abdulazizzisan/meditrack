package dev.zisan.meditrack.patient.dto;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PatientResponse(
	Long id,
	Long userId,
	String fullName,
	String email,
	LocalDate dateOfBirth,
	String gender,
	String bloodType,
	String phone,
	String address,
	Instant createdAt,
	Instant updatedAt
) {
}
