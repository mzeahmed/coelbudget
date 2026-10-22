package com.coelbudget;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BudgetTest {

    @Test
    void shouldHaveZeroBalanceWhenEmpty() {
        Budget budget = new Budget();

        assertEquals(BigDecimal.ZERO, budget.calculateBalance());
    }

    @Test
    void shouldCalculateBalanceFromIncomeAndExpenses() {
        Budget budget = new Budget();
        budget.addTransaction(transaction(
                "00000000-0000-0000-0000-000000000001",
                "Salaire", "2500.00", TransactionType.INCOME,
                Category.SALARY, LocalDate.of(2026, 1, 1)
        ));
        budget.addTransaction(transaction(
                "00000000-0000-0000-0000-000000000002",
                "Loyer", "800.00", TransactionType.EXPENSE,
                Category.HOUSING, LocalDate.of(2026, 1, 2)
        ));

        assertEquals(new BigDecimal("1700.00"), budget.calculateBalance());
    }

    @Test
    void shouldReturnUnmodifiableTransactionList() {
        Budget budget = new Budget();
        Transaction transaction = transaction(
                "00000000-0000-0000-0000-000000000003",
                "Courses", "45.00", TransactionType.EXPENSE,
                Category.FOOD, LocalDate.of(2026, 1, 3)
        );
        budget.addTransaction(transaction);

        List<Transaction> transactions = budget.getTransactions();

        assertEquals(List.of(transaction), transactions);
        assertThrows(
                UnsupportedOperationException.class,
                () -> transactions.add(transaction)
        );
    }

    @Test
    void shouldCalculateExpensesByCategoryAndIgnoreIncome() {
        Budget budget = new Budget();
        budget.addTransaction(transaction(
                "00000000-0000-0000-0000-000000000004",
                "Courses", "45.00", TransactionType.EXPENSE,
                Category.FOOD, LocalDate.of(2026, 1, 4)
        ));
        budget.addTransaction(transaction(
                "00000000-0000-0000-0000-000000000005",
                "Remboursement", "20.00", TransactionType.INCOME,
                Category.FOOD, LocalDate.of(2026, 1, 5)
        ));

        Map<Category, BigDecimal> expenses = budget.calculateExpensesByCategories();

        assertEquals(new BigDecimal("45.00"), budget.calculateExpensesByCategory(Category.FOOD));
        assertEquals(BigDecimal.ZERO, budget.calculateExpensesByCategory(Category.HOUSING));
        assertEquals(new BigDecimal("45.00"), expenses.get(Category.FOOD));
        assertEquals(BigDecimal.ZERO, expenses.get(Category.HOUSING));
        assertEquals(Category.values().length, expenses.size());
    }

    @Test
    void shouldReplaceExistingTransaction() {
        Budget budget = new Budget();
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000006");
        budget.addTransaction(transaction(
                id.toString(), "Courses", "45.00", TransactionType.EXPENSE,
                Category.FOOD, LocalDate.of(2026, 1, 6)
        ));
        Transaction updated = transaction(
                id.toString(), "Courses hebdomadaires", "60.00",
                TransactionType.EXPENSE, Category.FOOD,
                LocalDate.of(2026, 1, 6)
        );

        budget.updateTransaction(updated);

        assertEquals(List.of(updated), budget.getTransactions());
        assertEquals(new BigDecimal("-60.00"), budget.calculateBalance());
    }

    @Test
    void shouldRejectMissingOrNullTransactionUpdate() {
        Budget budget = new Budget();
        Transaction missing = transaction(
                "00000000-0000-0000-0000-000000000007",
                "Transport", "10.00", TransactionType.EXPENSE,
                Category.TRANSPORT, LocalDate.of(2026, 1, 7)
        );

        assertThrows(IllegalArgumentException.class, () -> budget.updateTransaction(missing));
        assertThrows(IllegalArgumentException.class, () -> budget.updateTransaction(null));
    }

    @Test
    void shouldDeleteTransactionAndRejectMissingOrNullIdentifier() {
        Budget budget = new Budget();
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000008");
        budget.addTransaction(transaction(
                id.toString(), "Transport", "10.00", TransactionType.EXPENSE,
                Category.TRANSPORT, LocalDate.of(2026, 1, 8)
        ));

        budget.deleteTransaction(id);

        assertEquals(List.of(), budget.getTransactions());
        assertThrows(IllegalArgumentException.class, () -> budget.deleteTransaction(id));
        assertThrows(IllegalArgumentException.class, () -> budget.deleteTransaction(null));
    }

    @Test
    void shouldCalculateMonthlyReportForRequestedMonthOnly() {
        Budget budget = new Budget();
        budget.addTransaction(transaction(
                "00000000-0000-0000-0000-000000000009",
                "Salaire", "2500.00", TransactionType.INCOME,
                Category.SALARY, LocalDate.of(2026, 1, 1)
        ));
        budget.addTransaction(transaction(
                "00000000-0000-0000-0000-000000000010",
                "Courses", "125.00", TransactionType.EXPENSE,
                Category.FOOD, LocalDate.of(2026, 1, 10)
        ));
        budget.addTransaction(transaction(
                "00000000-0000-0000-0000-000000000011",
                "Bus", "75.00", TransactionType.EXPENSE,
                Category.TRANSPORT, LocalDate.of(2026, 1, 12)
        ));
        budget.addTransaction(transaction(
                "00000000-0000-0000-0000-000000000012",
                "Salaire février", "2500.00", TransactionType.INCOME,
                Category.SALARY, LocalDate.of(2026, 2, 1)
        ));

        MonthlyReport report = budget.calculateMonthlyReport(YearMonth.of(2026, 1));

        assertEquals(YearMonth.of(2026, 1), report.month());
        assertEquals(new BigDecimal("2500.00"), report.income());
        assertEquals(new BigDecimal("200.00"), report.expenses());
        assertEquals(new BigDecimal("2300.00"), report.balance());
        assertEquals(new BigDecimal("125.00"), report.expensesByCategory().get(Category.FOOD));
        assertEquals(new BigDecimal("75.00"), report.expensesByCategory().get(Category.TRANSPORT));
        assertEquals(BigDecimal.ZERO, report.expensesByCategory().get(Category.HOUSING));
    }

    @Test
    void shouldRejectNullMonth() {
        Budget budget = new Budget();

        assertThrows(IllegalArgumentException.class, () -> budget.calculateMonthlyReport(null));
    }

    private Transaction transaction(
            String id,
            String description,
            String amount,
            TransactionType type,
            Category category,
            LocalDate date
    ) {
        return new Transaction(
                UUID.fromString(id),
                description,
                new BigDecimal(amount),
                type,
                category,
                date
        );
    }
}
