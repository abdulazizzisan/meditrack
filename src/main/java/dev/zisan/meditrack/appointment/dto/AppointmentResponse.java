package dev.zisan.meditrack.appointment.dto;

import dev.zisan.meditrack.appointment.entity.AppointmentStatus;
import java.time.Instant;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AppointmentResponse(
	Long id,
	Long patientId,
	String patientName,
	Long doctorId,
	String doctorName,
	String doctorSpecialization,
	LocalDateTime scheduledAt,
	AppointmentStatus status,
	String notes,
	Instant createdAt,
	Instant updatedAt
) {
}
