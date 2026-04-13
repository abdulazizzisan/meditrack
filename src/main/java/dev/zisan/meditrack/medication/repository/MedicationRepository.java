package dev.zisan.meditrack.medication.repository;

import dev.zisan.meditrack.medication.entity.Medication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {

	@EntityGraph(attributePaths = {"doctor", "doctor.user", "patient", "patient.user"})
	Page<Medication> findByPatientIdAndActiveOrderByStartDateDescIdDesc(Long patientId, boolean active, Pageable pageable);
}
