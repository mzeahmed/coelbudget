package com.coelbudget.console;

import com.coelbudget.Budget;
import com.coelbudget.Category;
import com.coelbudget.Transaction;
import com.coelbudget.TransactionType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Provides console output utilities for displaying budget information.
 *
 * <p>Handles the presentation of transactions, balances,
 * expenses and error messages.</p>
 */
public class BudgetConsolePrinter {

    private static final String RESET = "\033[0m";
    private static final String RED = "\033[0;31m";
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";
    private static final String BLUE = "\033[0;34m";
    private static final String CYAN = "\033[0;36m";

    /**
     * Prevents instantiation of this utility class.
     */
    private BudgetConsolePrinter() {
    }

    /**
     * Prints a summary of the given budget to the console.
     *
     * @param budget the budget to display
     */
    public static void print(Budget budget) {
        BigDecimal balance = budget.calculateBalance();
        List<Transaction> transactions = budget.getTransactions();

        System.out.println(BLUE + "╔══════════════════════════════════════╗" + RESET);
        System.out.println(BLUE + "║          COELBUDGET — RÉSUMÉ         ║" + RESET);
        System.out.println(BLUE + "╚══════════════════════════════════════╝" + RESET);
        System.out.println(CYAN + "\nTransactions" + RESET);
        System.out.println("────────────────────────────────────────");

        for (Transaction transaction : transactions) {
            printTransaction(transaction);
        }

        System.out.println("────────────────────────────────────────\n");
        printExpensesByCategory(budget);

        System.out.println("────────────────────────────────────────\n");

        String balanceColor = balance.signum() >= 0 ? GREEN : RED;
        System.out.println(balanceColor + "Solde final           : " + balance + RESET + "\n");
    }

    /**
     * Prints an error message to the console in red.
     *
     * @param message the error message to display
     */
    public static void printError(String message) {
        System.out.println(RED + "Erreur : " + message + RESET);
    }

    /**
     * Prints the total expenses for each budget category.
     *
     * <p>All categories are displayed, including those
     * without recorded expenses.</p>
     *
     * @param budget the budget containing the expenses
     */
    public static void printExpensesByCategory(Budget budget) {
        System.out.println(YELLOW + "Dépenses par catégorie : " + RESET);
        System.out.println("────────────────────────────────────────");

        Map<Category, BigDecimal> expenses = budget.calculateExpensesByCategories();

        for (Map.Entry<Category, BigDecimal> entry : expenses.entrySet()) {
            System.out.printf("%-12s : %s%n", entry.getKey(), entry.getValue());
        }
    }

    /**
     * Prints a transaction with its description, category and amount.
     *
     * <p>Income transactions are displayed in green with a plus sign,
     * while expenses are displayed in red with a minus sign.</p>
     *
     * @param transaction the transaction to display
     */
    private static void printTransaction(Transaction transaction) {
        String color = transaction.getType() == TransactionType.INCOME ? GREEN : RED;
        String sign = transaction.getType() == TransactionType.INCOME ? "+" : "-";

        System.out.println(color
                + sign + " " + transaction.getDescription()
                + " [" + transaction.getCategory() + "]"
                + " : " + transaction.getAmount()
                + RESET);
    }
}
