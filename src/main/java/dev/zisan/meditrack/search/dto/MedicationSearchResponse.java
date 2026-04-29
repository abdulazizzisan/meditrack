package dev.zisan.meditrack.search.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record MedicationSearchResponse(
	Long id,
	Long patientId,
	Long doctorId,
	String patientName,
	String doctorName,
	String name,
	String dosage,
	String frequency,
	LocalDate startDate,
	LocalDate endDate,
	boolean active
) {
}
