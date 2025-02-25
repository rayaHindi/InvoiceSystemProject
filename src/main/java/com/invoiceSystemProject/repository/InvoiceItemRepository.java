package com.invoiceSystemProject.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.invoiceSystemProject.model.InvoiceItem;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem , Long>{
	public List<InvoiceItem> findByInvoiceId(Long invoiceID);
}
