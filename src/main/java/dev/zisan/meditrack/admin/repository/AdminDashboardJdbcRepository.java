package dev.zisan.meditrack.admin.repository;

import dev.zisan.meditrack.admin.dto.AdminDashboardResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminDashboardJdbcRepository {

	private static final String DASHBOARD_SQL = """
		SELECT
		    (SELECT COUNT(*) FROM users) AS total_users,
		    (SELECT COUNT(*) FROM users WHERE enabled = TRUE) AS active_users,
		    (SELECT COUNT(*) FROM patients) AS total_patients,
		    (SELECT COUNT(*) FROM doctors) AS total_doctors,
		    (SELECT COUNT(*) FROM appointments) AS total_appointments,
		    (SELECT COUNT(*) FROM appointments WHERE status = 'PENDING') AS pending_appointments,
		    (SELECT COUNT(*) FROM appointments WHERE status = 'CONFIRMED') AS confirmed_appointments,
		    (SELECT COUNT(*) FROM appointments WHERE status = 'COMPLETED') AS completed_appointments,
		    (SELECT COUNT(*) FROM appointments WHERE status = 'CANCELLED') AS cancelled_appointments,
		    (SELECT COUNT(*) FROM medical_histories) AS total_medical_history_entries,
		    (SELECT COUNT(*) FROM medications WHERE is_active = TRUE) AS active_medications
		""";

	private final JdbcTemplate jdbcTemplate;

	public AdminDashboardJdbcRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public AdminDashboardResponse getDashboardSummary() {
		return jdbcTemplate.queryForObject(DASHBOARD_SQL, (rs, rowNum) -> AdminDashboardResponse.builder()
			.totalUsers(rs.getLong("total_users"))
			.activeUsers(rs.getLong("active_users"))
			.totalPatients(rs.getLong("total_patients"))
			.totalDoctors(rs.getLong("total_doctors"))
			.totalAppointments(rs.getLong("total_appointments"))
			.pendingAppointments(rs.getLong("pending_appointments"))
			.confirmedAppointments(rs.getLong("confirmed_appointments"))
			.completedAppointments(rs.getLong("completed_appointments"))
			.cancelledAppointments(rs.getLong("cancelled_appointments"))
			.totalMedicalHistoryEntries(rs.getLong("total_medical_history_entries"))
			.activeMedications(rs.getLong("active_medications"))
			.build());
	}
}
