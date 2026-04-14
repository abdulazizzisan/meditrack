package dev.zisan.meditrack.report.service;

import dev.zisan.meditrack.common.exception.BadRequestException;
import dev.zisan.meditrack.common.exception.ResourceNotFoundException;
import dev.zisan.meditrack.doctor.repository.DoctorRepository;
import dev.zisan.meditrack.report.dto.DoctorMedicationReportResponse;
import dev.zisan.meditrack.report.dto.DoctorPatientActivityReportResponse;
import dev.zisan.meditrack.report.dto.DoctorWorkloadReportResponse;
import dev.zisan.meditrack.report.dto.HospitalOverviewReportResponse;
import dev.zisan.meditrack.report.repository.ReportJdbcRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

	private final ReportJdbcRepository reportJdbcRepository;
	private final DoctorRepository doctorRepository;

	public ReportService(ReportJdbcRepository reportJdbcRepository, DoctorRepository doctorRepository) {
		this.reportJdbcRepository = reportJdbcRepository;
		this.doctorRepository = doctorRepository;
	}

	@Transactional(readOnly = true)
	public HospitalOverviewReportResponse getHospitalOverview(LocalDate startDate, LocalDate endDate) {
		validateDateRange(startDate, endDate);
		return reportJdbcRepository.getHospitalOverview(startDate, endDate);
	}

	@Transactional(readOnly = true)
	public List<DoctorWorkloadReportResponse> getDoctorWorkloadReport(LocalDate startDate, LocalDate endDate) {
		validateDateRange(startDate, endDate);
		return reportJdbcRepository.getDoctorWorkloadReport(startDate, endDate);
	}

	@Transactional(readOnly = true)
	public List<DoctorPatientActivityReportResponse> getDoctorPatientActivityReport(Long doctorId, LocalDate startDate,
			LocalDate endDate) {
		validateDateRange(startDate, endDate);
		assertDoctorExists(doctorId);
		return reportJdbcRepository.getDoctorPatientActivityReport(doctorId, startDate, endDate);
	}

	@Transactional(readOnly = true)
	public List<DoctorMedicationReportResponse> getDoctorMedicationReport(Long doctorId, LocalDate startDate,
			LocalDate endDate) {
		validateDateRange(startDate, endDate);
		assertDoctorExists(doctorId);
		return reportJdbcRepository.getDoctorMedicationReport(doctorId, startDate, endDate);
	}

	private void validateDateRange(LocalDate startDate, LocalDate endDate) {
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			throw new BadRequestException("Start date cannot be after end date.");
		}
	}

	private void assertDoctorExists(Long doctorId) {
		doctorRepository.findById(doctorId)
			.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
	}
}
