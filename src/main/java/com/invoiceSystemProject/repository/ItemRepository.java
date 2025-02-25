package com.invoiceSystemProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.invoiceSystemProject.model.Item;

public interface ItemRepository extends JpaRepository<Item,Long> {
	
}
