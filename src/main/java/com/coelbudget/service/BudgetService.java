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

    /**
     * Updates an existing transaction and persists the changes.
     *
     * @param updatedTransaction the updated transaction
     * @throws IllegalArgumentException if the transaction does not exist
     */
    public void updateTransaction(Transaction updatedTransaction) {
        if (updatedTransaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        UUID id = updatedTransaction.getId();

        this.findById(id);

        List<Transaction> updated = new ArrayList<>(budget.getTransactions());

        for (int i = 0; i < updated.size(); i++) {
            if (updated.get(i).getId().equals(id)) {
                updated.set(i, updatedTransaction);
                break;
            }
        }

        repository.saveAll(updated);

        budget.updateTransaction(updatedTransaction);
    }

    /**
     * Deletes an existing transaction and persists the changes.
     *
     * @param id the transaction identifier
     * @throws IllegalArgumentException if the transaction does not exist
     */
    public void deleteTransaction(UUID id) {
        this.findById(id);

        List<Transaction> updated = new ArrayList<>(budget.getTransactions());

        updated.removeIf(transaction -> transaction.getId().equals(id));

        repository.saveAll(updated);

        budget.deleteTransaction(id);
    }
}