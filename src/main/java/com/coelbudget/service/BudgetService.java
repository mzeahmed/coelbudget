package com.coelbudget.service;

import com.coelbudget.Budget;
import com.coelbudget.Transaction;
import com.coelbudget.repository.BudgetRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Coordinates budget operations and transaction persistence.
 */
public class BudgetService {

    private final Budget budget = new Budget();
    private final BudgetRepository repository;

    /**
     * Creates a service using the supplied repository.
     *
     * @param repository the transaction repository
     */
    public BudgetService(BudgetRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }

        this.repository = repository;

        for (Transaction transaction : repository.findAll()) {
            budget.addTransaction(transaction);
        }
    }

    /**
     * Returns the current budget.
     *
     * @return the current budget
     */
    public Budget getBudget() {
        return budget;
    }

    /**
     * Returns all transactions.
     *
     * @return an unmodifiable list of transactions
     */
    public List<Transaction> findAll() {
        return budget.getTransactions();
    }

    /**
     * Finds a transaction by its unique identifier.
     *
     * @param id the transaction identifier
     * @return the matching transaction
     * @throws IllegalArgumentException if no transaction matches
     */
    public Transaction findById(UUID id) {
        return budget.getTransactions().stream()
                .filter(transaction -> transaction.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Transaction not found: " + id)
                );
    }

    /**
     * Adds and persists a new transaction.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        List<Transaction> updated = new ArrayList<>(budget.getTransactions());
        updated.add(transaction);

        repository.saveAll(updated);
        budget.addTransaction(transaction);
    }
}