package com.invoiceSystemProject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.invoiceSystemProject.model.Item;
import com.invoiceSystemProject.repository.ItemRepository;

@Service
public class ItemService {
	@Autowired
	private ItemRepository itemRepo;
	
	public List<Item> getAllItems() {
		return itemRepo.findAll();
	}

}
