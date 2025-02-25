package com.invoiceSystemProject.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.invoiceSystemProject.model.Role;
import com.invoiceSystemProject.model.User;
import com.invoiceSystemProject.repository.RoleRepository;
import com.invoiceSystemProject.security.JwtUtil;
import com.invoiceSystemProject.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	@Autowired
	private RoleRepository roleRepo;
	@Autowired
	private JwtUtil jwtUtil;
	Logger logger= LoggerFactory.getLogger(AuthController.class);
	
	@GetMapping("/login")
	public String loginForm() {
	    return "login"; // Show login page
	}
	@PostMapping("/login")
	public String login(@RequestParam("username") String username,
	                    @RequestParam("password") String password,
	                    HttpServletResponse response,
	                    Model model) {
	    logger.info("Login attempt for user: {}", username);

	    try {
	        String token = authService.loginUser(username, password);

	        // Store JWT in HTTP-only cookie
	        Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
	        jwtCookie.setHttpOnly(true);
	        jwtCookie.setMaxAge(3600);
	        jwtCookie.setPath("/");
	        response.addCookie(jwtCookie);

	        String role = jwtUtil.extractRole(token);
	        logger.info("User role: {}", role);

	        // Redirect based on role
	        switch (role) {
	        case "USER": return "redirect:/user/dashboard";
	        case "SUPERUSER": return "redirect:/superuser/dashboard";
	        case "AUDITOR": return "redirect:/auditor/dashboard";
	        default: return "login";
	    }


	    } catch (UsernameNotFoundException | BadCredentialsException ex) {
	        model.addAttribute("error", ex.getMessage());
	        return "login"; 
	    }
	}


	
	 @GetMapping("/signup")
	    public String signupForm(Model model) {
	        model.addAttribute("user", new User()); // Bind empty user object to form
	        List<Role> roles = roleRepo.findAll(); // Fetch roles from db
	        model.addAttribute("roles", roles); 
	        return "signup";
	    }

	    @PostMapping("/signup")
	    public String signup(@ModelAttribute User user,@RequestParam("role") Long roleId, Model model) {
	    	logger.info("User signup attempt: {}", user.getUsername());
	    	
	        String response = authService.signUpUser(user,roleId);
	        
	        logger.info(" User registered successfully: {}", user.getUsername()); 
	        return "redirect:/auth/login"; // to login
	    }
	    
	    @PostMapping("/logout")
	    public String logout(HttpServletResponse response) {
	    	// remove token
	        Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
	        jwtCookie.setMaxAge(0); // expire immediately
	        jwtCookie.setPath("/");
	        response.addCookie(jwtCookie);

	        return "redirect:/auth/login"; // Redirect to login page
	    }

}
