package com.invoiceSystemProject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
		http
		.csrf(csrf->csrf.disable())
		.authorizeHttpRequests(
				auth->auth
				.requestMatchers("/auth/*").permitAll() // ✅ Allow login & signup
	            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // ✅ Allow static files
				.anyRequest().authenticated()
				)
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // No sessions
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		
		
		return http.build();
	}
	
	//This tells Spring that we want to use BCrypt hashing.
	  @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder(); // Encrypt passwords
	    }

	   @Bean
	    public AuthenticationConfiguration authenticationConfiguration() {
	        return new AuthenticationConfiguration();
	    }
}
