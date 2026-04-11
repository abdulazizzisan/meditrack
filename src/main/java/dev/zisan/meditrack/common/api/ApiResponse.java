package dev.zisan.meditrack.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

	private Integer statusCode;
	private String message;
	private T data;
	private String error;
	private Instant timestamp = Instant.now();
	private List<ValidationError> validationErrors;

	public ApiResponse(Integer statusCode, String message, T data, String error,
			List<ValidationError> validationErrors) {
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
		this.error = error;
		this.validationErrors = validationErrors;
		this.timestamp = Instant.now();
	}
}
