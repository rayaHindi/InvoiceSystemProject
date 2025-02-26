package com.invoiceSystemProject.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="invoice_history")
@EntityListeners(AuditingEntityListener.class) // Enables automatic auditing
public class InvoiceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "invoice_item_id", nullable = true) //null when we first create an invoice
    private InvoiceItem invoiceItem;

    @Column(nullable = false)
    private String action;  // "Created" "Added", "Updated", "Deleted"

    @Column(name = "prev_price", precision = 10, scale = 2)
    private BigDecimal prevPrice;

    @Column(name = "prev_qty")
    private Integer prevQuantity;

    @Column(name = "curr_price", precision = 10, scale = 2)
    private BigDecimal currPrice;

    @Column(name = "curr_qty")
    private Integer currQuantity;

    @Column(nullable = false)
    private boolean status=true; // true = valid, false = previous record


    @CreatedDate
	 @Column(name = "created_at", nullable = false, updatable = false)
	 private LocalDateTime createdAt;
    
    public InvoiceHistory() {}
    
	public InvoiceHistory(Invoice invoice, InvoiceItem invoiceItem, String action, BigDecimal prevPrice,
			Integer prevQuantity, BigDecimal currPrice, Integer currQuantity, boolean status) {
		super();
		this.invoice = invoice;
		this.invoiceItem = invoiceItem;
		this.action = action;
		this.prevPrice = prevPrice;
		this.prevQuantity = prevQuantity;
		this.currPrice = currPrice;
		this.currQuantity = currQuantity;
		this.status = status;
	}

	public InvoiceHistory(Long id, Invoice invoice, InvoiceItem invoiceItem, String action, BigDecimal prevPrice,
			Integer prevQuantity, BigDecimal currPrice, Integer currQuantity, boolean status, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.invoice = invoice;
		this.invoiceItem = invoiceItem;
		this.action = action;
		this.prevPrice = prevPrice;
		this.prevQuantity = prevQuantity;
		this.currPrice = currPrice;
		this.currQuantity = currQuantity;
		this.status = status;
		this.createdAt = createdAt;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public InvoiceItem getInvoiceItem() {
		return invoiceItem;
	}

	public void setInvoiceItem(InvoiceItem invoiceItem) {
		this.invoiceItem = invoiceItem;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public BigDecimal getPrevPrice() {
		return prevPrice;
	}

	public void setPrevPrice(BigDecimal prevPrice) {
		this.prevPrice = prevPrice;
	}

	public Integer getPrevQuantity() {
		return prevQuantity;
	}

	public void setPrevQuantity(Integer prevQuantity) {
		this.prevQuantity = prevQuantity;
	}

	public BigDecimal getCurrPrice() {
		return currPrice;
	}

	public void setCurrPrice(BigDecimal currPrice) {
		this.currPrice = currPrice;
	}

	public Integer getCurrQuantity() {
		return currQuantity;
	}

	public void setCurrQuantity(Integer currQuantity) {
		this.currQuantity = currQuantity;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

}
