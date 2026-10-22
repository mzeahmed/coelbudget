package com.coelbudget.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents the JSON persistence format of a transaction.
 *
 * @param id          the transaction identifier
 * @param description the transaction description
 * @param amount      the transaction amount
 * @param type        the transaction type
 * @param category    the transaction category
 * @param date        the effective transaction date
 */
public record TransactionData(
        UUID id,
        String description,
        BigDecimal amount,
        String type,
        String category,
        LocalDate date
) {
}