package dev.zisan.meditrack.report.dto;

import lombok.Builder;

@Builder
public record DoctorMedicationReportResponse(
	Long patientId,
	String patientName,
	long totalMedicationsIssued,
	long activeMedications,
	long inactiveMedications
) {
}
