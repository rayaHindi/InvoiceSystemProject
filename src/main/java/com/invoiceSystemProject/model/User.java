package com.invoiceSystemProject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="User")
public class User {
		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(unique = true, nullable = false)
	    private String username;
	  
		@Column(nullable = false)
	    private String password;

	    @ManyToOne
	    @JoinColumn(name = "role_id") // FK
	    private Role role;
	    
	    @Column(name = "deleted", nullable = false)
	    private boolean deleted = false;

	    
	    
	    public User() {}
	    
	    public User(Long id, String username, String password, Role role) {
			super();
			this.id = id;
			this.username = username;
			this.password = password;
			this.role = role;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Role getRole() {
			return role;
		}

		public void setRole(Role role) {
			this.role = role;
		}

		public boolean isDeleted() {
			return deleted;
		}

		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}

}

