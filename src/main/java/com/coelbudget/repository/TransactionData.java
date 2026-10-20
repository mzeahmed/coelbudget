package com.coelbudget.repository;

import java.math.BigDecimal;

/**
 * Represents the JSON persistence format of a transaction.
 *
 * @param description the transaction description
 * @param amount      the transaction amount
 * @param type        the transaction type
 * @param category    the transaction category
 */
public record TransactionData(
        String description,
        BigDecimal amount,
        String type,
        String category
) {
}
