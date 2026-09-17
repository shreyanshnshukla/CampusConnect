package campusconnect.test;

import campusconnect.model.Event;
import campusconnect.model.Expense;
import campusconnect.model.ExpenseCategory;
import campusconnect.model.Sponsor;
import campusconnect.model.Sponsorship;
import campusconnect.model.SponsorshipPackageType;
import campusconnect.model.SponsorshipStatus;
import campusconnect.repository.EventRepository;
import campusconnect.repository.ExpenseRepository;
import campusconnect.repository.RegistrationRepository;
import campusconnect.repository.SponsorRepository;
import campusconnect.repository.SponsorshipRepository;
import campusconnect.service.BudgetService;
import campusconnect.service.EventService;
import campusconnect.service.SponsorService;
import campusconnect.service.SponsorshipService;
import campusconnect.util.FileManager;

import java.time.LocalDate;

public class SponsorshipAndBudgetTests {

    public static Results runAll() {
        Results results = new Results();
        System.out.println("\n== Sponsorship & Budget Tests ==");

        testSponsorCreation(results);
        testSponsorshipPackageCalculation(results);
        testBudgetCalculation(results);
        testExpenseValidation(results);

        return results;
    }

    private static void testSponsorCreation(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("sponsor-creation");
            SponsorService sponsorService = new SponsorService(new SponsorRepository(fm));
            Sponsor sponsor = sponsorService.addSponsor("ABC Technologies", "Technology", "Priya Nair",
                    "priya@abctech.com", "9000000000");
            SimpleAssert.assertEquals("sponsor company name stored", "ABC Technologies",
                    sponsor.getCompanyName(), results);
        } catch (Exception e) {
            results.record("sponsor creation", false, "unexpected exception: " + e.getMessage());
        }
    }

    private static void testSponsorshipPackageCalculation(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("sponsorship-package");
            EventRepository eventRepository = new EventRepository(fm);
            EventService eventService = new EventService(eventRepository);
            SponsorRepository sponsorRepository = new SponsorRepository(fm);
            SponsorService sponsorService = new SponsorService(sponsorRepository);
            SponsorshipService sponsorshipService =
                    new SponsorshipService(new SponsorshipRepository(fm), eventRepository, sponsorRepository);

            Event event = eventService.createEvent("Startup Meet", "E-Cell", "Business",
                    LocalDate.now().plusDays(20), "Auditorium", 300, 0, 250, 150000);
            Sponsor sponsor = sponsorService.addSponsor("XYZ Innovations", "Technology", "Arjun Mehta",
                    "arjun@xyz.com", "9111111111");

            Sponsorship sponsorship = sponsorshipService.createSponsorship(event.getEventId(), sponsor.getSponsorId(), 75000);
            SimpleAssert.assertEquals("Rs.75,000 maps to GOLD package", "GOLD",
                    sponsorship.getPackageType().name(), results);

            SponsorshipPackageType silver = sponsorshipService.recommendPackage(25000);
            SimpleAssert.assertEquals("Rs.25,000 maps to SILVER package", "SILVER", silver.name(), results);

            SponsorshipPackageType platinum = sponsorshipService.recommendPackage(150000);
            SimpleAssert.assertEquals("Rs.1,50,000 maps to PLATINUM package", "PLATINUM", platinum.name(), results);
        } catch (Exception e) {
            results.record("sponsorship package calculation", false, "unexpected exception: " + e.getMessage());
        }
    }

    private static void testBudgetCalculation(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("budget-calculation");
            EventRepository eventRepository = new EventRepository(fm);
            EventService eventService = new EventService(eventRepository);
            SponsorRepository sponsorRepository = new SponsorRepository(fm);
            SponsorService sponsorService = new SponsorService(sponsorRepository);
            SponsorshipService sponsorshipService =
                    new SponsorshipService(new SponsorshipRepository(fm), eventRepository, sponsorRepository);
            RegistrationRepository registrationRepository = new RegistrationRepository(fm);
            ExpenseRepository expenseRepository = new ExpenseRepository(fm);
            BudgetService budgetService =
                    new BudgetService(expenseRepository, eventRepository, registrationRepository, sponsorshipService);

            Event event = eventService.createEvent("Cultural Night", "Cultural Club", "Cultural",
                    LocalDate.now().plusDays(15), "Open Ground", 500, 100, 400, 200000);
            Sponsor sponsor = sponsorService.addSponsor("TechCorp", "Technology", "Meera Iyer",
                    "meera@techcorp.com", "9222222222");
            Sponsorship sponsorship = sponsorshipService.createSponsorship(event.getEventId(), sponsor.getSponsorId(), 60000);
            sponsorshipService.updateStatus(sponsorship.getSponsorshipId(), SponsorshipStatus.CONFIRMED);

            budgetService.addExpense(event.getEventId(), ExpenseCategory.VENUE, "Ground booking", 20000, LocalDate.now());
            budgetService.addExpense(event.getEventId(), ExpenseCategory.FOOD, "Catering", 15000, LocalDate.now());

            double totalExpenses = budgetService.getTotalExpenses(event.getEventId());
            SimpleAssert.assertEquals("total expenses sum correctly", 35000.0, totalExpenses, results);

            double revenue = budgetService.getTotalRevenue(event.getEventId());
            SimpleAssert.assertEquals("revenue includes confirmed sponsorship only", 60000.0, revenue, results);

            double netBalance = budgetService.getNetBalance(event.getEventId());
            SimpleAssert.assertEquals("net balance = revenue - expenses", 25000.0, netBalance, results);
        } catch (Exception e) {
            results.record("budget calculation", false, "unexpected exception: " + e.getMessage());
        }
    }

    private static void testExpenseValidation(Results results) {
        try {
            FileManager fm = TestUtil.freshFileManager("expense-validation");
            EventRepository eventRepository = new EventRepository(fm);
            EventService eventService = new EventService(eventRepository);
            SponsorRepository sponsorRepository = new SponsorRepository(fm);
            SponsorshipService sponsorshipService =
                    new SponsorshipService(new SponsorshipRepository(fm), eventRepository, sponsorRepository);
            BudgetService budgetService = new BudgetService(new ExpenseRepository(fm), eventRepository,
                    new RegistrationRepository(fm), sponsorshipService);

            Event event = eventService.createEvent("Alumni Meet", "Alumni Cell", "Networking",
                    LocalDate.now().plusDays(30), "Conference Hall", 150, 0, 100, 50000);

            SimpleAssert.assertThrows("negative expense amount is rejected", () ->
                    budgetService.addExpense(event.getEventId(), ExpenseCategory.MISCELLANEOUS, "Test", -500, LocalDate.now()),
                    results);
            SimpleAssert.assertThrows("expense for unknown event is rejected", () ->
                    budgetService.addExpense("EVT999", ExpenseCategory.MISCELLANEOUS, "Test", 500, LocalDate.now()),
                    results);
        } catch (Exception e) {
            results.record("expense validation setup", false, "unexpected exception: " + e.getMessage());
        }
    }
}
