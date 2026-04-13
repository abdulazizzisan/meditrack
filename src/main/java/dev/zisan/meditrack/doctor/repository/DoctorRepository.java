package dev.zisan.meditrack.doctor.repository;

import dev.zisan.meditrack.doctor.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

	@EntityGraph(attributePaths = "user")
	Optional<Doctor> findWithUserById(Long id);

	Optional<Doctor> findByUserId(Long userId);

	Optional<Doctor> findByLicenseNumber(String licenseNumber);

	Page<Doctor> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);
}
