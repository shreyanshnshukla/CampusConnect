package campusconnect;

import campusconnect.exception.*;
import campusconnect.model.*;
import campusconnect.repository.*;
import campusconnect.report.Report;
import campusconnect.service.*;
import campusconnect.util.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Command-line entry point. Wires the layered architecture together and
 * hosts the menu loop; contains no business logic itself - every action
 * simply reads input and delegates to a service.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static EventService eventService;
    private static ParticipantService participantService;
    private static RegistrationService registrationService;
    private static SponsorService sponsorService;
    private static SponsorshipService sponsorshipService;
    private static BudgetService budgetService;
    private static RecommendationService recommendationService;
    private static ReportService reportService;

    public static void main(String[] args) {
        wireDependencies();
        System.out.println("CampusConnect started. Data is loaded from/saved to the 'data' directory.");

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: eventMenu(); break;
                case 2: participantMenu(); break;
                case 3: sponsorshipMenu(); break;
                case 4: budgetMenu(); break;
                case 5: recommendationMenu(); break;
                case 6: reportsMenu(); break;
                case 7: running = false; System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }

    private static void wireDependencies() {
        FileManager fileManager = new FileManager();

        EventRepository eventRepository = new EventRepository(fileManager);
        ParticipantRepository participantRepository = new ParticipantRepository(fileManager);
        SponsorRepository sponsorRepository = new SponsorRepository(fileManager);
        SponsorshipRepository sponsorshipRepository = new SponsorshipRepository(fileManager);
        RegistrationRepository registrationRepository = new RegistrationRepository(fileManager);
        ExpenseRepository expenseRepository = new ExpenseRepository(fileManager);

        eventService = new EventService(eventRepository);
        participantService = new ParticipantService(participantRepository);
        registrationService = new RegistrationService(registrationRepository, eventRepository);
        sponsorService = new SponsorService(sponsorRepository);
        sponsorshipService = new SponsorshipService(sponsorshipRepository, eventRepository, sponsorRepository);
        budgetService = new BudgetService(expenseRepository, eventRepository, registrationRepository, sponsorshipService);
        recommendationService = new RecommendationService(eventRepository, sponsorRepository, sponsorshipService);
        AnalyticsService analyticsService = new AnalyticsService(eventRepository, registrationService, sponsorshipService, budgetService);
        reportService = new ReportService(eventService, registrationService, sponsorshipService, budgetService, analyticsService);
    }

    private static void printMainMenu() {
        System.out.println();
        System.out.println(ReportFormatter.doubleSeparator());
        System.out.println("CAMPUSCONNECT");
        System.out.println("EVENT & SPONSORSHIP MANAGEMENT SYSTEM");
        System.out.println(ReportFormatter.doubleSeparator());
        System.out.println("1. Event Management");
        System.out.println("2. Participant & Registration Management");
        System.out.println("3. Sponsor & Sponsorship Management");
        System.out.println("4. Budget & Expenses");
        System.out.println("5. Sponsor Recommendations");
        System.out.println("6. Reports & Analytics");
        System.out.println("7. Exit");
    }

    // ---------------------------------------------------------------
    // Event Management
    // ---------------------------------------------------------------
    private static void eventMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Event Management ---");
            System.out.println("1. Create event");
            System.out.println("2. View all events");
            System.out.println("3. Search events");
            System.out.println("4. Update event");
            System.out.println("5. View event details");
            System.out.println("6. Change event status");
            System.out.println("7. Cancel event");
            System.out.println("8. Delete event");
            System.out.println("9. Back to main menu");
            int choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1: createEvent(); break;
                    case 2: listEvents(); break;
                    case 3: searchEvents(); break;
                    case 4: updateEvent(); break;
                    case 5: viewEventDetails(); break;
                    case 6: changeEventStatus(); break;
                    case 7: cancelEvent(); break;
                    case 8: deleteEvent(); break;
                    case 9: back = true; break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (InvalidDataException | EventNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void createEvent() throws InvalidDataException {
        String name = readString("Event name: ");
        String club = readString("Organizer club: ");
        String category = readString("Category: ");
        LocalDate date = readDate("Date (yyyy-MM-dd): ");
        String venue = readString("Venue: ");
        int capacity = readInt("Capacity: ");
        double fee = readDouble("Registration fee: ");
        int expected = readInt("Expected participants: ");
        double budget = readDouble("Budget: ");

        Event event = eventService.createEvent(name, club, category, date, venue, capacity, fee, expected, budget);
        System.out.println("Event created: " + event);
    }

    private static void listEvents() {
        List<Event> events = eventService.getAllEvents();
        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        events.forEach(System.out::println);
    }

    private static void searchEvents() {
        String keyword = readString("Search keyword: ");
        List<Event> results = eventService.searchEvents(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching events.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void updateEvent() throws EventNotFoundException, InvalidDataException {
        String eventId = readString("Event ID: ");
        String name = readString("New name: ");
        String venue = readString("New venue: ");
        int capacity = readInt("New capacity: ");
        double fee = readDouble("New registration fee: ");
        double budget = readDouble("New budget: ");
        eventService.updateEvent(eventId, name, venue, capacity, fee, budget);
        System.out.println("Event updated.");
    }

    private static void viewEventDetails() throws EventNotFoundException {
        String eventId = readString("Event ID: ");
        System.out.println(eventService.getEvent(eventId));
    }

    private static void changeEventStatus() throws EventNotFoundException {
        String eventId = readString("Event ID: ");
        EventStatus status = readEnum("New status " + java.util.Arrays.toString(EventStatus.values()) + ": ", EventStatus.class);
        if (status == null) return;
        eventService.updateStatus(eventId, status);
        System.out.println("Status updated.");
    }

    private static void cancelEvent() throws EventNotFoundException {
        String eventId = readString("Event ID: ");
        eventService.cancelEvent(eventId);
        System.out.println("Event cancelled.");
    }

    private static void deleteEvent() throws EventNotFoundException {
        String eventId = readString("Event ID: ");
        eventService.deleteEvent(eventId);
        System.out.println("Event deleted.");
    }

    // ---------------------------------------------------------------
    // Participant & Registration Management
    // ---------------------------------------------------------------
    private static void participantMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Participant & Registration Management ---");
            System.out.println("1. Add participant");
            System.out.println("2. View all participants");
            System.out.println("3. Search participants");
            System.out.println("4. Register participant for event");
            System.out.println("5. Cancel registration");
            System.out.println("6. Mark attendance");
            System.out.println("7. View registrations for event");
            System.out.println("8. View attendance percentage for event");
            System.out.println("9. Back to main menu");
            int choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1: addParticipant(); break;
                    case 2: listParticipants(); break;
                    case 3: searchParticipants(); break;
                    case 4: registerParticipant(); break;
                    case 5: cancelRegistration(); break;
                    case 6: markAttendance(); break;
                    case 7: viewRegistrationsForEvent(); break;
                    case 8: viewAttendancePercentage(); break;
                    case 9: back = true; break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (InvalidDataException | EventNotFoundException | DuplicateRegistrationException
                     | CapacityExceededException | RegistrationNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addParticipant() throws InvalidDataException {
        String name = readString("Name: ");
        String email = readString("Email: ");
        String phone = readString("Phone: ");
        String college = readString("College: ");
        Participant participant = participantService.addParticipant(name, email, phone, college);
        System.out.println("Participant added: " + participant);
    }

    private static void listParticipants() {
        List<Participant> participants = participantService.getAllParticipants();
        if (participants.isEmpty()) {
            System.out.println("No participants found.");
            return;
        }
        participants.forEach(System.out::println);
    }

    private static void searchParticipants() {
        String keyword = readString("Search keyword: ");
        List<Participant> results = participantService.searchParticipants(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching participants.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void registerParticipant() throws EventNotFoundException, DuplicateRegistrationException, CapacityExceededException {
        String eventId = readString("Event ID: ");
        String participantId = readString("Participant ID: ");
        Registration registration = registrationService.register(eventId, participantId);
        System.out.println("Registered: " + registration);
    }

    private static void cancelRegistration() throws RegistrationNotFoundException {
        String registrationId = readString("Registration ID: ");
        registrationService.cancelRegistration(registrationId);
        System.out.println("Registration cancelled.");
    }

    private static void markAttendance() throws RegistrationNotFoundException {
        String registrationId = readString("Registration ID: ");
        registrationService.markAttendance(registrationId);
        System.out.println("Attendance marked.");
    }

    private static void viewRegistrationsForEvent() {
        String eventId = readString("Event ID: ");
        List<Registration> regs = registrationService.getRegistrationsForEvent(eventId);
        if (regs.isEmpty()) {
            System.out.println("No registrations for this event.");
        } else {
            regs.forEach(System.out::println);
        }
    }

    private static void viewAttendancePercentage() {
        String eventId = readString("Event ID: ");
        double percent = registrationService.getAttendancePercentage(eventId);
        System.out.println("Attendance percentage: " + ReportFormatter.percent(percent));
    }

    // ---------------------------------------------------------------
    // Sponsor & Sponsorship Management
    // ---------------------------------------------------------------
    private static void sponsorshipMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Sponsor & Sponsorship Management ---");
            System.out.println("1. Add sponsor");
            System.out.println("2. View all sponsors");
            System.out.println("3. Search sponsors");
            System.out.println("4. Update sponsor");
            System.out.println("5. Create sponsorship (assign sponsor to event)");
            System.out.println("6. Update sponsorship status");
            System.out.println("7. Update payment status");
            System.out.println("8. View sponsorships for event");
            System.out.println("9. Back to main menu");
            int choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1: addSponsor(); break;
                    case 2: listSponsors(); break;
                    case 3: searchSponsors(); break;
                    case 4: updateSponsor(); break;
                    case 5: createSponsorship(); break;
                    case 6: updateSponsorshipStatus(); break;
                    case 7: updatePaymentStatus(); break;
                    case 8: viewSponsorshipsForEvent(); break;
                    case 9: back = true; break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (InvalidDataException | SponsorNotFoundException | EventNotFoundException
                     | SponsorshipNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addSponsor() throws InvalidDataException {
        String company = readString("Company name: ");
        String industry = readString("Industry: ");
        String contact = readString("Contact person: ");
        String email = readString("Email: ");
        String phone = readString("Phone: ");
        Sponsor sponsor = sponsorService.addSponsor(company, industry, contact, email, phone);
        System.out.println("Sponsor added: " + sponsor);
    }

    private static void listSponsors() {
        List<Sponsor> sponsors = sponsorService.getAllSponsors();
        if (sponsors.isEmpty()) {
            System.out.println("No sponsors found.");
            return;
        }
        sponsors.forEach(System.out::println);
    }

    private static void searchSponsors() {
        String keyword = readString("Search keyword: ");
        List<Sponsor> results = sponsorService.searchSponsors(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching sponsors.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void updateSponsor() throws SponsorNotFoundException, InvalidDataException {
        String sponsorId = readString("Sponsor ID: ");
        String contact = readString("New contact person: ");
        String email = readString("New email: ");
        String phone = readString("New phone: ");
        sponsorService.updateSponsor(sponsorId, contact, email, phone);
        System.out.println("Sponsor updated.");
    }

    private static void createSponsorship() throws EventNotFoundException, SponsorNotFoundException, InvalidDataException {
        String eventId = readString("Event ID: ");
        String sponsorId = readString("Sponsor ID: ");
        double amount = readDouble("Sponsorship amount: ");
        Sponsorship sponsorship = sponsorshipService.createSponsorship(eventId, sponsorId, amount);
        System.out.println("Sponsorship created (recommended package: " + sponsorship.getPackageType() + "): " + sponsorship);
    }

    private static void updateSponsorshipStatus() throws SponsorshipNotFoundException {
        String sponsorshipId = readString("Sponsorship ID: ");
        SponsorshipStatus status = readEnum("New status " + java.util.Arrays.toString(SponsorshipStatus.values()) + ": ", SponsorshipStatus.class);
        if (status == null) return;
        sponsorshipService.updateStatus(sponsorshipId, status);
        System.out.println("Sponsorship status updated.");
    }

    private static void updatePaymentStatus() throws SponsorshipNotFoundException {
        String sponsorshipId = readString("Sponsorship ID: ");
        PaymentStatus status = readEnum("New payment status " + java.util.Arrays.toString(PaymentStatus.values()) + ": ", PaymentStatus.class);
        if (status == null) return;
        sponsorshipService.updatePaymentStatus(sponsorshipId, status);
        System.out.println("Payment status updated.");
    }

    private static void viewSponsorshipsForEvent() {
        String eventId = readString("Event ID: ");
        List<Sponsorship> sponsorships = sponsorshipService.getSponsorshipsForEvent(eventId);
        if (sponsorships.isEmpty()) {
            System.out.println("No sponsorships for this event.");
        } else {
            sponsorships.forEach(System.out::println);
        }
    }

    // ---------------------------------------------------------------
    // Budget & Expense Management
    // ---------------------------------------------------------------
    private static void budgetMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Budget & Expenses ---");
            System.out.println("1. Add expense");
            System.out.println("2. Update expense");
            System.out.println("3. Delete expense");
            System.out.println("4. View expenses for event");
            System.out.println("5. View expense breakdown");
            System.out.println("6. View revenue / expenses / balance summary");
            System.out.println("7. Back to main menu");
            int choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1: addExpense(); break;
                    case 2: updateExpense(); break;
                    case 3: deleteExpense(); break;
                    case 4: viewExpensesForEvent(); break;
                    case 5: viewExpenseBreakdown(); break;
                    case 6: viewBudgetSummary(); break;
                    case 7: back = true; break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (InvalidDataException | EventNotFoundException | ExpenseNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addExpense() throws EventNotFoundException, InvalidDataException {
        String eventId = readString("Event ID: ");
        ExpenseCategory category = readEnum("Category " + java.util.Arrays.toString(ExpenseCategory.values()) + ": ", ExpenseCategory.class);
        if (category == null) return;
        String description = readString("Description: ");
        double amount = readDouble("Amount: ");
        LocalDate date = readDate("Date (yyyy-MM-dd): ");
        Expense expense = budgetService.addExpense(eventId, category, description, amount, date);
        System.out.println("Expense added: " + expense);
    }

    private static void updateExpense() throws ExpenseNotFoundException, InvalidDataException {
        String expenseId = readString("Expense ID: ");
        String description = readString("New description: ");
        double amount = readDouble("New amount: ");
        budgetService.updateExpense(expenseId, description, amount);
        System.out.println("Expense updated.");
    }

    private static void deleteExpense() throws ExpenseNotFoundException {
        String expenseId = readString("Expense ID: ");
        budgetService.deleteExpense(expenseId);
        System.out.println("Expense deleted.");
    }

    private static void viewExpensesForEvent() {
        String eventId = readString("Event ID: ");
        List<Expense> expenses = budgetService.getExpensesForEvent(eventId);
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded for this event.");
        } else {
            expenses.forEach(System.out::println);
        }
    }

    private static void viewExpenseBreakdown() {
        String eventId = readString("Event ID: ");
        Map<ExpenseCategory, Double> breakdown = budgetService.getExpenseBreakdown(eventId);
        if (breakdown.isEmpty()) {
            System.out.println("No expenses recorded for this event.");
            return;
        }
        breakdown.forEach((category, total) ->
                System.out.println(category + ": " + ReportFormatter.currency(total)));
    }

    private static void viewBudgetSummary() throws EventNotFoundException {
        String eventId = readString("Event ID: ");
        double revenue = budgetService.getTotalRevenue(eventId);
        double expenses = budgetService.getTotalExpenses(eventId);
        double netBalance = budgetService.getNetBalance(eventId);
        double utilization = budgetService.getBudgetUtilizationPercent(eventId);
        System.out.println("Revenue: " + ReportFormatter.currency(revenue));
        System.out.println("Expenses: " + ReportFormatter.currency(expenses));
        System.out.println("Net Balance: " + ReportFormatter.currency(netBalance));
        System.out.println("Budget Utilization: " + ReportFormatter.percent(utilization));
        if (budgetService.isBudgetWarningTriggered(eventId)) {
            System.out.println("WARNING: Budget utilization has crossed the "
                    + AppConstants.BUDGET_WARNING_THRESHOLD_PERCENT + "% threshold!");
        }
    }

    // ---------------------------------------------------------------
    // Sponsor Recommendations
    // ---------------------------------------------------------------
    private static void recommendationMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Sponsor Recommendations ---");
            System.out.println("1. Recommend package for an amount");
            System.out.println("2. Rank sponsors for an event");
            System.out.println("3. Back to main menu");
            int choice = readInt("Enter choice: ");
            try {
                switch (choice) {
                    case 1: recommendPackage(); break;
                    case 2: rankSponsorsForEvent(); break;
                    case 3: back = true; break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (InvalidDataException | EventNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void recommendPackage() throws InvalidDataException {
        double amount = readDouble("Sponsorship amount: ");
        SponsorshipPackageType type = sponsorshipService.recommendPackage(amount);
        System.out.println("Recommended Package: " + type);
    }

    private static void rankSponsorsForEvent() throws EventNotFoundException {
        String eventId = readString("Event ID: ");
        List<RecommendationService.ScoredSponsor> ranked = recommendationService.recommendSponsorsForEvent(eventId);
        if (ranked.isEmpty()) {
            System.out.println("No sponsors available to rank.");
            return;
        }
        System.out.println("Sponsor Recommendations");
        int rank = 1;
        for (RecommendationService.ScoredSponsor scored : ranked) {
            System.out.println(rank++ + ". " + scored.sponsor.getCompanyName() + " - " + scored.score + "/100");
        }
    }

    // ---------------------------------------------------------------
    // Reports & Analytics
    // ---------------------------------------------------------------
    private static void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Reports & Analytics ---");
            System.out.println("1. Event report");
            System.out.println("2. Sponsor report");
            System.out.println("3. Financial report");
            System.out.println("4. Compare two events");
            System.out.println("5. Back to main menu");
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: {
                    Report report = reportService.buildEventReport(readString("Event ID: "));
                    System.out.println(report.generate());
                    break;
                }
                case 2: {
                    Report report = reportService.buildSponsorReport(readString("Event ID: "));
                    System.out.println(report.generate());
                    break;
                }
                case 3: {
                    Report report = reportService.buildFinancialReport(readString("Event ID: "));
                    System.out.println(report.generate());
                    break;
                }
                case 4: {
                    String idA = readString("First event ID: ");
                    String idB = readString("Second event ID: ");
                    System.out.println(reportService.compareEvents(idA, idB));
                    break;
                }
                case 5: back = true; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    // ---------------------------------------------------------------
    // Input helpers - centralize validation-on-read and invalid-input handling
    // ---------------------------------------------------------------
    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return DateUtil.parseDate(input);
            } catch (Exception e) {
                System.out.println("Please enter a date in yyyy-MM-dd format.");
            }
        }
    }

    private static <T extends Enum<T>> T readEnum(String prompt, Class<T> enumClass) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.isEmpty()) return null;
            try {
                return Enum.valueOf(enumClass, input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid value. Please try again (or press Enter to cancel).");
            }
        }
    }
}
