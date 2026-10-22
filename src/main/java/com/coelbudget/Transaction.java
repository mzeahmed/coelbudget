package com.coelbudget;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents an immutable financial transaction.
 *
 * <p>Every transaction has a unique identifier, a description,
 * a strictly positive amount, a type, a category and an effective date.</p>
 */
public class Transaction {

    private final UUID id;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final Category category;
    private final LocalDate date;

    /**
     * Creates a transaction with all required properties.
     *
     * @param id          the unique transaction identifier
     * @param description the transaction description
     * @param amount      the strictly positive transaction amount
     * @param type        the transaction type
     * @param category    the transaction category
     * @param date        the effective transaction date
     * @throws IllegalArgumentException if any property is invalid
     */
    public Transaction(UUID id, String description, BigDecimal amount, TransactionType type, Category category, LocalDate date) {
        if (id == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero"
            );
        }

        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        if (date == null) {
            throw new IllegalArgumentException("Transaction date cannot be null");
        }

        this.id = id;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.date = date;
    }

    /**
     * Returns the transaction identifier.
     *
     * @return the UUID
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the transaction description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the transaction amount.
     *
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the transaction type.
     *
     * @return the type
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Returns the transaction category.
     *
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Returns the effective transaction date.
     *
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
}
