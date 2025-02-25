package com.invoiceSystemProject.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.invoiceSystemProject.model.Invoice;
import com.invoiceSystemProject.model.InvoiceItem;
import com.invoiceSystemProject.model.Item;
import com.invoiceSystemProject.model.User;
import com.invoiceSystemProject.repository.InvoiceItemRepository;
import com.invoiceSystemProject.repository.InvoiceRepository;
import com.invoiceSystemProject.repository.ItemRepository;

import org.springframework.data.domain.Pageable;

@Service
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepo;
	@Autowired
	private ItemRepository itemRepo;
	@Autowired
	private InvoiceItemRepository invoiceItemRepo;

	 public Page<Invoice> getInvoicesByUser(String username, int page) {
	        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
	        return invoiceRepo.findByUser_UsernameAndDeletedFalse(username, pageable);
	    }
	 // return items for a certain invoice
	 public List<InvoiceItem> getInvoiceItems(Long invoiceId){
		 return invoiceItemRepo.findByInvoiceId(invoiceId);
	 }
	 //for auditor
	 public Page<Invoice> getAllInvoices(int page) {
	        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
	        return invoiceRepo.findAll(pageable);
	    }
	
	public void createInvoice(Invoice invoice) {
	//	invoice.setUser(user);
		invoiceRepo.save(invoice);
		//return invoices;
	}


	public void createInvoice(User user, List<Long> itemIds, List<Integer> quantities) {
		Invoice invoice= new Invoice();
		BigDecimal total = BigDecimal.ZERO;	

		invoice.setUser(user);
		invoice.setTotal(total);// for now 0
		invoice= invoiceRepo.save(invoice);
		
		for (int i = 0; i < itemIds.size(); i++) {
            Item item = itemRepo.findById(itemIds.get(i)).orElse(null);
            if (item != null) {
                InvoiceItem invoiceItem = new InvoiceItem();
                
                invoiceItem.setInvoice(invoice);
                invoiceItem.setItem(item);
                invoiceItem.setQuantity(quantities.get(i));
                invoiceItem.setPrice(item.getPrice());

                invoiceItemRepo.save(invoiceItem);

                total = total.add(item.getPrice().multiply(BigDecimal.valueOf(quantities.get(i))));
            }
            invoice.setTotal(total);
            invoiceRepo.save(invoice);// with the total
        }

		
		
	}
	public void softDelete(Long invoiceID) {
		Invoice invoice = invoiceRepo.findById(invoiceID).orElse(null);
		if (invoice != null) { // Check if invoice exists
	        invoice.setDeleted(true);
	        invoiceRepo.save(invoice);
	    } else {
	        throw new RuntimeException("Invoice not found with ID: " + invoiceID);
	    }
	}
	public Invoice getInvoiceById(Long id) {
		return invoiceRepo.findById(id).orElse(null);
	}
}
