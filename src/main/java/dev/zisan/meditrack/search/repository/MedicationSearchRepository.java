package dev.zisan.meditrack.search.repository;

import dev.zisan.meditrack.search.document.MedicationSearchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MedicationSearchRepository extends ElasticsearchRepository<MedicationSearchDocument, Long> {

	Page<MedicationSearchDocument> findByVisibleTrue(Pageable pageable);

	Page<MedicationSearchDocument> findByVisibleTrueAndNameContainingOrVisibleTrueAndDosageContainingOrVisibleTrueAndFrequencyContainingOrVisibleTrueAndPatientNameContainingOrVisibleTrueAndDoctorNameContaining(
			String name,
			String dosage,
			String frequency,
			String patientName,
			String doctorName,
			Pageable pageable);
}
