package dev.zisan.meditrack.search.dto;

import java.util.List;
import lombok.Builder;

@Builder
public record PatientSearchResponse(
	Long id,
	Long userId,
	String fullName,
	String gender,
	String bloodType,
	List<String> diagnoses,
	List<String> medications
) {
}
