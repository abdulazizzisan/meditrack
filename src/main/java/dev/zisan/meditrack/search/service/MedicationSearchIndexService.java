package dev.zisan.meditrack.search.service;

import dev.zisan.meditrack.medication.entity.Medication;
import dev.zisan.meditrack.medication.repository.MedicationRepository;
import dev.zisan.meditrack.search.document.MedicationSearchDocument;
import dev.zisan.meditrack.search.repository.MedicationSearchRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MedicationSearchIndexService {

	private final MedicationRepository medicationRepository;
	private final MedicationSearchRepository medicationSearchRepository;

	public MedicationSearchIndexService(MedicationRepository medicationRepository,
			MedicationSearchRepository medicationSearchRepository) {
		this.medicationRepository = medicationRepository;
		this.medicationSearchRepository = medicationSearchRepository;
	}

	public void indexMedication(Medication medication) {
		try {
			medicationSearchRepository.save(mapToDocument(medication));
		} catch (Exception exception) {
			log.warn("Failed to index medication {}: {}", medication.getId(), exception.getMessage());
		}
	}

	public void reindexByPatientId(Long patientId) {
		medicationRepository.findAllByPatientId(patientId).forEach(this::indexMedication);
	}

	public void reindexByDoctorId(Long doctorId) {
		medicationRepository.findAllByDoctorId(doctorId).forEach(this::indexMedication);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void reindexAllMedications() {
		try {
			List<MedicationSearchDocument> documents = medicationRepository.findAllBy().stream()
				.map(this::mapToDocument)
				.toList();
			medicationSearchRepository.saveAll(documents);
			log.info("Reindexed {} medications into Elasticsearch", documents.size());
		} catch (Exception exception) {
			log.warn("Medication Elasticsearch reindex skipped: {}", exception.getMessage());
		}
	}

	private MedicationSearchDocument mapToDocument(Medication medication) {
		return MedicationSearchDocument.builder()
			.id(medication.getId())
			.patientId(medication.getPatient().getId())
			.doctorId(medication.getDoctor().getId())
			.patientName(medication.getPatient().getUser().getFullName())
			.doctorName(medication.getDoctor().getUser().getFullName())
			.name(medication.getName())
			.dosage(medication.getDosage())
			.frequency(medication.getFrequency())
			.startDate(medication.getStartDate())
			.endDate(medication.getEndDate())
			.active(medication.isActive())
			.visible(medication.getPatient().getUser().isEnabled() && medication.getDoctor().getUser().isEnabled())
			.build();
	}
}
