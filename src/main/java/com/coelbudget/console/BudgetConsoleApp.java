package com.coelbudget.console;

import com.coelbudget.Budget;
import com.coelbudget.Category;
import com.coelbudget.Transaction;
import com.coelbudget.TransactionType;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Manages the interactive console interface for CoelBudget.
 *
 * <p>Handles user input and navigation between menu options.</p>
 */
public class BudgetConsoleApp {

    private final Scanner scanner = new Scanner(System.in);
    private final Budget budget = new Budget();

    /**
     * Starts the interactive console application.
     */
    public void run() {
        boolean running = true;

        while (running) {
            this.printMenu();

            String input = this.scanner.nextLine();

            switch (input) {
                case "1":
                    this.addTransaction();
                    break;
                case "2":
                    BudgetConsolePrinter.print(this.budget);
                    break;
                case "3":
                    BudgetConsolePrinter.printExpensesByCategory(this.budget);
                    break;
                case "0":
                    System.out.println("Quitter");
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide, veuillez réessayer.");
                    break;
            }
        }
    }

    /**
     * Displays the available actions in the console menu.
     */
    private void printMenu() {
        System.out.println("========== COELBUDGET ==========:");
        System.out.println("1. Ajouter une transaction");
        System.out.println("2. Afficher le budget");
        System.out.println("3. Dépenses par catégorie");
        System.out.println("0. Quitter");
        System.out.println("────────────────────────────────────────");
        System.out.print("\nVotre choix : ");
    }

    /**
     * Displays the available transaction types in the console.
     */
    private void printTypeMenu() {
        System.out.println("\nType de transaction :");
        System.out.println("1. Revenu");
        System.out.println("2. Dépense");
        System.out.print("Votre choix : ");
    }

    /**
     * Prompts the user for transaction details and adds
     * a validated transaction to the current budget.
     */
    private void addTransaction() {
        System.out.print("Description de la transaction : ");
        String description = this.scanner.nextLine();

        System.out.print("Montant de la transaction : ");
        String amountInput = this.scanner.nextLine();

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountInput.trim());
        } catch (NumberFormatException e) {
            BudgetConsolePrinter.printError("Montant invalide.");
            return;
        }

        this.printTypeMenu();
        String typeInput = this.scanner.nextLine();

        TransactionType type = switch (typeInput) {
            case "1" -> TransactionType.INCOME;
            case "2" -> TransactionType.EXPENSE;
            default -> null;
        };

        if (type == null) {
            BudgetConsolePrinter.printError("Type de transaction invalide.");
            return;
        }

        this.printCategoryMenu();
        System.out.print("Votre choix : ");
        String categoryInput = this.scanner.nextLine();

        Category category;

        try {
            int choice = Integer.parseInt(categoryInput.trim());
            Category[] categories = Category.values();

            if (choice < 1 || choice > categories.length) {
                BudgetConsolePrinter.printError("Catégorie invalide.");
                return;
            }

            category = categories[choice - 1];

        } catch (NumberFormatException e) {
            BudgetConsolePrinter.printError("Catégorie invalide.");
            return;
        }

        try {
            Transaction transaction = new Transaction(
                    description,
                    amount,
                    type,
                    category
            );

            this.budget.addTransaction(transaction);

            System.out.println("\nTransaction ajoutée avec succès.\n");

        } catch (IllegalArgumentException e) {
            BudgetConsolePrinter.printError(e.getMessage());
        }
    }

    /**
     * Displays all available transaction categories
     * with their corresponding menu numbers.
     */
    private void printCategoryMenu() {
        System.out.println("\nCatégorie de transaction :");

        Category[] categories = Category.values();

        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i].name());
        }
    }
}