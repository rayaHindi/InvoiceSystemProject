package com.invoiceSystemProject.model;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="invoice_item")

public class InvoiceItem {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;
	
	@ManyToOne
	@JoinColumn(name = "invoice_id", nullable = false)
	private Invoice invoice;
	
	@Column(nullable = false)
	private int quantity;
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price;  

	
	public InvoiceItem() {}
	public InvoiceItem(Long id, Item item, Invoice invoice, int quantity,BigDecimal price) {
		super();
		this.id = id;
		this.item = item;
		this.invoice = invoice;
		this.quantity = quantity;
		this.price = price;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Invoice item ( name: "+ this.getItem().getName() + " , quantity: "+ this.getQuantity() + " )";
	}
	/*public BigDecimal getTotalPrice() {
        return price.multiply(BigDecimal.valueOf(quantity));  
    }*/
	
	
}
