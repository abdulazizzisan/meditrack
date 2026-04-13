package dev.zisan.meditrack.doctor.service;

import dev.zisan.meditrack.common.exception.ResourceNotFoundException;
import dev.zisan.meditrack.doctor.dto.DoctorResponse;
import dev.zisan.meditrack.doctor.dto.UpdateDoctorRequest;
import dev.zisan.meditrack.doctor.entity.Doctor;
import dev.zisan.meditrack.doctor.repository.DoctorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorService {

	private final DoctorRepository doctorRepository;

	public DoctorService(DoctorRepository doctorRepository) {
		this.doctorRepository = doctorRepository;
	}

	@Transactional(readOnly = true)
	public Page<DoctorResponse> getDoctors(String specialization, Pageable pageable) {
		if (specialization != null && !specialization.isBlank()) {
			return doctorRepository.findBySpecializationContainingIgnoreCase(specialization, pageable)
				.map(this::mapToResponse);
		}

		return doctorRepository.findAll(pageable).map(this::mapToResponse);
	}

	@Transactional(readOnly = true)
	public DoctorResponse getDoctorById(Long doctorId) {
		Doctor doctor = findDoctorById(doctorId);
		return mapToResponse(doctor);
	}

	@Transactional
	public DoctorResponse updateDoctor(Long doctorId, UpdateDoctorRequest request) {
		Doctor doctor = findDoctorById(doctorId);

		doctor.getUser().setFullName(request.fullName());
		doctor.setSpecialization(request.specialization());
		doctor.setHospitalAffiliation(request.hospitalAffiliation());

		return mapToResponse(doctor);
	}

	private Doctor findDoctorById(Long doctorId) {
		return doctorRepository.findWithUserById(doctorId)
			.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));
	}

	private DoctorResponse mapToResponse(Doctor doctor) {
		return DoctorResponse.builder()
			.id(doctor.getId())
			.userId(doctor.getUser().getId())
			.fullName(doctor.getUser().getFullName())
			.email(doctor.getUser().getEmail())
			.specialization(doctor.getSpecialization())
			.licenseNumber(doctor.getLicenseNumber())
			.hospitalAffiliation(doctor.getHospitalAffiliation())
			.createdAt(doctor.getCreatedAt())
			.updatedAt(doctor.getUpdatedAt())
			.build();
	}
}
