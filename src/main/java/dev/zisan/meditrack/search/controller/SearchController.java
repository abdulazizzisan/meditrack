package dev.zisan.meditrack.search.controller;

import dev.zisan.meditrack.common.api.ApiResponse;
import dev.zisan.meditrack.search.dto.DoctorSearchResponse;
import dev.zisan.meditrack.search.dto.MedicationSearchResponse;
import dev.zisan.meditrack.search.dto.PatientSearchResponse;
import dev.zisan.meditrack.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@GetMapping("/doctors")
	@PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR') or hasAuthority('ADMIN')")
	public ResponseEntity<ApiResponse<Page<DoctorSearchResponse>>> searchDoctors(
			@RequestParam(name = "q", required = false) String query,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<DoctorSearchResponse> result = searchService.searchDoctors(query, pageable);

		ApiResponse<Page<DoctorSearchResponse>> response = ApiResponse.<Page<DoctorSearchResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Doctor search completed successfully.")
			.data(result)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/patients")
	@PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('ADMIN')")
	public ResponseEntity<ApiResponse<Page<PatientSearchResponse>>> searchPatients(
			@RequestParam(name = "q", required = false) String query,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<PatientSearchResponse> result = searchService.searchPatients(query, pageable);

		ApiResponse<Page<PatientSearchResponse>> response = ApiResponse.<Page<PatientSearchResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Patient search completed successfully.")
			.data(result)
			.build();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/medications")
	@PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('ADMIN')")
	public ResponseEntity<ApiResponse<Page<MedicationSearchResponse>>> searchMedications(
			@RequestParam(name = "q", required = false) String query,
			@PageableDefault(size = 10) Pageable pageable) {
		Page<MedicationSearchResponse> result = searchService.searchMedications(query, pageable);

		ApiResponse<Page<MedicationSearchResponse>> response = ApiResponse.<Page<MedicationSearchResponse>>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Medication search completed successfully.")
			.data(result)
			.build();

		return ResponseEntity.ok(response);
	}
}
