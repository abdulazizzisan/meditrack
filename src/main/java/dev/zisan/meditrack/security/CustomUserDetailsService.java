package dev.zisan.meditrack.security;

import dev.zisan.meditrack.common.exception.ResourceNotFoundException;
import dev.zisan.meditrack.user.entity.User;
import dev.zisan.meditrack.user.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username)
			.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + username));

		return org.springframework.security.core.userdetails.User.builder()
			.username(user.getEmail())
			.password(user.getPasswordHash())
			.disabled(!user.isEnabled())
			.authorities(new SimpleGrantedAuthority(user.getRole().name()))
			.build();
	}
}
