package com.coelbudget;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;

/**
 * Represents a collection of financial transactions.
 *
 * <p>Provides operations to calculate the overall balance
 * and aggregate expenses by category.</p>
 */
public class Budget {

    private final List<Transaction> transactions = new ArrayList<>();

    /**
     * Adds a transaction to the budget.
     *
     * @param transaction the transaction to add
     * @throws IllegalArgumentException if transaction is null
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        transactions.add(transaction);
    }

    /**
     * Calculates the current budget balance.
     *
     * <p>Income amounts are added and expense amounts are subtracted.</p>
     *
     * @return the resulting balance, or zero if the budget is empty
     */
    public BigDecimal calculateBalance() {
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                balance = balance.add(transaction.getAmount());
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                balance = balance.subtract(transaction.getAmount());
            }
        }

        return balance;
    }

    /**
     * Returns an unmodifiable snapshot of the budget transactions.
     *
     * <p>Changes to the internal transaction list do not affect
     * the returned list. Transaction objects themselves are not copied.</p>
     *
     * @return an unmodifiable list of transactions
     */
    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    /**
     * Calculates the total expenses for a given category.
     *
     * <p>Only transactions of type {@link TransactionType#EXPENSE}
     * are included in the calculation. Income transactions are ignored.</p>
     *
     * @param category the category used to filter expenses
     * @return the total expense amount, or zero if no expenses match
     * @throws IllegalArgumentException if category is null
     */
    public BigDecimal calculateExpensesByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (transaction.getCategory() == category && transaction.getType() == TransactionType.EXPENSE) {
                total = total.add(transaction.getAmount());
            }

        }

        return total;
    }

    /**
     * Calculates expense totals for all transaction categories.
     *
     * <p>The returned map contains every {@link Category} value,
     * including categories with no expenses, which have a zero total.</p>
     *
     * @return a map associating each category with its total expenses
     */
    public Map<Category, BigDecimal> calculateExpensesByCategories() {
        Map<Category, BigDecimal> expenses = new EnumMap<>(Category.class);

        for (Category category : Category.values()) {
            expenses.put(category, this.calculateExpensesByCategory(category));
        }

        return expenses;
    }

    /**
     * Replaces an existing transaction with an updated transaction.
     *
     * @param updatedTransaction the updated transaction
     * @throws IllegalArgumentException if the transaction does not exist
     */
    public void updateTransaction(Transaction updatedTransaction) {
        if (updatedTransaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        for (int i = 0; i < transactions.size(); i++) {
            Transaction current = transactions.get(i);

            if (current.getId().equals(updatedTransaction.getId())) {
                transactions.set(i, updatedTransaction);
                return;
            }
        }

        throw new IllegalArgumentException("Transaction not found");
    }

    /**
     * Removes a transaction by its unique identifier.
     *
     * @param id the transaction identifier
     * @throws IllegalArgumentException if the transaction does not exist
     */
    public void deleteTransaction(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }

        boolean removed = transactions.removeIf(
                transaction -> transaction.getId().equals(id)
        );

        if (!removed) {
            throw new IllegalArgumentException("Transaction not found");
        }
    }

    /**
     * Calculates the financial summary for a given month.
     *
     * @param month the month to analyze
     * @return the monthly financial report
     * @throws IllegalArgumentException if month is null
     */
    public MonthlyReport calculateMonthlyReport(YearMonth month) {
        if (month == null) {
            throw new IllegalArgumentException("Month cannot be null");
        }

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;

        Map<Category, BigDecimal> expensesByCategory =
                new EnumMap<>(Category.class);

        for (Category category : Category.values()) {
            expensesByCategory.put(category, BigDecimal.ZERO);
        }

        for (Transaction transaction : transactions) {
            if (!YearMonth.from(transaction.getDate()).equals(month)) {
                continue;
            }

            if (transaction.getType() == TransactionType.INCOME) {
                income = income.add(transaction.getAmount());
            } else {
                expenses = expenses.add(transaction.getAmount());

                expensesByCategory.merge(
                        transaction.getCategory(),
                        transaction.getAmount(),
                        BigDecimal::add
                );
            }
        }

        return new MonthlyReport(
                month,
                income,
                expenses,
                income.subtract(expenses),
                expensesByCategory
        );
    }
}
