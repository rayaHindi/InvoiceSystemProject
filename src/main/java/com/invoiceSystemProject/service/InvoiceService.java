package com.invoiceSystemProject.service;

import java.math.BigDecimal;
import java.util.HashMap;
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

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Pageable;

@Service
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepo;
	@Autowired
	private ItemRepository itemRepo;
	@Autowired
	private InvoiceItemRepository invoiceItemRepo;
	@Autowired
	private InvoiceHistoryService invoiceHistoryService;
	@Autowired
	private InvoiceItemService invoiceItemService;


	 public Page<Invoice> getInvoicesByUser(String username, int page) {
	        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
	        return invoiceRepo.findByUser_UsernameAndDeletedFalse(username, pageable);
	    }
	 // return items for a certain invoice
	 public List<InvoiceItem> getInvoiceItems(Long invoiceId){
		 return invoiceItemRepo.findByInvoiceIdAndDeletedFalse(invoiceId);
	 }
	 //for auditor
	 public Page<Invoice> getAllInvoices(int page) {
	        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
	        return invoiceRepo.findByDeletedFalse(pageable);
	    }
	 public Page<Invoice> getDeletedInvoices(int page) {
	        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
	        return invoiceRepo.findByDeletedTrue(pageable);
	    }


	public void createInvoice(User user, List<Long> itemIds, List<Integer> quantities) {
		 BigDecimal total = BigDecimal.ZERO;	

		    // calculating total
		    for (int i = 0; i < itemIds.size(); i++) {
		        Item item = itemRepo.findById(itemIds.get(i)).orElse(null);
		        if (item != null) {
		            total = total.add(item.getPrice().multiply(BigDecimal.valueOf(quantities.get(i))));
		        }
		    }
		Invoice invoice= new Invoice();
		invoice.setUser(user);
		invoice.setTotal(total);// for now 0
		invoice= invoiceRepo.save(invoice);
		//logging invoice creation
        invoiceHistoryService.logInvoiceCreation(invoice);

		
		for (int i = 0; i < itemIds.size(); i++) {
            Item item = itemRepo.findById(itemIds.get(i)).orElse(null);
            if (item != null) {
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoice(invoice);
                invoiceItem.setItem(item);
                invoiceItem.setQuantity(quantities.get(i));
                invoiceItem.setPrice(item.getPrice());

                invoiceItemRepo.save(invoiceItem);
        		//logging item addition
                invoiceHistoryService.logItemAdded(invoice, invoiceItem);

            }
        }
	}
	
	public void softDelete(Long invoiceID) {
	    Invoice invoice = invoiceRepo.findById(invoiceID).orElse(null);
	    
	    if (invoice == null) {
	        throw new RuntimeException("Invoice not found with ID: " + invoiceID);
	    }

	    // Mark invoice as deleted
	    invoice.setDeleted(true);
	   // invoice.setTotal(BigDecimal.ZERO); //  all items will be deleted
	    invoiceRepo.save(invoice);

	    // Fetch associated items
	    List<InvoiceItem> items = invoiceItemRepo.findByInvoiceIdAndDeletedFalse(invoiceID);
	    
	    for (InvoiceItem item : items) {
	        invoiceItemService.softDelete(item.getId()); // Soft delete each item
	        invoiceHistoryService.logItemRemoved(invoice, item); // Log deletion of item
	    }

	    // Log the invoice deletion event
	    invoiceHistoryService.logInvoiceDeletion(invoice);
	}

	public Invoice getInvoiceById(Long id) {
		return invoiceRepo.findById(id).orElse(null);
	}
	
	public void editInvoice(Long id, List<Long> itemIds, List<Integer> quantities, List<BigDecimal> prices, List<Long> deletedItemIds) {
		Invoice invoice = invoiceRepo.findById(id).orElseThrow(()->new EntityNotFoundException("Invoice not found"));
		List<InvoiceItem> invoiceExistingItems= invoiceItemRepo.findByInvoiceIdAndDeletedFalse(id);
		HashMap <Long, InvoiceItem>beforeEditingItemsMap= new HashMap <Long, InvoiceItem>();
		
		for(InvoiceItem invoiceItem : invoiceExistingItems ) {
			beforeEditingItemsMap.put(invoiceItem.getItem().getId(), invoiceItem); // now we have the items before editing in the map
			}
	    BigDecimal newTotal = BigDecimal.ZERO;

		for(int i=0; i < itemIds.size(); i++) {
			Long currentItemID= itemIds.get(i);
			int quantity = quantities.get(i);
	        BigDecimal price = prices.get(i);
	       
			
			//// item already exists in the invoice, -> UPDATE
			if(beforeEditingItemsMap.containsKey(currentItemID)) {
				InvoiceItem invoiceItem = beforeEditingItemsMap.get(currentItemID);
				
				 boolean priceChanged = price.compareTo(invoiceItem.getPrice()) != 0;
				  boolean quantityChanged = !(quantity==invoiceItem.getQuantity());
				
				if(priceChanged || quantityChanged) {
			          invoiceHistoryService.logItemUpdated(invoice, invoiceItem, price, quantity);// log the update

						invoiceItem.setPrice(price); // updated price
						invoiceItem.setQuantity(quantity); // updated price
						invoiceItemRepo.save(invoiceItem); //save it
				}
	  

			}
			//// item doesn't' exist in the invoice, -> ADD
			else {
				
				Item item = itemRepo.findById(currentItemID).orElse(null);
				InvoiceItem newInvoiceItem = new InvoiceItem( item, invoice, quantity, price);
                invoiceItemRepo.save(newInvoiceItem);
                
                invoiceHistoryService.logItemAdded(invoice, newInvoiceItem);

			}
			
	        newTotal = newTotal.add(price.multiply(BigDecimal.valueOf(quantity)));

		}
		//handling deleted item///
		if(!deletedItemIds.isEmpty()) {
			for (Long itemId : deletedItemIds) {
	            InvoiceItem itemToDelete = beforeEditingItemsMap.get(itemId);
	            if(itemToDelete !=null) {
					invoiceItemService.softDelete( itemToDelete.getId());
	                invoiceHistoryService.logItemRemoved(invoice, itemToDelete);

	            }
	       }
		}
		
		invoice.setTotal(newTotal);
	    invoiceRepo.save(invoice);
	}

}
