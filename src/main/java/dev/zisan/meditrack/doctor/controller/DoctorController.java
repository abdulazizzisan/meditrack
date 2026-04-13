package dev.zisan.meditrack.doctor.controller;

import dev.zisan.meditrack.common.api.ApiResponse;
import dev.zisan.meditrack.doctor.dto.DoctorResponse;
import dev.zisan.meditrack.doctor.dto.UpdateDoctorRequest;
import dev.zisan.meditrack.doctor.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/doctors")
public class DoctorController {

	private final DoctorService doctorService;

	public DoctorController(DoctorService doctorService) {
		this.doctorService = doctorService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('PATIENT') or hasAuthority('ADMIN') or hasAuthority('DOCTOR')")
	public ResponseEntity<ApiResponse<Page<DoctorResponse>>> getDoctors(
			@RequestParam(required = false) String specialization,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<DoctorResponse> doctors = doctorService.getDoctors(specialization, pageable);

		ApiResponse<Page<DoctorResponse>> response = ApiResponse.<Page<DoctorResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Doctors retrieved successfully.")
			.data(doctors)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('PATIENT') or hasAuthority('ADMIN') or hasAuthority('DOCTOR')")
	public ResponseEntity<ApiResponse<DoctorResponse>> getDoctor(@PathVariable Long id) {
		DoctorResponse doctor = doctorService.getDoctorById(id);

		ApiResponse<DoctorResponse> response = ApiResponse.<DoctorResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Doctor profile retrieved successfully.")
			.data(doctor)
			.build();

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('DOCTOR') and @doctorSecurity.isOwner(authentication, #id))")
	public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctor(
			@PathVariable Long id,
			@Valid @RequestBody UpdateDoctorRequest request) {
		DoctorResponse doctor = doctorService.updateDoctor(id, request);

		ApiResponse<DoctorResponse> response = ApiResponse.<DoctorResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Doctor profile updated successfully.")
			.data(doctor)
			.build();

		return ResponseEntity.ok(response);
	}
}
