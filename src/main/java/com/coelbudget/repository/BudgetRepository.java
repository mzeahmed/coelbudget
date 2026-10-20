package com.coelbudget.repository;

import com.coelbudget.Transaction;

import java.util.List;

/**
 * Defines the contract for storing and retrieving budget transactions.
 *
 * <p>Implementations may use files, databases or other storage systems.</p>
 */
public interface BudgetRepository {

    /**
     * Retrieves all persisted transactions.
     *
     * @return the stored transactions
     */
    List<Transaction> findAll();

    /**
     * Persists the supplied transactions.
     *
     * @param transactions the transactions to store
     */
    void saveAll(List<Transaction> transactions);
}
