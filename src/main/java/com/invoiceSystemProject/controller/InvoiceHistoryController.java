package com.invoiceSystemProject.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.invoiceSystemProject.model.Invoice;
import com.invoiceSystemProject.model.InvoiceHistory;
import com.invoiceSystemProject.service.InvoiceHistoryService;
import com.invoiceSystemProject.service.InvoiceService;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/invoices")
public class InvoiceHistoryController {
	 @Autowired
	 	private InvoiceService invoiceService;
	 @Autowired
	 private InvoiceHistoryService invoiceHistoryService;
	 
		Logger logger= LoggerFactory.getLogger(InvoiceController.class);

	 
		@PreAuthorize("hasAnyRole('USER', 'SUPERUSER','AUDITOR')")
		@GetMapping("/{id}/track")
		public String trackInvoice(@PathVariable Long id, 
		                           @RequestParam(defaultValue = "0") int page, 
		                           Model model) {
		    Invoice invoice = invoiceService.getInvoiceById(id);
		    if (invoice == null) {
		        throw new EntityNotFoundException("Invoice not found!");
		    }
		    

		    // Fetch the invoice history with pagination
		    Page<InvoiceHistory> historyPage = invoiceHistoryService.getInvoiceHistory(id, page);
			logger.info("User tracking invoice {}",invoice.getId());

		    model.addAttribute("invoice", invoice);
		    model.addAttribute("historyPage", historyPage); // History list
		    model.addAttribute("currentPage", page);
		    
		    return "track_invoice"; // Thymeleaf template to display history
		}
		

}
