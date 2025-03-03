package com.invoiceSystemProject.controller;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.invoiceSystemProject.model.Invoice;
import com.invoiceSystemProject.model.InvoiceHistory;
import com.invoiceSystemProject.model.InvoiceItem;
import com.invoiceSystemProject.model.Item;
import com.invoiceSystemProject.model.Role;
import com.invoiceSystemProject.model.User;
import com.invoiceSystemProject.service.InvoiceHistoryService;
import com.invoiceSystemProject.service.InvoiceService;
import com.invoiceSystemProject.service.ItemService;
import com.invoiceSystemProject.service.UserService;

import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {
	 @Autowired
	 	private InvoiceService invoiceService;
	 @Autowired
	    private ItemService itemService;
	 @Autowired
	    private UserService userService;
	
	 
	Logger logger= LoggerFactory.getLogger(InvoiceController.class);
	
		// user views their invoices//
	   @PreAuthorize("hasAnyRole('USER', 'SUPERUSER', 'AUDITOR')") 
	   @GetMapping()
	    public String getUserInvoices(
	    		@RequestParam(defaultValue = "0") int page1,
	    		@RequestParam(defaultValue = "0") int page2,

	    		Principal principal, Model model) {
		   	logger.info("retrieving user's invoices");
		    User user = userService.getUserByName(principal.getName());
		    String role = user.getRole().getRoleType(); //  "USER", "SUPERUSER", "AUDITOR"
		    Page<Invoice> invoices;

		    if ("SUPERUSER".equals(role) || "AUDITOR".equals(role)) {
		        invoices = invoiceService.getAllInvoices(page1); // Superuser & Auditor see all invoices
		    } else {
		        invoices = invoiceService.getInvoicesByUser(principal.getName(), page1); //  only their invoices
		    }	   
		    if (role.equals("AUDITOR")) {
		        Page<Invoice> deletedInvoices = invoiceService.getDeletedInvoices(page2);
		        model.addAttribute("deletedInvoices", deletedInvoices);
		    }
		    
	        model.addAttribute("invoices", invoices); // passing to Thymeleaf
	        model.addAttribute("role", role); // passing role to Thymeleaf
	        
	        HashMap  <Long,List<InvoiceItem>> map = new HashMap<Long, List<InvoiceItem>>();
	        for (Invoice invoice : invoices.getContent()) {
	        	List<InvoiceItem> items = invoiceService.getInvoiceItems(invoice.getId());
	        	map.put(invoice.getId(), items);
	        }
	        model.addAttribute("invoiceItemsMap", map); // Pass to Thymeleaf
	        return "user_invoices"; // Thymeleaf template for user invoices
	    }
	  	
	   // user + superUser creating invoice //
	   @PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
	   @GetMapping("/create")
	    public String showCreateInvoiceForm(Model model) {
	        List<Item> items = itemService.getAllItems(); // Fetch items
	        model.addAttribute("items", items);
	        return "create_invoice"; // Render the form
	    }
		@PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
		@PostMapping("/create")
		public String  createInvoice(
					@RequestParam("itemIds") List<Long> itemIds,  // Multiple item IDs
			        @RequestParam("quantities") List<Integer> quantities, // Quantities matching item IDs
			        Principal principal
				) {
				logger.info("creating invoice for user {}",principal.getName());
				User user = userService.getUserByName(principal.getName());
				if (user == null) {
					throw new UsernameNotFoundException("User trying to create an invoice not found");
		        }
				invoiceService.createInvoice(user, itemIds, quantities);
				logger.info(" invoice created!");

		        return "redirect:/invoices";
		}
		

		// user + superUser editing an invoice //
		@PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
		@GetMapping("/{id}/edit")
		public String showEditInvoiceForm(@PathVariable Long id, Model model) {
		    Invoice invoice = invoiceService.getInvoiceById(id);
		    if (invoice == null) {
		        throw new EntityNotFoundException("Invoice not found!");
		    }
		    
		    List<Item> items = itemService.getAllItems(); // Get all available items
		    List<InvoiceItem> invoiceItems = invoiceService.getInvoiceItems(id); // Get existing items for this invoice
		    
		    model.addAttribute("invoice", invoice);
		    model.addAttribute("items", items);
		    model.addAttribute("invoiceItems", invoiceItems);
		    
		    return "edit_invoice"; // Returns the Thymeleaf template
		}
		
		@PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
		@PutMapping("/{id}/edit")
		public String editInvoice(
		        @PathVariable Long id,
		        @RequestParam("itemIds") List<Long> itemIds,
		        @RequestParam("quantities") List<Integer> quantities,
		        @RequestParam("prices") List<BigDecimal> prices,
		        @RequestParam(value = "deletedItemIds", required = false) List<Long> deletedItemIds, // NEW PARAMETER
		        Principal principal) throws AccessDeniedException {

		    User user = userService.getUserByName(principal.getName());
		    Invoice invoice = invoiceService.getInvoiceById(id);
		    String role =user.getRole().getRoleType();
		    
		    if (user == null) {
		        throw new UsernameNotFoundException("User requesting to edit invoice not found!");
		    }
		    if (invoice == null) {
		        throw new EntityNotFoundException("Invoice not found!");
		    }

		    // Authorization Check: If the user is a regular USER, they can only edit their own invoices
		    if (role.equals("USER") && !(invoice.getUser().getUsername().equals(user.getUsername()) ) ) {
		        throw new AccessDeniedException("You are not authorized to edit this invoice.");
		    }

			logger.info("User editing invoice {}",invoice.getId());

		    invoiceService.editInvoice(id, itemIds, quantities, prices, deletedItemIds);
		    return "redirect:/invoices";
		}
		
		
		// user + superUser deleting an invoice //
		@PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
		@PutMapping("/{id}/delete")
		public String deleteInvoice(@PathVariable Long id) {
			invoiceService.softDelete(id);
			return "redirect:/invoices";
		}
		
		
	
		//search //
		@PreAuthorize("hasAnyRole('USER', 'SUPERUSER', 'AUDITOR')")
		@GetMapping("/search")
		public String searchInvoice(@RequestParam(name = "invoiceId", required = false) Long invoiceId,
		                            Principal principal,
		                            Model model) {
		    if (invoiceId == null) {
		        return "search_invoice"; // Return the search page if no query is provided
		    }

		    User user = userService.getUserByName(principal.getName());
		    if (user == null) {
		        throw new UsernameNotFoundException("User not found!");
		    }
		    String role= user.getRole().getRoleType();

		    Invoice invoice = invoiceService.getInvoiceById(invoiceId);
		    if (invoice == null) {
		        model.addAttribute("error", "Invoice not found!");
				logger.error("Searched invoice not found!");

		        return "search_invoice";
		    }
		    if ( invoice.isDeleted()) {
		        model.addAttribute("error", "Invoice was deleted!");
				//logger.error("Searched invoice not found!");

		      //  return "search_invoice";
		    }

		    // Authorization Check
		    if (role.equals("USER") && !invoice.getUser().getUsername().equals(user.getUsername())) {
		        model.addAttribute("error", "You are not authorized to view this invoice.");
				logger.error("You are not authorized to view this invoice");

		        return "search_invoice";
		    }

		    List<InvoiceItem> invoiceItems = invoiceService.getInvoiceItems(invoiceId);
		    model.addAttribute("invoice", invoice);
		    model.addAttribute("invoiceItems", invoiceItems);
		    model.addAttribute("role", role);

		    return "search_invoice";
		}


	

}
