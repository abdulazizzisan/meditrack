package dev.zisan.meditrack.medication.service;

import dev.zisan.meditrack.common.exception.BadRequestException;
import dev.zisan.meditrack.common.exception.ResourceNotFoundException;
import dev.zisan.meditrack.doctor.entity.Doctor;
import dev.zisan.meditrack.doctor.repository.DoctorRepository;
import dev.zisan.meditrack.medication.dto.CreateMedicationRequest;
import dev.zisan.meditrack.medication.dto.MedicationResponse;
import dev.zisan.meditrack.medication.entity.Medication;
import dev.zisan.meditrack.medication.repository.MedicationRepository;
import dev.zisan.meditrack.patient.entity.Patient;
import dev.zisan.meditrack.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicationService {

	private final MedicationRepository medicationRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;

	@Transactional(readOnly = true)
	public Page<MedicationResponse> getPatientMedications(Long patientId, Pageable pageable) {
		ensurePatientExists(patientId);
		return medicationRepository.findByPatientIdAndActiveOrderByStartDateDescIdDesc(patientId, true, pageable)
			.map(this::toResponse);
	}

	@Transactional
	public MedicationResponse prescribeMedication(Long patientId, CreateMedicationRequest request) {
		if (request.endDate() != null && request.endDate().isBefore(request.startDate())) {
			throw new BadRequestException("Medication end date cannot be before the start date.");
		}

		Patient patient = patientRepository.findWithUserById(patientId)
			.orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

		Doctor doctor = doctorRepository.findWithUserById(request.doctorId())
			.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.doctorId()));

		Medication medication = Medication.builder()
			.patient(patient)
			.doctor(doctor)
			.name(request.name())
			.dosage(request.dosage())
			.frequency(request.frequency())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.active(request.active() == null || request.active())
			.build();

		return toResponse(medicationRepository.save(medication));
	}

	private void ensurePatientExists(Long patientId) {
		if (!patientRepository.existsById(patientId)) {
			throw new ResourceNotFoundException("Patient not found with id: " + patientId);
		}
	}

	private MedicationResponse toResponse(Medication medication) {
		return MedicationResponse.builder()
			.id(medication.getId())
			.patientId(medication.getPatient().getId())
			.doctorId(medication.getDoctor().getId())
			.doctorName(medication.getDoctor().getUser().getFullName())
			.name(medication.getName())
			.dosage(medication.getDosage())
			.frequency(medication.getFrequency())
			.startDate(medication.getStartDate())
			.endDate(medication.getEndDate())
			.active(medication.isActive())
			.createdAt(medication.getCreatedAt())
			.updatedAt(medication.getUpdatedAt())
			.build();
	}
}
