package dev.zisan.meditrack.security;

import static org.assertj.core.api.Assertions.assertThat;

import dev.zisan.meditrack.user.entity.Role;
import dev.zisan.meditrack.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

	private JwtService jwtService;

	@BeforeEach
	void setUp() {
		JwtProperties jwtProperties = new JwtProperties();
		jwtProperties.setSecret("VGhpc0lzQVNlY3VyZUJhc2U2NEVuY29kZWRLZXlGb3JNZWRpVHJhY2tKV1RUZXN0aW5nMTIzNDU2Nzg5MA==");
		jwtProperties.setAccessTokenExpirationMs(900_000L);
		jwtProperties.setRefreshTokenExpirationMs(604_800_000L);
		jwtService = new JwtService(jwtProperties);
	}

	@Test
	void shouldRejectAccessTokenForDisabledUser() {
		User user = User.builder()
			.id(1L)
			.email("doctor@example.com")
			.passwordHash("encoded-password")
			.fullName("Doctor Example")
			.role(Role.ROLE_DOCTOR)
			.enabled(true)
			.build();

		String token = jwtService.generateAccessToken(user);

		UserDetails disabledUser = org.springframework.security.core.userdetails.User.builder()
			.username(user.getEmail())
			.password(user.getPasswordHash())
			.authorities(user.getRole().name())
			.disabled(true)
			.build();

		assertThat(jwtService.isAccessTokenValid(token, disabledUser)).isFalse();
	}
}
