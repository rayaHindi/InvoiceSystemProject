package com.invoiceSystemProject.controller;

import com.invoiceSystemProject.service.QueryService;
import com.invoiceSystemProject.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.security.Principal;

@Controller
@RequestMapping("/invoices")
public class QueryController {

    @Autowired
    private QueryService queryService;
    @Autowired
    private UserService userService;
    
    @PreAuthorize("hasAnyRole('SUPERUSER', 'AUDITOR')")
    @GetMapping("/query")
    public String showQueryPage(Principal principal, Model model) {
        String role = userService.getUserByName(principal.getName()).getRole().getRoleType();
        model.addAttribute("role", role);  // ✅ Ensure role is passed to the template
        return "query_page"; 
    }
	
	@PreAuthorize("hasAnyRole('SUPERUSER', 'AUDITOR')")
    @GetMapping("/executeQuery")
    public String executeQuery(@RequestParam String sql, Model model,Principal principal) {
	
		String sql1=sql.trim().toUpperCase();
		if(!sql1.startsWith("SELECT")) {
		    throw new IllegalArgumentException("Only SELECT queries are allowed.");
		
		}
		String role = userService.getUserByName(principal.getName()).getRole().getRoleType();
    	model.addAttribute("role", role); 
		List<Object[]> result = queryService.executeQuery(sql);
			
        model.addAttribute("queryResult", result);
        model.addAttribute("sql", sql);  // Pass the entered SQL query back to the view
        return "query_page"; // reloads the same page with results
    }
}
