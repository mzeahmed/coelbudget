package com.coelbudget;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

/**
 * Represents a monthly financial summary.
 *
 * @param month              the reporting month
 * @param income             total monthly income
 * @param expenses           total monthly expenses
 * @param balance            monthly balance
 * @param expensesByCategory expenses grouped by category
 */
public record MonthlyReport(
        YearMonth month,
        BigDecimal income,
        BigDecimal expenses,
        BigDecimal balance,
        Map<Category, BigDecimal> expensesByCategory
) {
    /**
     * Creates a defensive copy of the category totals.
     */
    public MonthlyReport {
        expensesByCategory = Map.copyOf(expensesByCategory);
    }
}