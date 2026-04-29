package dev.zisan.meditrack.medication.repository;

import dev.zisan.meditrack.medication.entity.Medication;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

	@EntityGraph(attributePaths = {"doctor", "doctor.user", "patient", "patient.user"})
	Page<Medication> findByPatientIdAndActiveOrderByStartDateDescIdDesc(Long patientId, boolean active, Pageable pageable);

	@EntityGraph(attributePaths = {"doctor", "doctor.user", "patient", "patient.user"})
	List<Medication> findAllByPatientId(Long patientId);

	@EntityGraph(attributePaths = {"doctor", "doctor.user", "patient", "patient.user"})
	List<Medication> findAllByDoctorId(Long doctorId);

	@EntityGraph(attributePaths = {"doctor", "doctor.user", "patient", "patient.user"})
	List<Medication> findAllBy();
}
