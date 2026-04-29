package dev.zisan.meditrack.search.service;

import dev.zisan.meditrack.search.document.DoctorSearchDocument;
import dev.zisan.meditrack.search.document.MedicationSearchDocument;
import dev.zisan.meditrack.search.document.PatientSearchDocument;
import dev.zisan.meditrack.search.dto.DoctorSearchResponse;
import dev.zisan.meditrack.search.dto.MedicationSearchResponse;
import dev.zisan.meditrack.search.dto.PatientSearchResponse;
import dev.zisan.meditrack.search.repository.DoctorSearchRepository;
import dev.zisan.meditrack.search.repository.MedicationSearchRepository;
import dev.zisan.meditrack.search.repository.PatientSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final DoctorSearchRepository doctorSearchRepository;
	private final PatientSearchRepository patientSearchRepository;
	private final MedicationSearchRepository medicationSearchRepository;

	public Page<DoctorSearchResponse> searchDoctors(String query, Pageable pageable) {
		Page<DoctorSearchDocument> doctors;

		if (query == null || query.isBlank()) {
			doctors = doctorSearchRepository.findByEnabledTrue(pageable);
		} else {
			doctors = doctorSearchRepository
				.findByEnabledTrueAndFullNameContainingOrEnabledTrueAndSpecializationContainingOrEnabledTrueAndHospitalAffiliationContaining(
					query,
					query,
					query,
					pageable
				);
		}

		return doctors.map(this::mapToDoctorSearchResponse);
	}

	public Page<PatientSearchResponse> searchPatients(String query, Pageable pageable) {
		Page<PatientSearchDocument> patients;

		if (query == null || query.isBlank()) {
			patients = patientSearchRepository.findByEnabledTrue(pageable);
		} else {
			patients = patientSearchRepository
				.findByEnabledTrueAndFullNameContainingOrEnabledTrueAndDiagnosesContainingOrEnabledTrueAndMedicalNotesContainingOrEnabledTrueAndMedicationsContaining(
					query,
					query,
					query,
					query,
					pageable
				);
		}

		return patients.map(this::mapToPatientSearchResponse);
	}

	public Page<MedicationSearchResponse> searchMedications(String query, Pageable pageable) {
		Page<MedicationSearchDocument> medications;

		if (query == null || query.isBlank()) {
			medications = medicationSearchRepository.findByVisibleTrue(pageable);
		} else {
			medications = medicationSearchRepository
				.findByVisibleTrueAndNameContainingOrVisibleTrueAndDosageContainingOrVisibleTrueAndFrequencyContainingOrVisibleTrueAndPatientNameContainingOrVisibleTrueAndDoctorNameContaining(
					query,
					query,
					query,
					query,
					query,
					pageable
				);
		}

		return medications.map(this::mapToMedicationSearchResponse);
	}

	private DoctorSearchResponse mapToDoctorSearchResponse(DoctorSearchDocument doctor) {
		return DoctorSearchResponse.builder()
			.id(doctor.getId())
			.userId(doctor.getUserId())
			.fullName(doctor.getFullName())
			.specialization(doctor.getSpecialization())
			.hospitalAffiliation(doctor.getHospitalAffiliation())
			.licenseNumber(doctor.getLicenseNumber())
			.build();
	}

	private PatientSearchResponse mapToPatientSearchResponse(PatientSearchDocument patient) {
		return PatientSearchResponse.builder()
			.id(patient.getId())
			.userId(patient.getUserId())
			.fullName(patient.getFullName())
			.gender(patient.getGender())
			.bloodType(patient.getBloodType())
			.diagnoses(patient.getDiagnoses())
			.medications(patient.getMedications())
			.build();
	}

	private MedicationSearchResponse mapToMedicationSearchResponse(MedicationSearchDocument medication) {
		return MedicationSearchResponse.builder()
			.id(medication.getId())
			.patientId(medication.getPatientId())
			.doctorId(medication.getDoctorId())
			.patientName(medication.getPatientName())
			.doctorName(medication.getDoctorName())
			.name(medication.getName())
			.dosage(medication.getDosage())
			.frequency(medication.getFrequency())
			.startDate(medication.getStartDate())
			.endDate(medication.getEndDate())
			.active(medication.isActive())
			.build();
	}
}
