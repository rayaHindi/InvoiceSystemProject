package com.invoiceSystemProject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoiceSystemProject.model.InvoiceItem;
import com.invoiceSystemProject.repository.InvoiceItemRepository;

@Service
public class InvoiceItemService {
	@Autowired
	private InvoiceItemRepository invoiceItemRepo;
	
	public void softDelete(Long id) {
		InvoiceItem item = invoiceItemRepo.findById(id).orElse(null);
		item.setDeleted(true);
		invoiceItemRepo.save(item);
	}
	
}
