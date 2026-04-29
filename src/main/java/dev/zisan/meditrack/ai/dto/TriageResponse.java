package dev.zisan.meditrack.ai.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriageResponse {

	private String urgencyLevel;
	private List<String> possibleConditions;
	private String suggestedSpecialization;
	private boolean fallback;
	private String message;
}
