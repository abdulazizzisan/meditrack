package dev.zisan.meditrack.medication.controller;

import dev.zisan.meditrack.common.api.ApiResponse;
import dev.zisan.meditrack.medication.dto.CreateMedicationRequest;
import dev.zisan.meditrack.medication.dto.MedicationResponse;
import dev.zisan.meditrack.medication.service.MedicationService;
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
@RequestMapping("/v1/patients/{patientId}/medications")
public class MedicationController {

	private final MedicationService medicationService;

	public MedicationController(MedicationService medicationService) {
		this.medicationService = medicationService;
	}

	@GetMapping
	@PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('ADMIN') or (hasAuthority('PATIENT') and @patientSecurity.isOwner(authentication, #patientId))")
	public ResponseEntity<ApiResponse<Page<MedicationResponse>>> getPatientMedications(
			@PathVariable Long patientId,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<MedicationResponse> medications = medicationService.getPatientMedications(patientId, pageable);

		ApiResponse<Page<MedicationResponse>> response = ApiResponse.<Page<MedicationResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Active medications retrieved successfully.")
			.data(medications)
			.build();

		return ResponseEntity.ok(response);
	}

	@PostMapping
	@PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('DOCTOR') and @doctorSecurity.isOwner(authentication, #request.doctorId()))")
	public ResponseEntity<ApiResponse<MedicationResponse>> prescribeMedication(
			@PathVariable Long patientId,
			@Valid @RequestBody CreateMedicationRequest request) {
		MedicationResponse medication = medicationService.prescribeMedication(patientId, request);

		ApiResponse<MedicationResponse> response = ApiResponse.<MedicationResponse>builder()
			.statusCode(HttpStatus.CREATED.value())
			.message("Medication prescribed successfully.")
			.data(medication)
			.build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
