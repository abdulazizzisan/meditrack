package dev.zisan.meditrack.patient.repository;

import dev.zisan.meditrack.patient.entity.Patient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	Optional<Patient> findByUserId(Long userId);
}
