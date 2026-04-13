package dev.zisan.meditrack.medication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record CreateMedicationRequest(
	@NotNull(message = "Doctor id is required")
	Long doctorId,

	@NotBlank(message = "Medication name is required")
	String name,

	@NotBlank(message = "Dosage is required")
	String dosage,

	@NotBlank(message = "Frequency is required")
	String frequency,

	@NotNull(message = "Start date is required")
	@PastOrPresent(message = "Start date cannot be in the future")
	LocalDate startDate,

	LocalDate endDate,
	Boolean active
) {
}
