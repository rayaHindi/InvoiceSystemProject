package com.invoiceSystemProject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoiceSystemProject.model.User;


public interface UserRepository extends JpaRepository<User,Long> {
	 User findByUsername(String username);
}
