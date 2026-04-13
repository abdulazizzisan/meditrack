package dev.zisan.meditrack.security;

import dev.zisan.meditrack.patient.repository.PatientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("patientSecurity")
public class PatientSecurity {

	private final PatientRepository patientRepository;

	public PatientSecurity(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	public boolean isOwner(Authentication authentication, Long patientId) {
		if (authentication == null || authentication.getName() == null) {
			return false;
		}

		return patientRepository.findWithUserById(patientId)
			.map(patient -> patient.getUser().getEmail().equals(authentication.getName()))
			.orElse(false);
	}
}
