package dev.zisan.meditrack.report.repository;

import dev.zisan.meditrack.report.dto.DoctorMedicationReportResponse;
import dev.zisan.meditrack.report.dto.DoctorPatientActivityReportResponse;
import dev.zisan.meditrack.report.dto.DoctorWorkloadReportResponse;
import dev.zisan.meditrack.report.dto.HospitalOverviewReportResponse;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportJdbcRepository {

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public ReportJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public HospitalOverviewReportResponse getHospitalOverview(LocalDate startDate, LocalDate endDate) {
		String appointmentFilter = buildDateFilter("scheduled_at", startDate, endDate, "a");
		String historyFilter = buildDateFilter("visit_date", startDate, endDate, "mh");
		String medicationFilter = buildDateFilter("start_date", startDate, endDate, "m");
		MapSqlParameterSource params = buildDateParams(startDate, endDate);

		String sql = """
			SELECT
			    (SELECT COUNT(*) FROM appointments a WHERE 1=1 %s) AS total_appointments,
			    (SELECT COUNT(*) FROM appointments a WHERE a.status = 'PENDING' %s) AS pending_appointments,
			    (SELECT COUNT(*) FROM appointments a WHERE a.status = 'CONFIRMED' %s) AS confirmed_appointments,
			    (SELECT COUNT(*) FROM appointments a WHERE a.status = 'COMPLETED' %s) AS completed_appointments,
			    (SELECT COUNT(*) FROM appointments a WHERE a.status = 'CANCELLED' %s) AS cancelled_appointments,
			    (SELECT COUNT(*) FROM medical_histories mh WHERE 1=1 %s) AS total_medical_history_entries,
			    (SELECT COUNT(*) FROM medications m WHERE 1=1 %s) AS total_medications_issued,
			    (SELECT COUNT(*) FROM medications m WHERE m.is_active = TRUE %s) AS active_medications
			""".formatted(
			appointmentFilter,
			appointmentFilter,
			appointmentFilter,
			appointmentFilter,
			appointmentFilter,
			historyFilter,
			medicationFilter,
			medicationFilter
		);

		return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> HospitalOverviewReportResponse.builder()
			.startDate(startDate)
			.endDate(endDate)
			.totalAppointments(rs.getLong("total_appointments"))
			.pendingAppointments(rs.getLong("pending_appointments"))
			.confirmedAppointments(rs.getLong("confirmed_appointments"))
			.completedAppointments(rs.getLong("completed_appointments"))
			.cancelledAppointments(rs.getLong("cancelled_appointments"))
			.totalMedicalHistoryEntries(rs.getLong("total_medical_history_entries"))
			.totalMedicationsIssued(rs.getLong("total_medications_issued"))
			.activeMedications(rs.getLong("active_medications"))
			.build());
	}

	public List<DoctorWorkloadReportResponse> getDoctorWorkloadReport(LocalDate startDate, LocalDate endDate) {
		String appointmentFilter = buildDateFilter("scheduled_at", startDate, endDate, "a");
		MapSqlParameterSource params = buildDateParams(startDate, endDate);

		String sql = """
			SELECT
			    d.id AS doctor_id,
			    u.full_name AS doctor_name,
			    d.specialization,
			    COUNT(a.id) AS total_appointments,
			    COALESCE(SUM(CASE WHEN a.status = 'PENDING' THEN 1 ELSE 0 END), 0) AS pending_appointments,
			    COALESCE(SUM(CASE WHEN a.status = 'CONFIRMED' THEN 1 ELSE 0 END), 0) AS confirmed_appointments,
			    COALESCE(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completed_appointments,
			    COALESCE(SUM(CASE WHEN a.status = 'CANCELLED' THEN 1 ELSE 0 END), 0) AS cancelled_appointments,
			    COUNT(DISTINCT a.patient_id) AS unique_patients
			FROM doctors d
			JOIN users u ON u.id = d.user_id
			LEFT JOIN appointments a ON a.doctor_id = d.id %s
			GROUP BY d.id, u.full_name, d.specialization
			ORDER BY total_appointments DESC, doctor_name ASC
			""".formatted(appointmentFilter);

		return jdbcTemplate.query(sql, params, (rs, rowNum) -> DoctorWorkloadReportResponse.builder()
			.doctorId(rs.getLong("doctor_id"))
			.doctorName(rs.getString("doctor_name"))
			.specialization(rs.getString("specialization"))
			.totalAppointments(rs.getLong("total_appointments"))
			.pendingAppointments(rs.getLong("pending_appointments"))
			.confirmedAppointments(rs.getLong("confirmed_appointments"))
			.completedAppointments(rs.getLong("completed_appointments"))
			.cancelledAppointments(rs.getLong("cancelled_appointments"))
			.uniquePatients(rs.getLong("unique_patients"))
			.build());
	}

	public List<DoctorPatientActivityReportResponse> getDoctorPatientActivityReport(Long doctorId, LocalDate startDate,
			LocalDate endDate) {
		String appointmentFilter = buildDateFilter("scheduled_at", startDate, endDate, "a");
		String historyFilter = buildDateFilter("visit_date", startDate, endDate, "mh");
		MapSqlParameterSource params = buildDateParams(startDate, endDate);
		params.addValue("doctorId", doctorId);

		String sql = """
			SELECT
			    p.id AS patient_id,
			    u.full_name AS patient_name,
			    COUNT(DISTINCT a.id) AS appointment_count,
			    COALESCE(SUM(CASE WHEN a.status = 'COMPLETED' THEN 1 ELSE 0 END), 0) AS completed_appointments,
			    COUNT(DISTINCT mh.id) AS medical_history_entries,
			    MAX(mh.visit_date) AS last_visit_date
			FROM patients p
			JOIN users u ON u.id = p.user_id
			LEFT JOIN appointments a ON a.patient_id = p.id AND a.doctor_id = :doctorId %s
			LEFT JOIN medical_histories mh ON mh.patient_id = p.id AND mh.doctor_id = :doctorId %s
			WHERE EXISTS (
			    SELECT 1
			    FROM appointments a2
			    WHERE a2.patient_id = p.id AND a2.doctor_id = :doctorId
			)
			OR EXISTS (
			    SELECT 1
			    FROM medical_histories mh2
			    WHERE mh2.patient_id = p.id AND mh2.doctor_id = :doctorId
			)
			GROUP BY p.id, u.full_name
			ORDER BY patient_name ASC
			""".formatted(appointmentFilter, historyFilter);

		return jdbcTemplate.query(sql, params, (rs, rowNum) -> DoctorPatientActivityReportResponse.builder()
			.patientId(rs.getLong("patient_id"))
			.patientName(rs.getString("patient_name"))
			.appointmentCount(rs.getLong("appointment_count"))
			.completedAppointments(rs.getLong("completed_appointments"))
			.medicalHistoryEntries(rs.getLong("medical_history_entries"))
			.lastVisitDate(rs.getDate("last_visit_date") != null ? rs.getDate("last_visit_date").toLocalDate() : null)
			.build());
	}

	public List<DoctorMedicationReportResponse> getDoctorMedicationReport(Long doctorId, LocalDate startDate,
			LocalDate endDate) {
		String medicationFilter = buildDateFilter("start_date", startDate, endDate, "m");
		MapSqlParameterSource params = buildDateParams(startDate, endDate);
		params.addValue("doctorId", doctorId);

		String sql = """
			SELECT
			    p.id AS patient_id,
			    u.full_name AS patient_name,
			    COUNT(m.id) AS total_medications_issued,
			    COALESCE(SUM(CASE WHEN m.is_active = TRUE THEN 1 ELSE 0 END), 0) AS active_medications,
			    COALESCE(SUM(CASE WHEN m.is_active = FALSE THEN 1 ELSE 0 END), 0) AS inactive_medications
			FROM medications m
			JOIN patients p ON p.id = m.patient_id
			JOIN users u ON u.id = p.user_id
			WHERE m.doctor_id = :doctorId %s
			GROUP BY p.id, u.full_name
			ORDER BY total_medications_issued DESC, patient_name ASC
			""".formatted(medicationFilter);

		return jdbcTemplate.query(sql, params, (rs, rowNum) -> DoctorMedicationReportResponse.builder()
			.patientId(rs.getLong("patient_id"))
			.patientName(rs.getString("patient_name"))
			.totalMedicationsIssued(rs.getLong("total_medications_issued"))
			.activeMedications(rs.getLong("active_medications"))
			.inactiveMedications(rs.getLong("inactive_medications"))
			.build());
	}

	private MapSqlParameterSource buildDateParams(LocalDate startDate, LocalDate endDate) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("startDate", startDate != null ? Date.valueOf(startDate) : null);
		params.addValue("endDate", endDate != null ? Date.valueOf(endDate) : null);
		return params;
	}

	private String buildDateFilter(String columnName, LocalDate startDate, LocalDate endDate, String tableAlias) {
		String qualifiedColumn = tableAlias + "." + columnName;
		StringBuilder filter = new StringBuilder();

		if (startDate != null) {
			filter.append(" AND ").append(qualifiedColumn).append(" >= :startDate");
		}

		if (endDate != null) {
			filter.append(" AND ").append(qualifiedColumn).append(" <= :endDate");
		}

		return filter.toString();
	}
}
