package com.invoiceSystemProject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.invoiceSystemProject.model.Invoice;
import com.invoiceSystemProject.model.User;

public interface InvoiceRepository extends JpaRepository<Invoice , Long>{
	public Page<Invoice>findByUser_UsernameAndDeletedFalse(String username, Pageable pageable);
	public Page<Invoice>findByDeletedFalse(Pageable pageable);
	public Page<Invoice>findByDeletedTrue(Pageable pageable);

	//public Page<Invoice>findAllDeletedFalse(Pageable pageable);

}
