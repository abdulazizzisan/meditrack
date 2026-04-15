package dev.zisan.meditrack.search.repository;

import dev.zisan.meditrack.search.document.DoctorSearchDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DoctorSearchRepository extends ElasticsearchRepository<DoctorSearchDocument, Long> {

	Page<DoctorSearchDocument> findByEnabledTrueAndFullNameContainingOrEnabledTrueAndSpecializationContainingOrEnabledTrueAndHospitalAffiliationContaining(
			String fullName,
			String specialization,
			String hospitalAffiliation,
			Pageable pageable);

	Page<DoctorSearchDocument> findByEnabledTrue(Pageable pageable);
}
