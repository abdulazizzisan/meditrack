package dev.zisan.meditrack.patient.controller;

import dev.zisan.meditrack.common.api.ApiResponse;
import dev.zisan.meditrack.patient.dto.PatientResponse;
import dev.zisan.meditrack.patient.dto.UpdatePatientRequest;
import dev.zisan.meditrack.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/patients")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService patientService;

	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_DOCTOR') or hasAuthority('ROLE_ADMIN') or (hasAuthority('ROLE_PATIENT') and @patientSecurity.isOwner(authentication, #id))")
	public ResponseEntity<ApiResponse<PatientResponse>> getPatient(@PathVariable Long id) {
		PatientResponse patient = patientService.getPatientById(id);

		ApiResponse<PatientResponse> response = ApiResponse.<PatientResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Patient profile retrieved successfully.")
			.data(patient)
			.build();

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN') or (hasAuthority('ROLE_PATIENT') and @patientSecurity.isOwner(authentication, #id))")
	public ResponseEntity<ApiResponse<PatientResponse>> updatePatient(
			@PathVariable Long id,
			@Valid @RequestBody UpdatePatientRequest request) {
		PatientResponse patient = patientService.updatePatient(id, request);

		ApiResponse<PatientResponse> response = ApiResponse.<PatientResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Patient profile updated successfully.")
			.data(patient)
			.build();

		return ResponseEntity.ok(response);
	}
}
