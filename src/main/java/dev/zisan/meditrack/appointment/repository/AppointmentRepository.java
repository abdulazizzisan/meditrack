package dev.zisan.meditrack.appointment.repository;

import dev.zisan.meditrack.appointment.entity.Appointment;
import dev.zisan.meditrack.appointment.entity.AppointmentStatus;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	@EntityGraph(attributePaths = {"patient", "patient.user", "doctor", "doctor.user"})
	Optional<Appointment> findDetailsById(Long id);

	@EntityGraph(attributePaths = {"patient", "patient.user", "doctor", "doctor.user"})
	Page<Appointment> findByDoctorIdOrderByScheduledAtDesc(Long doctorId, Pageable pageable);

	boolean existsByDoctorIdAndScheduledAtAndStatusIn(Long doctorId, java.time.LocalDateTime scheduledAt,
			Collection<AppointmentStatus> statuses);
}
