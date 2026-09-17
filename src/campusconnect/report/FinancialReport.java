package campusconnect.report;

import campusconnect.exception.EventNotFoundException;
import campusconnect.model.ExpenseCategory;
import campusconnect.service.BudgetService;
import campusconnect.util.ReportFormatter;

import java.util.Map;

public class FinancialReport extends Report {
    private final String eventId;
    private final BudgetService budgetService;

    public FinancialReport(String eventId, BudgetService budgetService) {
        super("Financial Report");
        this.eventId = eventId;
        this.budgetService = budgetService;
    }

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        try {
            double revenue = budgetService.getTotalRevenue(eventId);
            double expenses = budgetService.getTotalExpenses(eventId);
            double netBalance = budgetService.getNetBalance(eventId);
            Map<ExpenseCategory, Double> breakdown = budgetService.getExpenseBreakdown(eventId);

            sb.append(ReportFormatter.doubleSeparator()).append("\n");
            sb.append("FINANCIAL REPORT for event ").append(eventId).append("\n");
            sb.append(ReportFormatter.doubleSeparator()).append("\n");
            sb.append("Total Revenue: ").append(ReportFormatter.currency(revenue)).append("\n");
            sb.append("Total Expenses: ").append(ReportFormatter.currency(expenses)).append("\n");
            sb.append("Net Balance: ").append(ReportFormatter.currency(netBalance)).append("\n");
            sb.append(ReportFormatter.separator()).append("\n");
            sb.append("Expense Breakdown:\n");
            if (breakdown.isEmpty()) {
                sb.append("  No expenses recorded.\n");
            }
            for (Map.Entry<ExpenseCategory, Double> entry : breakdown.entrySet()) {
                sb.append(String.format("  %-15s %s%n", entry.getKey(), ReportFormatter.currency(entry.getValue())));
            }
        } catch (EventNotFoundException e) {
            sb.append("Could not generate report: ").append(e.getMessage());
        }
        return sb.toString();
    }
}
