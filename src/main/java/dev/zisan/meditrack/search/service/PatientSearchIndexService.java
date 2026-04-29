package dev.zisan.meditrack.search.service;

import dev.zisan.meditrack.medicalhistory.repository.MedicalHistoryRepository;
import dev.zisan.meditrack.medication.repository.MedicationRepository;
import dev.zisan.meditrack.patient.entity.Patient;
import dev.zisan.meditrack.patient.repository.PatientRepository;
import dev.zisan.meditrack.search.document.PatientSearchDocument;
import dev.zisan.meditrack.search.repository.PatientSearchRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PatientSearchIndexService {

	private final PatientRepository patientRepository;
	private final MedicalHistoryRepository medicalHistoryRepository;
	private final MedicationRepository medicationRepository;
	private final PatientSearchRepository patientSearchRepository;

	public PatientSearchIndexService(PatientRepository patientRepository,
			MedicalHistoryRepository medicalHistoryRepository,
			MedicationRepository medicationRepository,
			PatientSearchRepository patientSearchRepository) {
		this.patientRepository = patientRepository;
		this.medicalHistoryRepository = medicalHistoryRepository;
		this.medicationRepository = medicationRepository;
		this.patientSearchRepository = patientSearchRepository;
	}

	public void indexPatientById(Long patientId) {
		patientRepository.findWithUserById(patientId).ifPresent(this::indexPatient);
	}

	public void indexPatient(Patient patient) {
		try {
			patientSearchRepository.save(mapToDocument(patient));
		} catch (Exception exception) {
			log.warn("Failed to index patient {}: {}", patient.getId(), exception.getMessage());
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void reindexAllPatients() {
		try {
			List<PatientSearchDocument> documents = patientRepository.findAllBy().stream()
				.map(this::mapToDocument)
				.toList();
			patientSearchRepository.saveAll(documents);
			log.info("Reindexed {} patients into Elasticsearch", documents.size());
		} catch (Exception exception) {
			log.warn("Patient Elasticsearch reindex skipped: {}", exception.getMessage());
		}
	}

	private PatientSearchDocument mapToDocument(Patient patient) {
		return PatientSearchDocument.builder()
			.id(patient.getId())
			.userId(patient.getUser().getId())
			.fullName(patient.getUser().getFullName())
			.gender(patient.getGender())
			.bloodType(patient.getBloodType())
			.phone(patient.getPhone())
			.diagnoses(medicalHistoryRepository.findAllByPatientId(patient.getId()).stream()
				.map(history -> history.getDiagnosis())
				.toList())
			.medicalNotes(medicalHistoryRepository.findAllByPatientId(patient.getId()).stream()
				.map(history -> history.getNotes())
				.filter(note -> note != null && !note.isBlank())
				.toList())
			.medications(medicationRepository.findAllByPatientId(patient.getId()).stream()
				.map(medication -> medication.getName())
				.toList())
			.enabled(patient.getUser().isEnabled())
			.build();
	}
}
