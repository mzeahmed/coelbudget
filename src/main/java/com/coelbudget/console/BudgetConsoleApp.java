package com.coelbudget.console;

import com.coelbudget.Category;
import com.coelbudget.Transaction;
import com.coelbudget.TransactionType;
import com.coelbudget.repository.JsonBudgetRepository;
import com.coelbudget.service.BudgetService;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Manages the interactive console interface for CoelBudget.
 *
 * <p>Handles user input and delegates business operations
 * to {@link BudgetService}.</p>
 */
public class BudgetConsoleApp {

    private final Scanner scanner = new Scanner(System.in);

    private final BudgetService budgetService;

    /**
     * Creates the console application and initializes its budget service.
     */
    public BudgetConsoleApp() {
        this.budgetService = new BudgetService(
                new JsonBudgetRepository(Path.of("data", "budget.json"))
        );
    }

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
                    BudgetConsolePrinter.print(
                            this.budgetService.getBudget()
                    );
                    break;

                case "3":
                    BudgetConsolePrinter.printExpensesByCategory(
                            this.budgetService.getBudget()
                    );
                    break;

                case "0":
                    System.out.println("Au revoir !");
                    running = false;
                    break;

                default:
                    BudgetConsolePrinter.printError(
                            "Choix invalide, veuillez réessayer."
                    );
                    break;
            }
        }
    }

    /**
     * Displays the available actions in the console menu.
     */
    private void printMenu() {
        System.out.println("\n========== COELBUDGET ==========");
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
     * Displays all available transaction categories
     * with their corresponding menu numbers.
     */
    private void printCategoryMenu() {
        System.out.println("\nCatégorie de transaction :");

        Category[] categories = Category.values();

        for (int i = 0; i < categories.length; i++) {
            System.out.println(
                    (i + 1) + ". " + categories[i].name()
            );
        }
    }

    /**
     * Prompts the user for transaction details and delegates
     * transaction creation and persistence to the budget service.
     */
    private void addTransaction() {

        // Step 1: Read the transaction description.
        System.out.print("\nDescription de la transaction : ");
        String description = this.scanner.nextLine();

        // Step 2: Read and validate the amount.
        System.out.print("Montant de la transaction : ");
        String amountInput = this.scanner.nextLine();

        BigDecimal amount;

        try {
            amount = new BigDecimal(amountInput.trim());
        } catch (NumberFormatException e) {
            BudgetConsolePrinter.printError("Montant invalide.");
            return;
        }

        // Step 3: Read the transaction type.
        this.printTypeMenu();

        String typeInput = this.scanner.nextLine();

        TransactionType type = switch (typeInput) {
            case "1" -> TransactionType.INCOME;
            case "2" -> TransactionType.EXPENSE;
            default -> null;
        };

        if (type == null) {
            BudgetConsolePrinter.printError(
                    "Type de transaction invalide."
            );
            return;
        }

        // Step 4: Read and validate the category.
        this.printCategoryMenu();

        System.out.print("Votre choix : ");
        String categoryInput = this.scanner.nextLine();

        Category category;

        try {
            int choice = Integer.parseInt(categoryInput.trim());

            Category[] categories = Category.values();

            if (choice < 1 || choice > categories.length) {
                BudgetConsolePrinter.printError(
                        "Catégorie invalide."
                );
                return;
            }

            category = categories[choice - 1];

        } catch (NumberFormatException e) {
            BudgetConsolePrinter.printError(
                    "Catégorie invalide."
            );
            return;
        }

        // Step 5: Create and persist the transaction.
        try {
            Transaction transaction = new Transaction(
                    description,
                    amount,
                    type,
                    category
            );

            this.budgetService.addTransaction(transaction);

            System.out.println(
                    "\nTransaction ajoutée avec succès !"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            BudgetConsolePrinter.printError(e.getMessage());
        }
    }
}
