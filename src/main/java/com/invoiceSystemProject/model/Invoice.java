package com.invoiceSystemProject.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="Invoice")
@EntityListeners(AuditingEntityListener.class) // Enables automatic auditing
public class Invoice {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;
	
	
	 @Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal total;
	

	 @CreatedDate
	 @Column(name = "created_at", nullable = false, updatable = false)
	 private LocalDateTime createdAt;
	 
	 @Column(name = "deleted", nullable = false)
	 private boolean deleted = false;

	 
	public Invoice() {}
	 public Invoice(Long id, User user, BigDecimal total) {
		super();
		this.id = id;
		this.user = user;
		this.total = total;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}


		public User getUser() {
			return user;
		}


		public void setUser(User user) {
			this.user = user;
		}


		public BigDecimal getTotal() {
			return total;
		}


		public void setTotal(BigDecimal total) {
			this.total = total;
		}
		public boolean isDeleted() {
			return deleted;
		}
		public void setDeleted(boolean deleted) {
			this.deleted = deleted;
		}
}


