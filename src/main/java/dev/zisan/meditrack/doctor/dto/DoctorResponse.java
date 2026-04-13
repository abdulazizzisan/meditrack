package dev.zisan.meditrack.doctor.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record DoctorResponse(
	Long id,
	Long userId,
	String fullName,
	String email,
	String specialization,
	String licenseNumber,
	String hospitalAffiliation,
	Instant createdAt,
	Instant updatedAt
) {
}
