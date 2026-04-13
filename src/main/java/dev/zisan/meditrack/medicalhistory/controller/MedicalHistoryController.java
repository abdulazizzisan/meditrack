package dev.zisan.meditrack.medicalhistory.controller;

import dev.zisan.meditrack.common.api.ApiResponse;
import dev.zisan.meditrack.medicalhistory.dto.CreateMedicalHistoryRequest;
import dev.zisan.meditrack.medicalhistory.dto.MedicalHistoryResponse;
import dev.zisan.meditrack.medicalhistory.service.MedicalHistoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/patients/{patientId}/history")
public class MedicalHistoryController {

	private final MedicalHistoryService medicalHistoryService;

	public MedicalHistoryController(MedicalHistoryService medicalHistoryService) {
		this.medicalHistoryService = medicalHistoryService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('ADMIN') or (hasAuthority('PATIENT') and @patientSecurity.isOwner(authentication, #patientId))")
	public ResponseEntity<ApiResponse<Page<MedicalHistoryResponse>>> getPatientHistory(
			@PathVariable Long patientId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<MedicalHistoryResponse> histories = medicalHistoryService.getPatientHistory(patientId, pageable);

		ApiResponse<Page<MedicalHistoryResponse>> response = ApiResponse.<Page<MedicalHistoryResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Medical history retrieved successfully.")
			.data(histories)
			.build();

		return ResponseEntity.ok(response);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('DOCTOR') and @doctorSecurity.isOwner(authentication, #request.doctorId()))")
	public ResponseEntity<ApiResponse<MedicalHistoryResponse>> addMedicalHistory(
			@PathVariable Long patientId,
			@Valid @RequestBody CreateMedicalHistoryRequest request) {
		MedicalHistoryResponse history = medicalHistoryService.addMedicalHistory(patientId, request);

		ApiResponse<MedicalHistoryResponse> response = ApiResponse.<MedicalHistoryResponse>builder()
			.statusCode(HttpStatus.CREATED.value())
			.message("Medical history entry created successfully.")
			.data(history)
			.build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
