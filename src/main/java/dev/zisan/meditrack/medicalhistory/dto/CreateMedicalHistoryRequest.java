package dev.zisan.meditrack.medicalhistory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record CreateMedicalHistoryRequest(
	@NotNull(message = "Doctor id is required")
	Long doctorId,

	@NotBlank(message = "Diagnosis is required")
	String diagnosis,

	String notes,

	@NotNull(message = "Visit date is required")
	@PastOrPresent(message = "Visit date cannot be in the future")
	LocalDate visitDate
) {
}
