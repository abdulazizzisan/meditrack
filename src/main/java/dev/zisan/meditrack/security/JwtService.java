package dev.zisan.meditrack.security;

import dev.zisan.meditrack.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private static final String ROLE_CLAIM = "role";
	private static final String TOKEN_TYPE_CLAIM = "token_type";
	private static final String ACCESS_TOKEN_TYPE = "access";
	private static final String REFRESH_TOKEN_TYPE = "refresh";

	private final JwtProperties jwtProperties;

	public JwtService(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	public String generateAccessToken(User user) {
		return generateToken(user, jwtProperties.getAccessTokenExpirationMs(), ACCESS_TOKEN_TYPE);
	}

	public String generateRefreshToken(User user) {
		return generateToken(user, jwtProperties.getRefreshTokenExpirationMs(), REFRESH_TOKEN_TYPE);
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public boolean isAccessTokenValid(String token, UserDetails userDetails) {
		return isTokenValid(token, userDetails, ACCESS_TOKEN_TYPE);
	}

	public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
		return isTokenValid(token, userDetails, REFRESH_TOKEN_TYPE);
	}

	public long getAccessTokenExpirationMs() {
		return jwtProperties.getAccessTokenExpirationMs();
	}

	private boolean isTokenValid(String token, UserDetails userDetails, String expectedType) {
		Claims claims = extractAllClaims(token);
		String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
		return claims.getSubject().equals(userDetails.getUsername())
			&& expectedType.equals(tokenType)
			&& claims.getExpiration().after(new Date());
	}

	private String generateToken(User user, long expirationMs, String tokenType) {
		Instant now = Instant.now();
		Map<String, Object> claims = new HashMap<>();
		claims.put(ROLE_CLAIM, user.getRole().name());
		claims.put(TOKEN_TYPE_CLAIM, tokenType);

		return Jwts.builder()
			.claims(claims)
			.subject(user.getEmail())
			.issuedAt(Date.from(now))
			.expiration(Date.from(now.plusMillis(expirationMs)))
			.signWith(getSigningKey())
			.compact();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
			.verifyWith((javax.crypto.SecretKey) getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private Key getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
