package dev.zisan.meditrack.admin.service;

import dev.zisan.meditrack.admin.dto.AdminDashboardResponse;
import dev.zisan.meditrack.admin.dto.AdminUserResponse;
import dev.zisan.meditrack.admin.repository.AdminDashboardJdbcRepository;
import dev.zisan.meditrack.common.exception.BadRequestException;
import dev.zisan.meditrack.common.exception.ResourceNotFoundException;
import dev.zisan.meditrack.doctor.repository.DoctorRepository;
import dev.zisan.meditrack.patient.repository.PatientRepository;
import dev.zisan.meditrack.search.service.DoctorSearchIndexService;
import dev.zisan.meditrack.search.service.MedicationSearchIndexService;
import dev.zisan.meditrack.search.service.PatientSearchIndexService;
import dev.zisan.meditrack.user.entity.User;
import dev.zisan.meditrack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserRepository userRepository;
	private final AdminDashboardJdbcRepository adminDashboardJdbcRepository;
	private final DoctorRepository doctorRepository;
	private final DoctorSearchIndexService doctorSearchIndexService;
	private final PatientRepository patientRepository;
	private final PatientSearchIndexService patientSearchIndexService;
	private final MedicationSearchIndexService medicationSearchIndexService;

	@Transactional(readOnly = true)
	public Page<AdminUserResponse> getUsers(Pageable pageable) {
		return userRepository.findAll(pageable).map(this::mapToAdminUserResponse);
	}

	@Transactional
	public AdminUserResponse deactivateUser(Long userId, String currentAdminEmail) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

		if (user.getEmail().equalsIgnoreCase(currentAdminEmail)) {
			throw new BadRequestException("Admins cannot deactivate their own account.");
		}

		if (!user.isEnabled()) {
			throw new BadRequestException("User account is already deactivated.");
		}

		user.setEnabled(false);
		doctorRepository.findByUserId(userId).ifPresent(doctorSearchIndexService::indexDoctor);
		patientRepository.findByUserId(userId).ifPresent(patient -> {
			patientSearchIndexService.indexPatient(patient);
			medicationSearchIndexService.reindexByPatientId(patient.getId());
		});
		doctorRepository.findByUserId(userId).ifPresent(doctor -> medicationSearchIndexService.reindexByDoctorId(doctor.getId()));
		return mapToAdminUserResponse(user);
	}

	@Transactional(readOnly = true)
	public AdminDashboardResponse getDashboard() {
		return adminDashboardJdbcRepository.getDashboardSummary();
	}

	private AdminUserResponse mapToAdminUserResponse(User user) {
		return AdminUserResponse.builder()
			.id(user.getId())
			.fullName(user.getFullName())
			.email(user.getEmail())
			.role(user.getRole())
			.enabled(user.isEnabled())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}
}
