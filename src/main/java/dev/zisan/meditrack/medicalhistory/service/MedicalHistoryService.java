package dev.zisan.meditrack.medicalhistory.service;

import dev.zisan.meditrack.common.exception.ResourceNotFoundException;
import dev.zisan.meditrack.doctor.entity.Doctor;
import dev.zisan.meditrack.doctor.repository.DoctorRepository;
import dev.zisan.meditrack.medicalhistory.dto.CreateMedicalHistoryRequest;
import dev.zisan.meditrack.medicalhistory.dto.MedicalHistoryResponse;
import dev.zisan.meditrack.medicalhistory.entity.MedicalHistory;
import dev.zisan.meditrack.medicalhistory.repository.MedicalHistoryRepository;
import dev.zisan.meditrack.patient.entity.Patient;
import dev.zisan.meditrack.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedicalHistoryService {

	private final MedicalHistoryRepository medicalHistoryRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;

	@Transactional(readOnly = true)
	public Page<MedicalHistoryResponse> getPatientHistory(Long patientId, Pageable pageable) {
		ensurePatientExists(patientId);
		return medicalHistoryRepository.findByPatientIdOrderByVisitDateDescIdDesc(patientId, pageable)
			.map(this::toResponse);
	}

	@Transactional
	public MedicalHistoryResponse addMedicalHistory(Long patientId, CreateMedicalHistoryRequest request) {
		Patient patient = patientRepository.findWithUserById(patientId)
			.orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

		Doctor doctor = doctorRepository.findWithUserById(request.doctorId())
			.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.doctorId()));

		MedicalHistory medicalHistory = MedicalHistory.builder()
			.patient(patient)
			.doctor(doctor)
			.diagnosis(request.diagnosis())
			.notes(request.notes())
			.visitDate(request.visitDate())
			.build();

		return toResponse(medicalHistoryRepository.save(medicalHistory));
	}

	private void ensurePatientExists(Long patientId) {
		if (!patientRepository.existsById(patientId)) {
			throw new ResourceNotFoundException("Patient not found with id: " + patientId);
		}
	}

	private MedicalHistoryResponse toResponse(MedicalHistory medicalHistory) {
		return MedicalHistoryResponse.builder()
			.id(medicalHistory.getId())
			.patientId(medicalHistory.getPatient().getId())
			.doctorId(medicalHistory.getDoctor().getId())
			.doctorName(medicalHistory.getDoctor().getUser().getFullName())
			.diagnosis(medicalHistory.getDiagnosis())
			.notes(medicalHistory.getNotes())
			.visitDate(medicalHistory.getVisitDate())
			.createdAt(medicalHistory.getCreatedAt())
			.updatedAt(medicalHistory.getUpdatedAt())
			.build();
	}
}
