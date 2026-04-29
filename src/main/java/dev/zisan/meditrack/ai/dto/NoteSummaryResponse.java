package dev.zisan.meditrack.ai.dto;

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
public class NoteSummaryResponse {
	private String chiefComplaint;
	private String assessment;
	private String plan;
	private boolean fallback;
	private String message;
}
