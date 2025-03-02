package com.invoiceSystemProject.service;

import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.invoiceSystemProject.model.User;
import com.invoiceSystemProject.repository.RoleRepository;
import com.invoiceSystemProject.repository.UserRepository;
import com.invoiceSystemProject.security.JwtUtil;

import jakarta.persistence.EntityNotFoundException;

import com.invoiceSystemProject.model.Role;

@Service
public class AuthService {
	@Autowired
	private UserRepository userRepo;
	@Autowired
    private PasswordEncoder passwordEncoder; // Inject PasswordEncoder
    @Autowired
    private RoleRepository roleRepo; // Fetch role from DB
    
	 @Autowired
	    private JwtUtil jwt;

	public String loginUser(String username, String password) {
	    User optionalUser = userRepo.findByUsername(username);
	    if (optionalUser==null) {
	        throw new UsernameNotFoundException("User with username '" + username + "' not found.");
	    }
	    User user = optionalUser;

		if (!passwordEncoder.matches( password, user.getPassword())) {
	        throw new BadCredentialsException("Invalid username or password.");

		}
        return jwt.generateToken(user.getUsername(), user.getRole().getRoleType()); 
	}
	
	public String signUpUser(User user, Long roleType) {
		
		 Role role = roleRepo.findById(roleType).orElseThrow(() -> new EntityNotFoundException("Role not found"));
		 user.setRole(role);
		 user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
		 userRepo.save(user);   
		 return "User registered successfully!";

	}

}
