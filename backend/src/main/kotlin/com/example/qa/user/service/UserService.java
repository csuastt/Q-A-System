package com.example.qa.user.service;

import com.example.qa.user.model.AppUser;
import com.example.qa.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
	@Autowired 
	private UserRepository userRepository;

	@Override 
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<AppUser> user = userRepository.findByUsername(username);

		if (user.isPresent())
			return user.get();
		else 
			throw new UsernameNotFoundException(String.format("Username[%s] not found", username));
	}
}
