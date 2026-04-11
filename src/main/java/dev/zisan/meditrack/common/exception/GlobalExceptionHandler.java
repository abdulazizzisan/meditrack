package dev.zisan.meditrack.common.exception;

import dev.zisan.meditrack.common.api.ApiResponse;
import dev.zisan.meditrack.common.api.ValidationError;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException exception) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException exception) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException exception) {
		return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
	public ResponseEntity<ApiResponse<Void>> handleForbidden(Exception exception) {
		return buildErrorResponse(HttpStatus.FORBIDDEN, exception.getMessage());
	}

	@ExceptionHandler({UnauthorizedException.class, AuthenticationException.class})
	public ResponseEntity<ApiResponse<Void>> handleUnauthorized(Exception exception) {
		return buildErrorResponse(HttpStatus.UNAUTHORIZED, exception.getMessage());
	}

	@ExceptionHandler(ExternalServiceException.class)
	public ResponseEntity<ApiResponse<Void>> handleExternalService(ExternalServiceException exception) {
		return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
		List<ValidationError> validationErrors = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(this::toValidationError)
			.toList();

		ApiResponse<Void> response = ApiResponse.<Void>builder()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.message("Validation failed")
			.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
			.validationErrors(validationErrors)
			.build();
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException exception) {
		List<ValidationError> validationErrors = exception.getConstraintViolations()
			.stream()
			.map(violation -> new ValidationError(violation.getPropertyPath().toString(), violation.getMessage()))
			.toList();

		ApiResponse<Void> response = ApiResponse.<Void>builder()
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.message("Validation failed")
			.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
			.validationErrors(validationErrors)
			.build();
		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
		return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
	}

	private ResponseEntity<ApiResponse<Void>> buildErrorResponse(HttpStatus status, String message) {
		ApiResponse<Void> response = ApiResponse.<Void>builder()
			.statusCode(status.value())
			.message(message)
			.error(status.getReasonPhrase())
			.build();
		return ResponseEntity.status(status).body(response);
	}

	private ValidationError toValidationError(FieldError fieldError) {
		return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
	}
}
