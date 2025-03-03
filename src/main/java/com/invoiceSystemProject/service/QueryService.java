package com.invoiceSystemProject.service;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

@Service
public class QueryService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> executeQuery(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }
}
