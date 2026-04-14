package dev.zisan.meditrack.report.dto;

import lombok.Builder;

@Builder
public record DoctorWorkloadReportResponse(
	Long doctorId,
	String doctorName,
	String specialization,
	long totalAppointments,
	long pendingAppointments,
	long confirmedAppointments,
	long completedAppointments,
	long cancelledAppointments,
	long uniquePatients
) {
}
