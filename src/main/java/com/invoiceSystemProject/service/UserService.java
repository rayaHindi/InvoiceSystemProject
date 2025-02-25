package com.invoiceSystemProject.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoiceSystemProject.model.User;
import com.invoiceSystemProject.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepo;
	
	public User getUserByName(String name) {
		return userRepo.findByUsername(name);
	}

}
