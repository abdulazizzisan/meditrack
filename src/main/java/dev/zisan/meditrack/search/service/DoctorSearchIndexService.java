package dev.zisan.meditrack.search.service;

import dev.zisan.meditrack.doctor.entity.Doctor;
import dev.zisan.meditrack.doctor.repository.DoctorRepository;
import dev.zisan.meditrack.search.document.DoctorSearchDocument;
import dev.zisan.meditrack.search.repository.DoctorSearchRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorSearchIndexService {

	private final DoctorRepository doctorRepository;
	private final DoctorSearchRepository doctorSearchRepository;

	public void indexDoctor(Doctor doctor) {
		try {
			doctorSearchRepository.save(mapToDocument(doctor));
		} catch (Exception exception) {
			log.warn("Failed to index doctor {}: {}", doctor.getId(), exception.getMessage());
		}
	}

	public void indexDoctorById(Long doctorId) {
		doctorRepository.findWithUserById(doctorId).ifPresent(this::indexDoctor);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void reindexAllDoctors() {
		try {
			List<DoctorSearchDocument> documents = doctorRepository.findAllBy().stream()
				.map(this::mapToDocument)
				.toList();
			doctorSearchRepository.saveAll(documents);
			log.info("Reindexed {} doctors into Elasticsearch", documents.size());
		} catch (Exception exception) {
			log.warn("Doctor Elasticsearch reindex skipped: {}", exception.getMessage());
		}
	}

	private DoctorSearchDocument mapToDocument(Doctor doctor) {
		return DoctorSearchDocument.builder()
			.id(doctor.getId())
			.userId(doctor.getUser().getId())
			.fullName(doctor.getUser().getFullName())
			.specialization(doctor.getSpecialization())
			.hospitalAffiliation(doctor.getHospitalAffiliation())
			.licenseNumber(doctor.getLicenseNumber())
			.enabled(doctor.getUser().isEnabled())
			.build();
	}
}
