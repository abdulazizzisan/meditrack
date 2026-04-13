package dev.zisan.meditrack.security;

import dev.zisan.meditrack.doctor.repository.DoctorRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("doctorSecurity")
public class DoctorSecurity {

	private final DoctorRepository doctorRepository;

	public DoctorSecurity(DoctorRepository doctorRepository) {
		this.doctorRepository = doctorRepository;
	}

	public boolean isOwner(Authentication authentication, Long doctorId) {
		if (authentication == null || authentication.getName() == null) {
			return false;
		}

		return doctorRepository.findWithUserById(doctorId)
			.map(doctor -> doctor.getUser().getEmail().equals(authentication.getName()))
			.orElse(false);
	}
}
