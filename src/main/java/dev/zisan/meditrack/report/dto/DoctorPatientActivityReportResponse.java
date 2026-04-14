package dev.zisan.meditrack.report.dto;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DoctorPatientActivityReportResponse(
	Long patientId,
	String patientName,
	long appointmentCount,
	long completedAppointments,
	long medicalHistoryEntries,
	LocalDate lastVisitDate
) {
}
