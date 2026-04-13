package dev.zisan.meditrack.patient.service;

import dev.zisan.meditrack.common.exception.ResourceNotFoundException;
import dev.zisan.meditrack.patient.dto.PatientResponse;
import dev.zisan.meditrack.patient.dto.UpdatePatientRequest;
import dev.zisan.meditrack.patient.entity.Patient;
import dev.zisan.meditrack.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

	private final PatientRepository patientRepository;

	public PatientService(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;
	}

	@Transactional(readOnly = true)
	public PatientResponse getPatientById(Long patientId) {
		Patient patient = findPatientById(patientId);
		return mapToResponse(patient);
	}

	@Transactional
	public PatientResponse updatePatient(Long patientId, UpdatePatientRequest request) {
		Patient patient = findPatientById(patientId);

		patient.getUser().setFullName(request.fullName());
		patient.setDateOfBirth(request.dateOfBirth());
		patient.setGender(request.gender());
		patient.setBloodType(request.bloodType());
		patient.setPhone(request.phone());
		patient.setAddress(request.address());

		return mapToResponse(patient);
	}

	private Patient findPatientById(Long patientId) {
		return patientRepository.findWithUserById(patientId)
			.orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));
	}

	private PatientResponse mapToResponse(Patient patient) {
		return PatientResponse.builder()
			.id(patient.getId())
			.userId(patient.getUser().getId())
			.fullName(patient.getUser().getFullName())
			.email(patient.getUser().getEmail())
			.dateOfBirth(patient.getDateOfBirth())
			.gender(patient.getGender())
			.bloodType(patient.getBloodType())
			.phone(patient.getPhone())
			.address(patient.getAddress())
			.createdAt(patient.getCreatedAt())
			.updatedAt(patient.getUpdatedAt())
			.build();
	}
}
