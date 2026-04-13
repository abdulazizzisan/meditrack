package dev.zisan.meditrack.appointment.dto;

import dev.zisan.meditrack.appointment.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAppointmentStatusRequest(
	@NotNull(message = "Status is required")
	AppointmentStatus status
) {
}
