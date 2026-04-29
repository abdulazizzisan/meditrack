package dev.zisan.meditrack.search.repository;

import dev.zisan.meditrack.search.document.PatientSearchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PatientSearchRepository extends ElasticsearchRepository<PatientSearchDocument, Long> {

	Page<PatientSearchDocument> findByEnabledTrue(Pageable pageable);

	Page<PatientSearchDocument> findByEnabledTrueAndFullNameContainingOrEnabledTrueAndDiagnosesContainingOrEnabledTrueAndMedicalNotesContainingOrEnabledTrueAndMedicationsContaining(
			String fullName,
			String diagnoses,
			String medicalNotes,
			String medications,
			Pageable pageable);
}
