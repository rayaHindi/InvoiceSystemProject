package com.invoiceSystemProject.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.invoiceSystemProject.model.Invoice;
import com.invoiceSystemProject.model.InvoiceHistory;
import com.invoiceSystemProject.model.InvoiceItem;
import com.invoiceSystemProject.model.Item;
import com.invoiceSystemProject.model.User;
import com.invoiceSystemProject.service.InvoiceHistoryService;
import com.invoiceSystemProject.service.InvoiceService;
import com.invoiceSystemProject.service.ItemService;
import com.invoiceSystemProject.service.UserService;
/*
 * 
 * GET	/invoices/user	Get all invoices for the logged-in user
GET	/invoices/all	Get all invoices (for SuperUser/Admin)
POST	/invoices/create	Create a new invoice
GET	/invoices/{id}	Get a specific invoice by ID
PUT	/invoices/{id}/edit	Edit/update an existing invoice
DELETE	/invoices/{id}/delete	Delete an invoice
 * */
@Controller
//@RequestMapping("/invoices")
public class InvoiceController {
	 @Autowired
	 	private InvoiceService invoiceService;
	
	 @Autowired
	    private ItemService itemService;
	 @Autowired
	    private UserService userService;
	 @Autowired
	 private InvoiceHistoryService invoiceHistoryService;
	 
	Logger logger= LoggerFactory.getLogger(InvoiceController.class);
	
		// user views their invoices//
	   @PreAuthorize("hasRole('USER')") 
	   @GetMapping("/user/invoices")
	    public String getUserInvoices(@RequestParam(defaultValue = "0") int page, Principal principal, Model model) {
		  logger.info("retrieving user's invoices");
	        Page<Invoice> invoices = invoiceService.getInvoicesByUser(principal.getName(), page);
	        model.addAttribute("invoices", invoices); // Pass to Thymeleaf
	        
	        HashMap  <Long,List<InvoiceItem>> map = new HashMap<Long, List<InvoiceItem>>();
	        for (Invoice invoice : invoices.getContent()) {
	        	List<InvoiceItem> items = invoiceService.getInvoiceItems(invoice.getId());
	        	map.put(invoice.getId(), items);
	        }
	        model.addAttribute("invoiceItemsMap", map); // Pass to Thymeleaf
	        return "user_invoices"; // Returns Thymeleaf template for user invoices
	    }
	  	
	   // user + superUser creating invoice //
	   @PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
	   @GetMapping("/invoices/create")
	    public String showCreateInvoiceForm(Model model) {
	        List<Item> items = itemService.getAllItems(); // Fetch items
	        model.addAttribute("items", items);
	        return "create_invoice"; // Render the form
	    }
		@PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
		@PostMapping("/invoices/create")
		public String  createInvoice(
					@RequestParam("itemIds") List<Long> itemIds,  // Multiple item IDs
			        @RequestParam("quantities") List<Integer> quantities, // Quantities matching item IDs
			        Principal principal
				) {
				logger.info("creating invoice for user {}",principal.getName());
				User user = userService.getUserByName(principal.getName());
				if (user == null) {
		            logger.error("User not found");
		        }
				invoiceService.createInvoice(user, itemIds, quantities);
		        return "redirect:/user/invoices";
		}
		

		// user + superUser editing an invoice //
		@PreAuthorize("hasRole('USER')")
		@GetMapping("/invoices/{id}/edit")
		public String showEditInvoiceForm(@PathVariable Long id, Model model) {
		    Invoice invoice = invoiceService.getInvoiceById(id);
		    if (invoice == null) {
		        throw new RuntimeException("Invoice not found!");
		    }
		    
		    List<Item> items = itemService.getAllItems(); // Get all available items
		    List<InvoiceItem> invoiceItems = invoiceService.getInvoiceItems(id); // Get existing items for this invoice
		    
		    model.addAttribute("invoice", invoice);
		    model.addAttribute("items", items);
		    model.addAttribute("invoiceItems", invoiceItems);
		    
		    return "edit_invoice"; // Returns the Thymeleaf template
		}
		@PreAuthorize("hasRole('USER')")
		@PostMapping("/invoices/{id}/edit")
		public String editInvoice(
		        @PathVariable Long id,
		        @RequestParam("itemIds") List<Long> itemIds,
		        @RequestParam("quantities") List<Integer> quantities,
		        @RequestParam("prices") List<BigDecimal> prices,
		        @RequestParam(value = "deletedItemIds", required = false) List<Long> deletedItemIds, // NEW PARAMETER

		        Principal principal) {

		    User user = userService.getUserByName(principal.getName());
		    if (user == null) {
		        throw new RuntimeException("User not found!");
		    }

		    invoiceService.editInvoice(id, itemIds, quantities, prices, deletedItemIds);
		    return "redirect:/user/invoices";
		}
		
		// user + superUser deleting an invoice //
		@PreAuthorize("hasAnyRole('USER,SUPERUSER')")
		@PostMapping("/invoices/{id}/delete")
		public String deleteInvoice(@PathVariable Long id) {
			invoiceService.softDelete(id);
			return "redirect:/user/invoices";
		}
		
		@PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
		@GetMapping("/invoices/{id}/track")
		public String trackInvoice(@PathVariable Long id, 
		                           @RequestParam(defaultValue = "0") int page, 
		                           Model model) {
		    Invoice invoice = invoiceService.getInvoiceById(id);
		    if (invoice == null) {
		        throw new RuntimeException("Invoice not found!");
		    }

		    // Fetch the invoice history with pagination
		    Page<InvoiceHistory> historyPage = invoiceHistoryService.getInvoiceHistory(id, page);

		    model.addAttribute("invoice", invoice);
		    model.addAttribute("historyPage", historyPage); // History list
		    model.addAttribute("currentPage", page);
		    
		    return "track_invoice"; // Thymeleaf template to display history
		}

	

}
