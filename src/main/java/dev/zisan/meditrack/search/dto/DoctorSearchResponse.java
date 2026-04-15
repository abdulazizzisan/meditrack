package dev.zisan.meditrack.search.dto;

import lombok.Builder;

@Builder
public record DoctorSearchResponse(
	Long id,
	Long userId,
	String fullName,
	String specialization,
	String hospitalAffiliation,
	String licenseNumber
) {
}
