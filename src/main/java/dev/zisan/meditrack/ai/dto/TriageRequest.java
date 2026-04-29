package dev.zisan.meditrack.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record TriageRequest(
	@NotEmpty(message = "At least one symptom is required")
	List<@NotBlank(message = "Symptoms must not be blank") String> symptoms
) {
}
