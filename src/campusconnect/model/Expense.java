package campusconnect.model;

import java.time.LocalDate;

public class Expense {
    private final String expenseId;
    private final String eventId;
    private ExpenseCategory category;
    private String description;
    private double amount;
    private LocalDate date;

    public Expense(String expenseId, String eventId, ExpenseCategory category, String description,
                    double amount, LocalDate date) {
        this.expenseId = expenseId;
        this.eventId = eventId;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public String getExpenseId() { return expenseId; }
    public String getEventId() { return eventId; }
    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    @Override
    public String toString() {
        return String.format("[%s] Event:%s Category:%s Desc:%s Amount:%.2f Date:%s",
                expenseId, eventId, category, description, amount, date);
    }
}
