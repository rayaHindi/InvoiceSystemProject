package com.invoiceSystemProject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="Role")
public class Role {
	
		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String roleType; //  "USER", "SUPERUSER", "AUDITOR"
	    @Column(name = "deleted", nullable = false)
	    private boolean deleted = false;

	    public Role() {}

	    public Role(String roleType) {
	        this.roleType = roleType;
	    }

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getRoleType() {
	        return roleType;
	    }

	    public void setRoleType(String roleType) {
	        this.roleType = roleType;
	    }

		public boolean isDeleted() {
			return deleted;
		}

		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}
	    
}
