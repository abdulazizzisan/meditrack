package dev.zisan.meditrack.security;

import dev.zisan.meditrack.appointment.repository.AppointmentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("appointmentSecurity")
public class AppointmentSecurity {

	private final AppointmentRepository appointmentRepository;

	public AppointmentSecurity(AppointmentRepository appointmentRepository) {
		this.appointmentRepository = appointmentRepository;
	}

	public boolean canAccess(Authentication authentication, Long appointmentId) {
		return isPatientOwner(authentication, appointmentId) || isDoctorOwner(authentication, appointmentId);
	}

	public boolean isPatientOwner(Authentication authentication, Long appointmentId) {
		if (authentication == null || authentication.getName() == null) {
			return false;
		}

		return appointmentRepository.findDetailsById(appointmentId)
			.map(appointment -> appointment.getPatient().getUser().getEmail().equals(authentication.getName()))
			.orElse(false);
	}

	public boolean isDoctorOwner(Authentication authentication, Long appointmentId) {
		if (authentication == null || authentication.getName() == null) {
			return false;
		}

		return appointmentRepository.findDetailsById(appointmentId)
			.map(appointment -> appointment.getDoctor().getUser().getEmail().equals(authentication.getName()))
			.orElse(false);
	}
}
