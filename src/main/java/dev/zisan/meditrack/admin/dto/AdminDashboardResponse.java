package dev.zisan.meditrack.admin.dto;

import lombok.Builder;

@Builder
public record AdminDashboardResponse(
	long totalUsers,
	long activeUsers,
	long totalPatients,
	long totalDoctors,
	long totalAppointments,
	long pendingAppointments,
	long confirmedAppointments,
	long completedAppointments,
	long cancelledAppointments,
	long totalMedicalHistoryEntries,
	long activeMedications
) {
}
