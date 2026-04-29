package dev.zisan.meditrack.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record SummarizeNotesRequest(
	@NotBlank(message = "Clinical notes are required")
	String notes
) {
}
