package dev.zisan.meditrack.ai.controller;

import dev.zisan.meditrack.ai.dto.NoteSummaryResponse;
import dev.zisan.meditrack.ai.dto.SummarizeNotesRequest;
import dev.zisan.meditrack.ai.dto.TriageRequest;
import dev.zisan.meditrack.ai.dto.TriageResponse;
import dev.zisan.meditrack.ai.service.AiService;
import dev.zisan.meditrack.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ai")
@RequiredArgsConstructor
public class AiController {

	private final AiService aiService;

	@PostMapping("/triage")
	@PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR')")
	public ResponseEntity<ApiResponse<TriageResponse>> triage(@Valid @RequestBody TriageRequest request) {
		TriageResponse result = aiService.triage(request);

		ApiResponse<TriageResponse> response = ApiResponse.<TriageResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message(result.isFallback() ? "AI triage fallback returned." : "AI triage completed successfully.")
			.data(result)
			.build();

		return ResponseEntity.ok(response);
	}

	@PostMapping("/summarize-notes")
	@PreAuthorize("hasAuthority('DOCTOR')")
	public ResponseEntity<ApiResponse<NoteSummaryResponse>> summarizeNotes(
			@Valid @RequestBody SummarizeNotesRequest request) {
		NoteSummaryResponse result = aiService.summarizeNotes(request);

		ApiResponse<NoteSummaryResponse> response = ApiResponse.<NoteSummaryResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message(result.isFallback() ? "AI note summary fallback returned." : "AI note summary completed successfully.")
			.data(result)
			.build();

		return ResponseEntity.ok(response);
	}
}
