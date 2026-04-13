package dev.zisan.meditrack.patient.repository;

import dev.zisan.meditrack.patient.entity.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	@EntityGraph(attributePaths = "user")
	Optional<Patient> findWithUserById(Long id);

	Optional<Patient> findByUserId(Long userId);
}
