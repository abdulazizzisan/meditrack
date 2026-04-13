package dev.zisan.meditrack.medicalhistory.dto;

import java.time.Instant;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MedicalHistoryResponse(
	Long id,
	Long patientId,
	Long doctorId,
	String doctorName,
	String diagnosis,
	String notes,
	LocalDate visitDate,
	Instant createdAt,
	Instant updatedAt
) {
}
