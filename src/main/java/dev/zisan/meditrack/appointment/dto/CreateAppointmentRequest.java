package dev.zisan.meditrack.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateAppointmentRequest(
	@NotNull(message = "Patient id is required")
	Long patientId,

	@NotNull(message = "Doctor id is required")
	Long doctorId,

	@NotNull(message = "Scheduled time is required")
	@Future(message = "Appointment time must be in the future")
	LocalDateTime scheduledAt,

	String notes
) {
}
