package com.coelbudget;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final Category category;
    private final UUID id;

    /**
     * Creates a transaction with the default category.
     *
     * @param description the transaction description
     * @param amount      the positive transaction amount
     * @param type        the transaction type
     */
    public Transaction(String description, BigDecimal amount, TransactionType type, Category category) {
        this(UUID.randomUUID(), description, amount, type, category);
    }

    /**
     * Creates a transaction with a generated identifier.
     *
     * @param description the transaction description
     * @param amount      the positive transaction amount
     * @param type        the transaction type
     * @param category    the transaction category
     */
    public Transaction(UUID id, String description, BigDecimal amount, TransactionType type, Category category) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be blank");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }

        if (category == null) {
            throw new IllegalArgumentException("Category must not be null");
        }

        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
    }

    /**
     * Returns the unique transaction identifier.
     *
     * @return the transaction UUID
     */
    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }
}