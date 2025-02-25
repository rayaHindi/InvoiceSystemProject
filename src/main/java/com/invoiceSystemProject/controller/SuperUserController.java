package com.invoiceSystemProject.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.invoiceSystemProject.model.Invoice;
import com.invoiceSystemProject.service.InvoiceService;

@Controller
@RequestMapping("/superuser")
@PreAuthorize("hasRole('SUPERUSER')")
public class SuperUserController {
	 @GetMapping("/dashboard")
	    public String showAdminDashboard() {
	        return "superuser_dashboard"; // Loads admin_dashboard.html
	    }
}
