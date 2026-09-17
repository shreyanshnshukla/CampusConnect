package campusconnect.service;

import campusconnect.exception.EventNotFoundException;
import campusconnect.exception.ExpenseNotFoundException;
import campusconnect.exception.InvalidDataException;
import campusconnect.model.Event;
import campusconnect.model.Expense;
import campusconnect.model.ExpenseCategory;
import campusconnect.repository.EventRepository;
import campusconnect.repository.ExpenseRepository;
import campusconnect.repository.RegistrationRepository;
import campusconnect.util.AppConstants;
import campusconnect.util.InputValidator;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class BudgetService {
    private final ExpenseRepository expenseRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final SponsorshipService sponsorshipService;
    private int idCounter;

    public BudgetService(ExpenseRepository expenseRepository, EventRepository eventRepository,
                          RegistrationRepository registrationRepository, SponsorshipService sponsorshipService) {
        this.expenseRepository = expenseRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.sponsorshipService = sponsorshipService;
        this.idCounter = expenseRepository.findAll().size() + 1;
    }

    public Expense addExpense(String eventId, ExpenseCategory category, String description,
                               double amount, LocalDate date) throws EventNotFoundException, InvalidDataException {
        if (!eventRepository.exists(eventId)) {
            throw new EventNotFoundException("No event found with ID: " + eventId);
        }
        InputValidator.requireNonEmpty(description, "Description");
        InputValidator.requirePositive(amount, "Expense amount");

        String expenseId = "EXP" + String.format("%04d", idCounter++);
        Expense expense = new Expense(expenseId, eventId, category, description, amount, date);
        expenseRepository.save(expense);
        return expense;
    }

    public void updateExpense(String expenseId, String description, double amount)
            throws ExpenseNotFoundException, InvalidDataException {
        Expense expense = getExpense(expenseId);
        InputValidator.requireNonEmpty(description, "Description");
        InputValidator.requirePositive(amount, "Expense amount");
        expense.setDescription(description);
        expense.setAmount(amount);
        expenseRepository.save(expense);
    }

    public void deleteExpense(String expenseId) throws ExpenseNotFoundException {
        getExpense(expenseId);
        expenseRepository.delete(expenseId);
    }

    public Expense getExpense(String expenseId) throws ExpenseNotFoundException {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException("No expense found with ID: " + expenseId));
    }

    public List<Expense> getExpensesForEvent(String eventId) {
        return expenseRepository.findByEventId(eventId);
    }

    public Map<ExpenseCategory, Double> getExpenseBreakdown(String eventId) {
        Map<ExpenseCategory, Double> breakdown = new EnumMap<>(ExpenseCategory.class);
        for (Expense e : expenseRepository.findByEventId(eventId)) {
            breakdown.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        return breakdown;
    }

    public double getTotalExpenses(String eventId) {
        return expenseRepository.findByEventId(eventId).stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public double getTotalRevenue(String eventId) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No event found with ID: " + eventId));
        long attendeeCount = registrationRepository.countActiveRegistrations(eventId);
        double registrationRevenue = attendeeCount * event.getRegistrationFee();
        double sponsorshipRevenue = sponsorshipService.getTotalConfirmedSponsorshipForEvent(eventId);
        return registrationRevenue + sponsorshipRevenue;
    }

    public double getNetBalance(String eventId) throws EventNotFoundException {
        return getTotalRevenue(eventId) - getTotalExpenses(eventId);
    }

    public double getBudgetUtilizationPercent(String eventId) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("No event found with ID: " + eventId));
        if (event.getBudget() <= 0) return 0.0;
        return (getTotalExpenses(eventId) / event.getBudget()) * 100.0;
    }

    public boolean isBudgetWarningTriggered(String eventId) throws EventNotFoundException {
        return getBudgetUtilizationPercent(eventId) >= AppConstants.BUDGET_WARNING_THRESHOLD_PERCENT;
    }
}
