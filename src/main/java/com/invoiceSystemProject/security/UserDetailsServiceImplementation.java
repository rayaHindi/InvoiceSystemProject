package com.invoiceSystemProject.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.invoiceSystemProject.model.User;
import com.invoiceSystemProject.repository.UserRepository;

import io.jsonwebtoken.lang.Collections;

@Service
public class UserDetailsServiceImplementation implements  UserDetailsService {
	@Autowired
	private UserRepository userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepo.findByUsername(username);
		if (user == null )new UsernameNotFoundException("User not found");
		return new org.springframework.security.core.userdetails.User(
	            user.getUsername(), 
	            user.getPassword(), 
	            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
	        );	
}

}
