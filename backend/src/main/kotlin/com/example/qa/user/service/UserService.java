package com.example.qa.user.service;

import com.example.qa.user.model.AppUser;
import com.example.qa.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<AppUser> user = userRepository.findByUsernameAndEnable(username, true);

		if (user.isPresent())
			return user.get();
		else 
			throw new UsernameNotFoundException(String.format("Username[%s] not found", username));
	}
}
