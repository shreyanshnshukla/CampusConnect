package campusconnect.repository;

import campusconnect.model.Expense;
import campusconnect.model.ExpenseCategory;
import campusconnect.util.AppConstants;
import campusconnect.util.DateUtil;
import campusconnect.util.FileManager;

import java.util.*;

public class ExpenseRepository {
    private final Map<String, Expense> expenses = new LinkedHashMap<>();
    private final FileManager fileManager;
    private final String filePath;

    public ExpenseRepository(FileManager fileManager) {
        this.fileManager = fileManager;
        this.filePath = fileManager.resolvePath(AppConstants.EXPENSES_FILE);
        load();
    }

    public void save(Expense expense) {
        expenses.put(expense.getExpenseId(), expense);
        persist();
    }

    public void delete(String expenseId) {
        expenses.remove(expenseId);
        persist();
    }

    public Optional<Expense> findById(String expenseId) {
        return Optional.ofNullable(expenses.get(expenseId));
    }

    public List<Expense> findAll() {
        return new ArrayList<>(expenses.values());
    }

    public List<Expense> findByEventId(String eventId) {
        List<Expense> result = new ArrayList<>();
        for (Expense e : expenses.values()) {
            if (e.getEventId().equals(eventId)) result.add(e);
        }
        return result;
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Expense e : expenses.values()) {
            lines.add(String.join("|", e.getExpenseId(), e.getEventId(), e.getCategory().name(),
                    e.getDescription(), String.valueOf(e.getAmount()), DateUtil.formatDate(e.getDate())));
        }
        fileManager.writeLines(filePath, lines);
    }

    private void load() {
        for (String line : fileManager.readLines(filePath)) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", -1);
            Expense e = new Expense(p[0], p[1], ExpenseCategory.valueOf(p[2]), p[3],
                    Double.parseDouble(p[4]), DateUtil.parseDate(p[5]));
            expenses.put(e.getExpenseId(), e);
        }
    }
}
