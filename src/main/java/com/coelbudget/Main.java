package com.coelbudget;

import com.coelbudget.console.BudgetConsoleApp;

/**
 * Entry point of the CoelBudget application.
 */
public class Main {

    /**
     * Starts the CoelBudget console application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        BudgetConsoleApp app = new BudgetConsoleApp();
        app.run();
    }
}
