package dev.zisan.meditrack.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private Integer statusCode;
	private String message;
	private T data;
	private String error;
	@Builder.Default
	private Instant timestamp = Instant.now();
	private List<ValidationError> validationErrors;
}
