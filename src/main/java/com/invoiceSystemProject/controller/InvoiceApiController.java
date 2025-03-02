package com.invoiceSystemProject.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.invoiceSystemProject.model.Invoice;
import com.invoiceSystemProject.model.InvoiceHistory;
import com.invoiceSystemProject.model.InvoiceItem;
import com.invoiceSystemProject.model.Item;
import com.invoiceSystemProject.model.User;
import com.invoiceSystemProject.service.InvoiceHistoryService;
import com.invoiceSystemProject.service.InvoiceService;
import com.invoiceSystemProject.service.ItemService;
import com.invoiceSystemProject.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/invoices")
@Tag(name = "Invoice API", description = "Endpoints for managing invoices")
public class InvoiceApiController {

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private InvoiceHistoryService invoiceHistoryService;

    Logger logger = LoggerFactory.getLogger(InvoiceApiController.class);

    /* Retrieve invoices for the logged-in user or all invoices for Superuser/Auditor */
    @PreAuthorize("hasAnyRole('USER', 'SUPERUSER', 'AUDITOR')")
    @GetMapping
    public ResponseEntity<Page<Invoice>> getUserInvoices(
            @RequestParam(defaultValue = "0") int page,
            Principal principal) {

        logger.info("Retrieving user's invoices");
        User user = userService.getUserByName(principal.getName());
        String role = user.getRole().getRoleType();

        Page<Invoice> invoices = (role.equals("SUPERUSER") || role.equals("AUDITOR")) 
            ? invoiceService.getAllInvoices(page) 
            : invoiceService.getInvoicesByUser(principal.getName(), page);

        return ResponseEntity.ok(invoices);
    }

    /* Create an invoice */
    @PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
    @PostMapping("/create")
    public ResponseEntity<String> createInvoice(
            @RequestParam("itemIds") List<Long> itemIds,
            @RequestParam("quantities") List<Integer> quantities,
            Principal principal) {

        logger.info("Creating invoice for user {}", principal.getName());
        User user = userService.getUserByName(principal.getName());
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        invoiceService.createInvoice(user, itemIds, quantities);
        return ResponseEntity.ok("Invoice created successfully");
    }

    /* Edit an invoice */
    @PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
    @PutMapping("/{id}/edit")
    public ResponseEntity<String> editInvoice(
            @PathVariable Long id,
            @RequestParam("itemIds") List<Long> itemIds,
            @RequestParam("quantities") List<Integer> quantities,
            @RequestParam("prices") List<BigDecimal> prices,
            @RequestParam(value = "deletedItemIds", required = false) List<Long> deletedItemIds,
            Principal principal) {

        User user = userService.getUserByName(principal.getName());
        Invoice invoice = invoiceService.getInvoiceById(id);
        String role = user.getRole().getRoleType();

        if (user == null || invoice == null) {
            return ResponseEntity.badRequest().body("User or Invoice not found");
        }

        if (role.equals("USER") && !invoice.getUser().getUsername().equals(user.getUsername())) {
            return ResponseEntity.status(403).body("Unauthorized to edit this invoice");
        }

        invoiceService.editInvoice(id, itemIds, quantities, prices, deletedItemIds);
        return ResponseEntity.ok("Invoice updated successfully");
    }

    /* Soft delete an invoice */
    @PreAuthorize("hasAnyRole('USER', 'SUPERUSER')")
    @PutMapping("/{id}/delete")
    public ResponseEntity<String> deleteInvoice(@PathVariable Long id) {
        invoiceService.softDelete(id);
        return ResponseEntity.ok("Invoice deleted successfully");
    }

    /* Track an invoice */
    @PreAuthorize("hasAnyRole('USER', 'SUPERUSER','AUDITOR')")
    @GetMapping("/{id}/track")
    public ResponseEntity<Page<InvoiceHistory>> trackInvoice(
            @PathVariable Long id, 
            @RequestParam(defaultValue = "0") int page) {

        Invoice invoice = invoiceService.getInvoiceById(id);
        if (invoice == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Page<InvoiceHistory> historyPage = invoiceHistoryService.getInvoiceHistory(id, page);
        return ResponseEntity.ok(historyPage);
    }

    /* Search an invoice */
    @PreAuthorize("hasAnyRole('USER', 'SUPERUSER', 'AUDITOR')")
    @GetMapping("/search")
    public ResponseEntity<?> searchInvoice(
            @RequestParam(name = "invoiceId", required = false) Long invoiceId,
            Principal principal) {

        if (invoiceId == null) {
            return ResponseEntity.badRequest().body("Invoice ID is required");
        }

        User user = userService.getUserByName(principal.getName());
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        String role = user.getRole().getRoleType();
        Invoice invoice = invoiceService.getInvoiceById(invoiceId);

        if (invoice == null || invoice.isDeleted()) {
            return ResponseEntity.badRequest().body("Invoice not found");
        }

        if (role.equals("USER") && !invoice.getUser().getUsername().equals(user.getUsername())) {
            return ResponseEntity.status(403).body("Unauthorized to view this invoice");
        }

        List<InvoiceItem> invoiceItems = invoiceService.getInvoiceItems(invoiceId);
        HashMap<String, Object> response = new HashMap<>();
        response.put("invoice", invoice);
        response.put("invoiceItems", invoiceItems);
        response.put("role", role);

        return ResponseEntity.ok(response);
    }
}
