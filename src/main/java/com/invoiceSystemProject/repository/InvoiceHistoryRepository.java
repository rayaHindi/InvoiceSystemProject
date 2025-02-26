package com.invoiceSystemProject.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.invoiceSystemProject.model.InvoiceHistory;

import jakarta.transaction.Transactional;

public interface InvoiceHistoryRepository extends JpaRepository<InvoiceHistory,Long> {
	public Page<InvoiceHistory> findByInvoiceId(Long invoiceId, Pageable pageable);
	public List<InvoiceHistory> findByInvoiceIdAndInvoiceItemId(Long invoiceId, Long invoiceItemId);
	 @Modifying
	    @Transactional
	    @Query("UPDATE InvoiceHistory h SET h.status = false WHERE h.invoice.id = :invoiceId AND h.invoiceItem.id = :invoiceItemId")
	    void invalidatePreviousEntry(@Param("invoiceId") Long invoiceId, @Param("invoiceItemId") Long invoiceItemId);

	
}
