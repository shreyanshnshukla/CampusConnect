# CampusConnect - Design Diagrams

These diagrams are written in [Mermaid](https://mermaid.js.org/) and
render directly on GitHub. They describe the actual implementation in
`src/campusconnect/` - nothing here is aspirational.

## 1. System Architecture Diagram

```mermaid
flowchart TB
    User["Evaluator / Club Organizer<br/>(terminal)"]

    subgraph CLI["Presentation Layer"]
        Main["Main.java<br/>(menu loop, input validation-on-read,<br/>no business logic)"]
    end

    subgraph Services["Service Layer (business rules)"]
        EventSvc["EventService"]
        PartSvc["ParticipantService"]
        RegSvc["RegistrationService"]
        SponSvc["SponsorService"]
        ShipSvc["SponsorshipService"]
        BudgetSvc["BudgetService"]
        RecSvc["RecommendationService"]
        AnalyticsSvc["AnalyticsService"]
        ReportSvc["ReportService"]
    end

    subgraph Reports["report/ package"]
        ReportBase["Report (abstract)"]
        EventRep["EventReport"]
        SponRep["SponsorReport"]
        FinRep["FinancialReport"]
    end

    subgraph Repos["Repository Layer (in-memory cache + persistence)"]
        EventRepo["EventRepository"]
        PartRepo["ParticipantRepository"]
        SponRepo["SponsorRepository"]
        ShipRepo["SponsorshipRepository"]
        RegRepo["RegistrationRepository"]
        ExpRepo["ExpenseRepository"]
    end

    subgraph Util["util package"]
        FileMgr["FileManager<br/>(java.nio.file I/O)"]
        Validator["InputValidator"]
        DateUtil["DateUtil (java.time)"]
        Constants["AppConstants<br/>(business-rule thresholds)"]
    end

    DataFiles[("data/*.txt<br/>(pipe-delimited persistence)")]

    User --> Main
    Main --> EventSvc
    Main --> PartSvc
    Main --> RegSvc
    Main --> SponSvc
    Main --> ShipSvc
    Main --> BudgetSvc
    Main --> RecSvc
    Main --> ReportSvc

    ReportSvc --> ReportBase
    ReportBase --> EventRep
    ReportBase --> SponRep
    ReportBase --> FinRep
    ReportSvc --> AnalyticsSvc

    EventSvc --> EventRepo
    PartSvc --> PartRepo
    RegSvc --> RegRepo
    RegSvc --> EventRepo
    SponSvc --> SponRepo
    ShipSvc --> ShipRepo
    ShipSvc --> EventRepo
    ShipSvc --> SponRepo
    BudgetSvc --> ExpRepo
    BudgetSvc --> EventRepo
    BudgetSvc --> RegRepo
    BudgetSvc --> ShipSvc
    RecSvc --> EventRepo
    RecSvc --> SponRepo
    RecSvc --> ShipSvc
    AnalyticsSvc --> EventRepo
    AnalyticsSvc --> RegSvc
    AnalyticsSvc --> ShipSvc
    AnalyticsSvc --> BudgetSvc

    EventSvc -.uses.-> Validator
    PartSvc -.uses.-> Validator
    SponSvc -.uses.-> Validator
    ShipSvc -.uses.-> Validator
    BudgetSvc -.uses.-> Validator
    ShipSvc -.uses.-> Constants
    BudgetSvc -.uses.-> Constants
    EventRepo -.uses.-> DateUtil
    RegRepo -.uses.-> DateUtil
    ExpRepo -.uses.-> DateUtil

    EventRepo --> FileMgr
    PartRepo --> FileMgr
    SponRepo --> FileMgr
    ShipRepo --> FileMgr
    RegRepo --> FileMgr
    ExpRepo --> FileMgr
    FileMgr --> DataFiles
```

## 2. Use Case Diagram

```mermaid
flowchart LR
    Organizer((Club Organizer))

    Organizer --> UC1[Create / update / cancel event]
    Organizer --> UC2[Register participant]
    Organizer --> UC3[Mark attendance]
    Organizer --> UC4[Add sponsor]
    Organizer --> UC5[Create sponsorship]
    Organizer --> UC6[Get package recommendation]
    Organizer --> UC7[Rank sponsors for event]
    Organizer --> UC8[Add / categorize expense]
    Organizer --> UC9[View budget summary]
    Organizer --> UC10[Generate event/sponsor/financial report]
    Organizer --> UC11[Compare two events]

    UC2 -.includes.-> UC2a[Check capacity]
    UC2 -.includes.-> UC2b[Check duplicate registration]
    UC5 -.includes.-> UC6
    UC9 -.includes.-> UC9a[Trigger budget warning if over threshold]
```

## 3. Process Flow / Workflow Diagram - Registering a Participant

```mermaid
flowchart TD
    A[Organizer selects<br/>Register participant] --> B[Enter Event ID + Participant ID]
    B --> C{Event exists?}
    C -- No --> D[Throw EventNotFoundException<br/>show error, return to menu]
    C -- Yes --> E{Already registered<br/>and active?}
    E -- Yes --> F[Throw DuplicateRegistrationException<br/>show error, return to menu]
    E -- No --> G{Active registrations<br/>>= capacity?}
    G -- Yes --> H[Throw CapacityExceededException<br/>show error, return to menu]
    G -- No --> I[Create Registration<br/>status=REGISTERED, attendance=false]
    I --> J[Save via RegistrationRepository<br/>persist to registrations.txt]
    J --> K[Display confirmation]
```

## 4. Class Diagram (core model + service relationships)

```mermaid
classDiagram
    class User {
        <<abstract>>
        -String userId
        -String name
        -String email
        -String phone
        +describeRole() String*
    }
    class Organizer {
        -String clubName
        +describeRole() String
    }
    class Participant {
        -String college
        +describeRole() String
    }
    User <|-- Organizer
    User <|-- Participant

    class Event {
        -String eventId
        -String name
        -EventStatus status
        -int capacity
        -double budget
    }
    class Sponsor {
        -String sponsorId
        -String companyName
        -String industry
    }
    class Sponsorship {
        -String sponsorshipId
        -SponsorshipPackageType packageType
        -SponsorshipStatus status
        -PaymentStatus paymentStatus
    }
    class Registration {
        -String registrationId
        -boolean attendance
        -RegistrationStatus status
    }
    class Expense {
        -String expenseId
        -ExpenseCategory category
        -double amount
    }

    class Report {
        <<abstract>>
        #String title
        +generate() String*
    }
    class EventReport
    class SponsorReport
    class FinancialReport
    Report <|-- EventReport
    Report <|-- SponsorReport
    Report <|-- FinancialReport

    class EventService
    class ParticipantService
    class RegistrationService
    class SponsorService
    class SponsorshipService
    class BudgetService
    class RecommendationService
    class AnalyticsService
    class ReportService

    EventService ..> Event : creates/manages
    ParticipantService ..> Participant : creates/manages
    RegistrationService ..> Registration : creates/manages
    RegistrationService ..> Event : reads capacity
    SponsorService ..> Sponsor : creates/manages
    SponsorshipService ..> Sponsorship : creates/manages
    BudgetService ..> Expense : creates/manages
    RecommendationService ..> Sponsor : scores
    RecommendationService ..> Event : reads
    ReportService ..> Report : builds
```

## 5. Sequence Diagram - Creating a Sponsorship (with package recommendation)

```mermaid
sequenceDiagram
    actor Organizer
    participant Main
    participant SponsorshipService
    participant EventRepository
    participant SponsorRepository
    participant AppConstants
    participant SponsorshipRepository

    Organizer->>Main: choose "Create sponsorship"
    Main->>Organizer: prompt eventId, sponsorId, amount
    Organizer-->>Main: EVT001, SPN001, 75000
    Main->>SponsorshipService: createSponsorship(EVT001, SPN001, 75000)
    SponsorshipService->>EventRepository: exists(EVT001)?
    EventRepository-->>SponsorshipService: true
    SponsorshipService->>SponsorRepository: exists(SPN001)?
    SponsorRepository-->>SponsorshipService: true
    SponsorshipService->>AppConstants: recommendPackage(75000)
    AppConstants-->>SponsorshipService: GOLD
    SponsorshipService->>SponsorshipRepository: save(new Sponsorship)
    SponsorshipRepository->>SponsorshipRepository: persist to sponsorships.txt
    SponsorshipRepository-->>SponsorshipService: ok
    SponsorshipService-->>Main: Sponsorship(SHP001, GOLD, ...)
    Main-->>Organizer: "Sponsorship created (recommended package: GOLD): ..."
```

## Note on persistence design

No ER diagram / database schema is included because this project uses
**file-based persistence** (see `README.md` - Persistence Decision), as
the Programming in Java syllabus it targets does not cover JDBC. Each
entity is instead stored as one pipe-delimited line per record in its own
`data/*.txt` file, loaded into an in-memory `Map` on startup by the
corresponding `*Repository` class and rewritten on every change.
