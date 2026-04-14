package dev.zisan.meditrack.admin.service;

import dev.zisan.meditrack.admin.dto.AdminDashboardResponse;
import dev.zisan.meditrack.admin.dto.AdminUserResponse;
import dev.zisan.meditrack.admin.repository.AdminDashboardJdbcRepository;
import dev.zisan.meditrack.common.exception.BadRequestException;
import dev.zisan.meditrack.common.exception.ResourceNotFoundException;
import dev.zisan.meditrack.user.entity.User;
import dev.zisan.meditrack.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

	private final UserRepository userRepository;
	private final AdminDashboardJdbcRepository adminDashboardJdbcRepository;

	public AdminService(UserRepository userRepository, AdminDashboardJdbcRepository adminDashboardJdbcRepository) {
		this.userRepository = userRepository;
		this.adminDashboardJdbcRepository = adminDashboardJdbcRepository;
	}

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
