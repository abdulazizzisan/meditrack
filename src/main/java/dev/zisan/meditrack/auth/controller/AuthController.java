package dev.zisan.meditrack.auth.controller;

import dev.zisan.meditrack.auth.dto.AuthResponse;
import dev.zisan.meditrack.auth.dto.LoginRequest;
import dev.zisan.meditrack.auth.dto.RefreshTokenRequest;
import dev.zisan.meditrack.auth.dto.RegisterRequest;
import dev.zisan.meditrack.auth.service.AuthService;
import dev.zisan.meditrack.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
		AuthResponse authResponse = authService.register(request);

		ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
			.statusCode(HttpStatus.CREATED.value())
			.message("User registered successfully.")
			.data(authResponse)
			.build();

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
		AuthResponse authResponse = authService.login(request);

		ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Login successful.")
			.data(authResponse)
			.build();

		return ResponseEntity.ok(response);
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		AuthResponse authResponse = authService.refresh(request);

		ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
			.statusCode(HttpStatus.OK.value())
			.message("Token refreshed successfully.")
			.data(authResponse)
			.build();

		return ResponseEntity.ok(response);
	}
}
