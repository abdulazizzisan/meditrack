package dev.zisan.meditrack.medicalhistory.repository;

import dev.zisan.meditrack.medicalhistory.entity.MedicalHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {

	@EntityGraph(attributePaths = {"doctor", "doctor.user", "patient", "patient.user"})
	Page<MedicalHistory> findByPatientIdOrderByVisitDateDescIdDesc(Long patientId, Pageable pageable);
}
