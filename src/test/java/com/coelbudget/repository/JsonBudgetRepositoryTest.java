package com.coelbudget.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonBudgetRepositoryTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void shouldReturnEmptyListWhenBudgetFileIsEmpty() throws IOException {
        Path budgetFile = Files.createFile(temporaryDirectory.resolve("budget.json"));
        JsonBudgetRepository repository = new JsonBudgetRepository(budgetFile);

        assertEquals(0, repository.findAll().size());
    }

    @Test
    void shouldRejectNonEmptyInvalidJson() throws IOException {
        Path budgetFile = temporaryDirectory.resolve("budget.json");
        Files.writeString(budgetFile, "invalid json");
        JsonBudgetRepository repository = new JsonBudgetRepository(budgetFile);

        assertThrows(IllegalStateException.class, repository::findAll);
    }
}
