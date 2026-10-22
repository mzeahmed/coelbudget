package com.coelbudget;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionTest {

    @Test
    void shouldCreateValidTransaction() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        LocalDate date = LocalDate.of(2026, 1, 1);
        BigDecimal salaryAmount = new BigDecimal("2500.00");

        Transaction salary = new Transaction(
                id,
                "Salaire",
                salaryAmount,
                TransactionType.INCOME,
                Category.SALARY,
                date
        );

        assertEquals(id, salary.getId());
        assertEquals("Salaire", salary.getDescription());
        assertEquals(salaryAmount, salary.getAmount());
        assertEquals(TransactionType.INCOME, salary.getType());
        assertEquals(Category.SALARY, salary.getCategory());
        assertEquals(date, salary.getDate());
    }
}
