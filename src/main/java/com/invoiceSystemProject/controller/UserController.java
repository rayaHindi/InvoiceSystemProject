package com.invoiceSystemProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.invoiceSystemProject.model.Invoice;
import com.invoiceSystemProject.service.InvoiceService;

@Controller
@RequestMapping("/user")
public class UserController {
    @GetMapping("/dashboard")
    public String showUserDashboard() {
        return "user_dashboard"; // Loads user_dashboard.html
    }
}
