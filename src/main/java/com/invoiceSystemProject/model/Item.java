package com.invoiceSystemProject.model;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="Item")
public class Item {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	 @Column(nullable = false)
	private String name;
	 @Column(nullable = false, precision = 10, scale = 2) // 10 total /2 decimal 
	private BigDecimal price;
	 
	public Item() {}
	 
	public Item(Long id, String name, BigDecimal price) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}	
	 
	 
}
