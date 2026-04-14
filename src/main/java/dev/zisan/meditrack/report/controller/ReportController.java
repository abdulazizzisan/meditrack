package dev.zisan.meditrack.report.controller;

import dev.zisan.meditrack.common.api.ApiResponse;
import dev.zisan.meditrack.report.dto.DoctorMedicationReportResponse;
import dev.zisan.meditrack.report.dto.DoctorPatientActivityReportResponse;
import dev.zisan.meditrack.report.dto.DoctorWorkloadReportResponse;
import dev.zisan.meditrack.report.dto.HospitalOverviewReportResponse;
import dev.zisan.meditrack.report.service.ReportService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/reports")
public class ReportController {

	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping("/admin/hospital-overview")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ApiResponse<HospitalOverviewReportResponse>> getHospitalOverview(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		HospitalOverviewReportResponse report = reportService.getHospitalOverview(startDate, endDate);

		ApiResponse<HospitalOverviewReportResponse> response = ApiResponse.<HospitalOverviewReportResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Hospital overview report generated successfully.")
			.data(report)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/admin/doctor-workload")
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<ApiResponse<List<DoctorWorkloadReportResponse>>> getDoctorWorkloadReport(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		List<DoctorWorkloadReportResponse> report = reportService.getDoctorWorkloadReport(startDate, endDate);

		ApiResponse<List<DoctorWorkloadReportResponse>> response = ApiResponse.<List<DoctorWorkloadReportResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Doctor workload report generated successfully.")
			.data(report)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/doctors/{doctorId}/patient-activity")
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('DOCTOR') and @doctorSecurity.isOwner(authentication, #doctorId))")
	public ResponseEntity<ApiResponse<List<DoctorPatientActivityReportResponse>>> getDoctorPatientActivityReport(
			@PathVariable Long doctorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		List<DoctorPatientActivityReportResponse> report = reportService.getDoctorPatientActivityReport(
			doctorId,
			startDate,
			endDate
		);

		ApiResponse<List<DoctorPatientActivityReportResponse>> response = ApiResponse.<List<DoctorPatientActivityReportResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Doctor patient activity report generated successfully.")
			.data(report)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/doctors/{doctorId}/medication-summary")
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('DOCTOR') and @doctorSecurity.isOwner(authentication, #doctorId))")
	public ResponseEntity<ApiResponse<List<DoctorMedicationReportResponse>>> getDoctorMedicationReport(
			@PathVariable Long doctorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
		List<DoctorMedicationReportResponse> report = reportService.getDoctorMedicationReport(
			doctorId,
			startDate,
			endDate
		);

		ApiResponse<List<DoctorMedicationReportResponse>> response = ApiResponse.<List<DoctorMedicationReportResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Doctor medication report generated successfully.")
			.data(report)
			.build();

		return ResponseEntity.ok(response);
	}
}
