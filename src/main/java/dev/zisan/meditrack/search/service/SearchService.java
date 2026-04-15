package dev.zisan.meditrack.search.service;

import dev.zisan.meditrack.search.document.DoctorSearchDocument;
import dev.zisan.meditrack.search.dto.DoctorSearchResponse;
import dev.zisan.meditrack.search.repository.DoctorSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

	private final DoctorSearchRepository doctorSearchRepository;

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
}
