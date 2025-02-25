package com.invoiceSystemProject.security;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

@Component
public class JwtAuthFilter extends OncePerRequestFilter{
	
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
    private UserDetailsService userDetailsService;

	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	        throws ServletException, IOException {

	    String token = extractJwtFromCookie(request);

	    if (token != null) {
	    	 String username = jwtUtil.extractUsername(token);
	         String role = jwtUtil.extractRole(token);
	         System.out.println("in JwtAuthFilter extraxted role from token  "+ role);

	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

	            if (jwtUtil.validateToken(token, userDetails.getUsername())) {
	            	List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
	            	System.out.println("in JwtAuthFilter authorities "+authorities);
	                SecurityContextHolder.getContext().setAuthentication(
	                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
	                        userDetails, null, authorities
	                    )
	                );
	            }
	        }
	    }
	    chain.doFilter(request, response);
	}
	
	private String extractJwtFromCookie(HttpServletRequest request) {
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie cookie : cookies) {
	            if ("JWT_TOKEN".equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	    }
	    return null;
	}

	}
