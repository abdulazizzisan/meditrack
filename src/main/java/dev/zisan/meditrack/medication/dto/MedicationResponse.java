package dev.zisan.meditrack.medication.dto;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MedicationResponse(
	Long id,
	Long patientId,
	Long doctorId,
	String doctorName,
	String name,
	String dosage,
	String frequency,
	LocalDate startDate,
	LocalDate endDate,
	boolean active,
	Instant createdAt,
	Instant updatedAt
) {
}
