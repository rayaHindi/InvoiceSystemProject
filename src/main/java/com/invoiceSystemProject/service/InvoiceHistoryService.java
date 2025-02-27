package com.invoiceSystemProject.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.invoiceSystemProject.model.Invoice;
import com.invoiceSystemProject.model.InvoiceHistory;
import com.invoiceSystemProject.model.InvoiceItem;
import com.invoiceSystemProject.repository.InvoiceHistoryRepository;
import org.springframework.data.domain.Pageable;

@Service
public class InvoiceHistoryService {
	@Autowired
	private InvoiceHistoryRepository historyRepo;
	
	/*public void invalidatePreviousEntry(Long invoiceId, Long invoiceItemId) {
	    List<InvoiceHistory> prevRecords = historyRepo.findByInvoiceIdAndInvoiceItemId(invoiceId, invoiceItemId);
	    if (!prevRecords.isEmpty()) {
	        for (InvoiceHistory history : prevRecords) {
	            history.setStatus(false);
	        }
	        historyRepo.saveAll(prevRecords); 
	    }
	}
*/
	
	public Page<InvoiceHistory> getInvoiceHistory(Long id, int page){
		Pageable pageable = PageRequest.of(page, 10,Sort.by("createdAt"));
		return historyRepo.findByInvoiceId(id, pageable);
	}
	
	public void logInvoiceCreation(Invoice invoice) {
		
		 InvoiceHistory history = 
				 new InvoiceHistory(invoice, null, "Created",
				 					null, null, invoice.getTotal(), null, 
				 						true);
		 historyRepo.save(history);
		
		
	}
	public void logInvoiceDeletion(Invoice invoice) {
		
		 InvoiceHistory history = 
				 new InvoiceHistory(invoice, null, "Deleted",
				 					null, null, null, null, 
				 						true);
		 historyRepo.save(history);
		
		
	}

	public void logItemAdded(Invoice invoice, InvoiceItem invoiceItem) {
		 InvoiceHistory history = 
				 new InvoiceHistory(invoice, invoiceItem, "Added",
				 					null, null, invoiceItem.getPrice(), invoiceItem.getQuantity(), 
				 						true);
		 historyRepo.save(history);
		
	}


	public void logItemUpdated(Invoice invoice, InvoiceItem invoiceItem, BigDecimal price, int quantity) {
		historyRepo.invalidatePreviousEntry(invoice.getId(),invoiceItem.getId() );
	
		 InvoiceHistory history = 
				 new InvoiceHistory(invoice, invoiceItem, "Updated",
						 invoiceItem.getPrice(), invoiceItem.getQuantity(), price, quantity, 
				 						true);
		 historyRepo.save(history);
		
	}

	public void logItemRemoved(Invoice invoice, InvoiceItem itemToDelete) {
		historyRepo.invalidatePreviousEntry(invoice.getId(),itemToDelete.getId() );

		 InvoiceHistory history = 
				 new InvoiceHistory(invoice, itemToDelete, "Item Deleted",
						 itemToDelete.getPrice(), itemToDelete.getQuantity(), null, null,
				 						true);
		 historyRepo.save(history);
		
	}
	
	
	
}
