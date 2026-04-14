package dev.zisan.meditrack.admin.controller;

import dev.zisan.meditrack.admin.dto.AdminDashboardResponse;
import dev.zisan.meditrack.admin.dto.AdminUserResponse;
import dev.zisan.meditrack.admin.service.AdminService;
import dev.zisan.meditrack.common.api.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminController {

	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/users")
	public ResponseEntity<ApiResponse<Page<AdminUserResponse>>> getUsers(
			@PageableDefault(size = 10) Pageable pageable) {
		Page<AdminUserResponse> users = adminService.getUsers(pageable);

		ApiResponse<Page<AdminUserResponse>> response = ApiResponse.<Page<AdminUserResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Users retrieved successfully.")
			.data(users)
			.build();

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<ApiResponse<AdminUserResponse>> deactivateUser(
			@PathVariable Long id,
			Authentication authentication) {
		AdminUserResponse user = adminService.deactivateUser(id, authentication.getName());

		ApiResponse<AdminUserResponse> response = ApiResponse.<AdminUserResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("User deactivated successfully.")
			.data(user)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/dashboard")
	public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
		AdminDashboardResponse dashboard = adminService.getDashboard();

		ApiResponse<AdminDashboardResponse> response = ApiResponse.<AdminDashboardResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Admin dashboard data retrieved successfully.")
			.data(dashboard)
			.build();

		return ResponseEntity.ok(response);
	}
}
