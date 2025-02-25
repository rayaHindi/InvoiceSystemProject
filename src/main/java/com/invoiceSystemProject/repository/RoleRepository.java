package com.invoiceSystemProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.invoiceSystemProject.model.Role;

public interface RoleRepository extends JpaRepository<Role,Long> {
	public Role findByRoleType(String role);
}
