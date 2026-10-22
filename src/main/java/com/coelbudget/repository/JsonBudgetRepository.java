package com.coelbudget.repository;

import com.coelbudget.Category;
import com.coelbudget.Transaction;
import com.coelbudget.TransactionType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Stores and retrieves budget transactions in a local JSON file.
 *
 * <p>Preserves transaction identifiers across application restarts
 * and supports legacy transactions without identifiers.</p>
 */
public class JsonBudgetRepository implements BudgetRepository {

    private final Path filePath;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    /**
     * Creates a JSON repository using the specified file.
     *
     * @param filePath the path to the JSON storage file
     */
    public JsonBudgetRepository(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        this.filePath = filePath;
    }

    /**
     * Reads transactions from the JSON file.
     *
     * <p>Generates identifiers for legacy transactions that
     * do not have an ID in the JSON file.</p>
     *
     * @return all stored transactions, or an empty list if the file is absent or empty
     * @throws IllegalStateException if the file cannot be read or contains invalid data
     */
    @Override
    public List<Transaction> findAll() {
        if (Files.notExists(filePath)) {
            return List.of();
        }

        try {
            if (Files.size(filePath) == 0) {
                return List.of();
            }

            List<TransactionData> data = this.mapper.readValue(
                    filePath.toFile(),
                    new TypeReference<List<TransactionData>>() {
                    }
            );

            if (data == null) {
                throw new IllegalStateException(
                        "Invalid JSON transaction list"
                );
            }

            return data.stream()
                    .map(item -> new Transaction(
                            item.id() != null ? item.id() : UUID.randomUUID(),
                            item.description(),
                            item.amount(),
                            TransactionType.valueOf(item.type()),
                            Category.valueOf(item.category()),
                            item.date() != null ? item.date() : LocalDate.now()
                    ))
                    .toList();

        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Unable to load budget transactions", e
            );
        }
    }

    /**
     * Saves all transactions to the JSON file.
     *
     * <p>Writes to a temporary file before replacing the destination,
     * reducing the risk of corrupting existing data.</p>
     *
     * @param transactions the transactions to persist
     * @throws IllegalStateException if saving fails
     */
    @Override
    public void saveAll(List<Transaction> transactions) {
        Path temporaryFile = null;

        try {
            Path absolutePath = filePath.toAbsolutePath();
            Path directory = absolutePath.getParent();

            Files.createDirectories(directory);

            List<TransactionData> data = transactions.stream()
                    .map(transaction -> new TransactionData(
                            transaction.getId(),
                            transaction.getDescription(),
                            transaction.getAmount(),
                            transaction.getType().name(),
                            transaction.getCategory().name(),
                            transaction.getDate()
                    ))
                    .toList();

            temporaryFile = Files.createTempFile(
                    directory,
                    "coelbudget-",
                    ".tmp"
            );

            this.mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(temporaryFile.toFile(), data);

            try {
                Files.move(
                        temporaryFile,
                        absolutePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );

            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(
                        temporaryFile,
                        absolutePath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to save budget transactions", e
            );

        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // Best-effort temporary file cleanup.
                }
            }
        }
    }
}
