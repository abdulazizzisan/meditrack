package dev.zisan.meditrack.report.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record HospitalOverviewReportResponse(
	LocalDate startDate,
	LocalDate endDate,
	long totalAppointments,
	long pendingAppointments,
	long confirmedAppointments,
	long completedAppointments,
	long cancelledAppointments,
	long totalMedicalHistoryEntries,
	long totalMedicationsIssued,
	long activeMedications
) {
}
