package dev.zisan.meditrack.auth.service;

import dev.zisan.meditrack.auth.dto.AuthResponse;
import dev.zisan.meditrack.auth.dto.LoginRequest;
import dev.zisan.meditrack.auth.dto.RefreshTokenRequest;
import dev.zisan.meditrack.auth.dto.RegisterRequest;
import dev.zisan.meditrack.common.exception.BadRequestException;
import dev.zisan.meditrack.common.exception.ConflictException;
import dev.zisan.meditrack.doctor.entity.Doctor;
import dev.zisan.meditrack.doctor.repository.DoctorRepository;
import dev.zisan.meditrack.patient.entity.Patient;
import dev.zisan.meditrack.patient.repository.PatientRepository;
import dev.zisan.meditrack.security.JwtService;
import dev.zisan.meditrack.user.entity.Role;
import dev.zisan.meditrack.user.entity.User;
import dev.zisan.meditrack.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PatientRepository patientRepository,
			DoctorRepository doctorRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtService jwtService) {
		this.userRepository = userRepository;
		this.patientRepository = patientRepository;
		this.doctorRepository = doctorRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthResponse register(RegisterRequest request) {
		validateRegistrationRequest(request);

		if (userRepository.existsByEmail(request.email())) {
			throw new ConflictException("Email is already in use.");
		}

		if (request.role() == Role.DOCTOR
				&& doctorRepository.findByLicenseNumber(request.licenseNumber()).isPresent()) {
			throw new ConflictException("License number is already in use.");
		}

		User user = User.builder()
			.fullName(request.fullName())
			.email(request.email())
			.passwordHash(passwordEncoder.encode(request.password()))
			.role(request.role())
			.enabled(true)
			.build();

		User savedUser = userRepository.save(user);

		if (savedUser.getRole() == Role.PATIENT) {
			patientRepository.save(Patient.builder()
				.user(savedUser)
				.dateOfBirth(request.dateOfBirth())
				.gender(request.gender())
				.bloodType(request.bloodType())
				.phone(request.phone())
				.address(request.address())
				.build());
		}

		if (savedUser.getRole() == Role.DOCTOR) {
			doctorRepository.save(Doctor.builder()
				.user(savedUser)
				.specialization(request.specialization())
				.licenseNumber(request.licenseNumber())
				.hospitalAffiliation(request.hospitalAffiliation())
				.build());
		}

		return buildAuthResponse(savedUser);
	}

	public AuthResponse login(LoginRequest request) {
		try {
			authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password())
			);
		} catch (DisabledException exception) {
			throw new BadRequestException("User account is disabled.");
		} catch (BadCredentialsException exception) {
			throw new BadRequestException("Invalid email or password.");
		}

		User user = userRepository.findByEmail(request.email())
			.orElseThrow(() -> new BadRequestException("Invalid email or password."));

		return buildAuthResponse(user);
	}

	public AuthResponse refresh(RefreshTokenRequest request) {
		String username;

		try {
			username = jwtService.extractUsername(request.refreshToken());
		} catch (Exception exception) {
			throw new BadRequestException("Invalid refresh token.");
		}

		User user = userRepository.findByEmail(username)
			.orElseThrow(() -> new BadRequestException("Invalid refresh token."));

		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
			.username(user.getEmail())
			.password(user.getPasswordHash())
			.authorities(user.getRole().name())
			.disabled(!user.isEnabled())
			.build();

		if (!jwtService.isRefreshTokenValid(request.refreshToken(), userDetails)) {
			throw new BadRequestException("Invalid refresh token.");
		}

		return buildAuthResponse(user, request.refreshToken());
	}

	private AuthResponse buildAuthResponse(User user) {
		String refreshToken = jwtService.generateRefreshToken(user);
		return buildAuthResponse(user, refreshToken);
	}

	private AuthResponse buildAuthResponse(User user, String refreshToken) {
		return AuthResponse.builder()
			.userId(user.getId())
			.fullName(user.getFullName())
			.email(user.getEmail())
			.role(user.getRole())
			.accessToken(jwtService.generateAccessToken(user))
			.refreshToken(refreshToken)
			.tokenType("Bearer")
			.expiresIn(jwtService.getAccessTokenExpirationMs())
			.build();
	}

	private void validateRegistrationRequest(RegisterRequest request) {
		if (request.role() == Role.ADMIN) {
			throw new BadRequestException("Admin registration is not allowed.");
		}

		if (request.role() == Role.DOCTOR) {
			if (isBlank(request.specialization()) || isBlank(request.licenseNumber())) {
				throw new BadRequestException("Doctor registration requires specialization and license number.");
			}
		}

		if (request.role() == Role.PATIENT && isBlank(request.phone())) {
			throw new BadRequestException("Patient registration requires a phone number.");
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.isBlank();
	}
}
