package com.invoiceSystemProject.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoiceSystemProject.service.GeminiService;
import com.invoiceSystemProject.service.QueryService;
import com.invoiceSystemProject.service.UserService;

import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/invoices/gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private QueryService queryService;
    
    @Autowired
    private UserService userService;
    
	Logger logger= LoggerFactory.getLogger(InvoiceController.class);

    //  show the Gemini Page
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPERUSER', 'AUDITOR')")
    public String showGeminiPage(Model model, Principal principal) {
    	 String role = userService.getUserByName(principal.getName()).getRole().getRoleType();
         model.addAttribute("role", role);  //  Ensure role is passed to the template [for  return to dash-board]
        return "gemini_page";  // Renders gemini_page.html
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERUSER', 'AUDITOR')")
    public String sentToGemini(@RequestParam("userPrompt") String userPrompt, Model model,  Principal principal) {
        String fixedPrompt = "I  have an invoice table named \"invoice\" [id, created_at, total ,user_id, deleted] i want u to return the select statement that do the following (only for deleted=false invoices) with the knowledge that my db is mysql so only use functions that can be applied on it:";
        		String prompt = fixedPrompt + userPrompt;

        logger.info("Sending the prompt to gemini: "+ prompt);
        String sql = geminiService.generateContent(prompt).block();

        if (sql == null || sql.isBlank()) {
            model.addAttribute("errorMessage", "Gemini AI did not return a valid response.");
            logger.error("Gemini AI did not return a valid response. ");
            return "gemini_page";
        }
        
        // from the start and end [from Gemini it comes ``` sql select...```]
        // 1-Removing (```) 
        sql = sql.replaceAll("```", "").trim();

        // 2- "SQL " (case-insensitive) is removed properly
        sql = sql.replaceAll("(?i)^SQL\\s+", "").trim();

        // 3- check that it's select statement
        if (!sql.toUpperCase().startsWith("SELECT")) {
            model.addAttribute("errorMessage", " Only SELECT queries are allowed. " + sql);
            logger.error("Only SELECT queries are allowed.");

            return "gemini_page";
        }

        logger.info("Cleaned SQL: " + sql);

        List<Object[]> result = queryService.executeQuery(sql);

        model.addAttribute("queryResult", result);
        model.addAttribute("generatedSql", sql);
        model.addAttribute("userPrompt", userPrompt);
        String role = userService.getUserByName(principal.getName()).getRole().getRoleType();
        model.addAttribute("role", role);  //  Ensure role is passed to the template [for  return to dash-board]
        return "gemini_page";


    }
}
