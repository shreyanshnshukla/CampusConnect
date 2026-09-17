# CampusConnect - Campus Event & Sponsorship Management System

A terminal-based Java application for managing campus events, participant
registrations, sponsors, sponsorships, budgets, and event analytics.
Built as an evaluated academic project for the **Programming in Java**
course at VIT.

## Overview

Campus clubs juggle events, registrations, sponsor outreach, and expense
tracking across spreadsheets, chat threads, and paper. CampusConnect
centralizes all of it into one CLI system with six core modules: Event
Management, Participant & Registration Management, Sponsor & Sponsorship
Management, Budget & Expenses, Sponsor Recommendations, and Reports &
Analytics.

See [`statement.md`](statement.md) for the full problem statement, scope,
and target users.

## Features

- **Event Management** - create, search, update, cancel, and delete events;
  track lifecycle status (`UPCOMING`, `ONGOING`, `COMPLETED`, `CANCELLED`).
- **Participant & Registration Management** - register participants for
  events with duplicate-registration and capacity-exceeded protection;
  mark attendance and compute attendance percentage.
- **Sponsor & Sponsorship Management** - manage sponsor contacts, create
  sponsorships tied to a rule-based package tier (`SILVER`/`GOLD`/
  `PLATINUM`), and track sponsorship/payment status.
- **Sponsor Recommendation Engine** - ranks sponsors for a given event
  using a transparent, weighted scoring algorithm (industry relevance,
  audience compatibility, sponsorship capacity, event visibility, past
  partnership - 100 points total). This is a deterministic rule-based
  algorithm, **not machine learning**.
- **Budget & Expense Management** - categorized expenses, revenue
  (registration fees + confirmed sponsorships), net balance, budget
  utilization percentage, and an over-budget warning at a configurable
  threshold.
- **Reports & Analytics** - per-event report, per-event sponsor report,
  per-event financial report (with expense breakdown), and a side-by-side
  comparison of two events. Built with Java Streams over the in-memory
  collections.

## Technologies / Tools Used

- **Java (JDK 17+ recommended, compiles on JDK 11+)** - no external
  frameworks or libraries.
- **Plain text file persistence** (`java.nio.file` / `java.io`) - no
  database, since the Programming in Java syllabus this project targets
  is core Java only (no JDBC).
- **Hand-rolled test harness** - no JUnit dependency, since the project
  has no build tool (Maven/Gradle) available; tests run with plain
  `javac`/`java`.

## Java Concepts Demonstrated

| Concept | Where |
|---|---|
| Classes & Objects, Encapsulation | All `model` classes - private fields with getters/setters |
| Inheritance | `User` (abstract) -> `Organizer`, `Participant` |
| Abstraction & Polymorphism | `Report` (abstract) -> `EventReport`, `SponsorReport`, `FinancialReport` - each overrides `generate()` |
| Constructors (incl. overloading via defaults) | Every model/service/repository class |
| Packages | `model`, `service`, `repository`, `exception`, `util`, `report` |
| Access modifiers | `private` fields throughout, package-private helpers, `public` service APIs |
| Collections Framework | `Map`, `List`, `EnumMap` across repositories and services |
| Custom exceptions | 8 checked exceptions in `exception/` (e.g. `CapacityExceededException`, `DuplicateRegistrationException`) |
| Enums | `EventStatus`, `SponsorshipStatus`, `SponsorshipPackageType`, `PaymentStatus`, `ExpenseCategory`, `RegistrationStatus` |
| File I/O / persistence | `util.FileManager` - reads/writes pipe-delimited text files |
| java.time (Date/Time API) | `LocalDate`/`LocalDateTime` for event dates and registration timestamps, via `util.DateUtil` |
| Streams | Search/filter/aggregation in every `*Service` class |
| Input validation | `util.InputValidator`, plus loop-until-valid input helpers in `Main` |
| Modular/layered design | `model` / `service` / `repository` / `exception` / `util` / `report` |

## Project Architecture

```
Main (CLI menu, no business logic)
   |
   v
service/   <- business rules, validation orchestration, Streams-based queries
   |
   v
repository/  <- in-memory Map cache + load/save via FileManager
   |
   v
util.FileManager  <- raw File I/O (java.nio.file)
```

`model/` holds plain data classes (and the `User` inheritance hierarchy).
`report/` holds the `Report` abstraction and its three subclasses, built
by `ReportService`. `exception/` holds the custom checked exceptions every
service throws instead of catching a blanket `Exception`.

See [`documentation/diagrams.md`](documentation/diagrams.md) for the
system architecture, use case, workflow, class, and sequence diagrams
(Mermaid - renders directly on GitHub).

## Folder Structure

```
CampusConnect/
+-- src/campusconnect/
|   +-- Main.java
|   +-- model/          Event, Sponsor, Sponsorship, Registration, Expense,
|   |                    User/Organizer/Participant, and 6 enums
|   +-- service/         EventService, ParticipantService, RegistrationService,
|   |                    SponsorService, SponsorshipService, BudgetService,
|   |                    RecommendationService, AnalyticsService, ReportService
|   +-- repository/      EventRepository, ParticipantRepository, SponsorRepository,
|   |                    SponsorshipRepository, RegistrationRepository, ExpenseRepository
|   +-- exception/       8 custom checked exceptions
|   +-- util/            AppConstants, InputValidator, DateUtil, FileManager, ReportFormatter
|   +-- report/          Report (abstract), EventReport, SponsorReport, FinancialReport
+-- test/campusconnect/test/
|   +-- TestRunner.java  Runs every test class, prints a pass/fail summary
|   +-- Results.java, SimpleAssert.java, TestUtil.java   Test harness (no JUnit)
|   +-- EventAndParticipantTests.java
|   +-- SponsorshipAndBudgetTests.java
|   +-- RecommendationAndAnalyticsTests.java
+-- documentation/
|   +-- diagrams.md      Architecture / use case / workflow / class / sequence diagrams
+-- data/                Generated at runtime - pipe-delimited .txt persistence files
+-- README.md
+-- statement.md
+-- .gitignore
```

## Prerequisites

- JDK 11 or later installed (`java -version` / `javac -version`).
- No other dependencies. No Maven/Gradle/IDE required - everything runs
  from `javac`/`java` at the terminal.

## Compilation

From the project root, the command below works on **any OS** (Windows
`cmd.exe`, PowerShell, macOS, Linux) because `-sourcepath` tells `javac` to
pull in every class `Main.java` references, instead of relying on shell
globbing that differs between shells:

```
javac -d bin -sourcepath src src/campusconnect/Main.java
```

(Windows users: use backslashes - `src\campusconnect\Main.java`.)

If you'd rather compile every `.java` file explicitly:

- **Linux / macOS (bash):**
  ```bash
  javac -d bin $(find src -name "*.java")
  ```
- **Windows PowerShell:**
  ```powershell
  javac -d bin (Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName })
  ```
- **Windows `cmd.exe`:** does not support command substitution at all -
  use the `-sourcepath` command above instead.

Either approach produces class files under `bin/`.

## Execution

```bash
java -cp bin campusconnect.Main
```

The application creates a `data/` directory on first run and persists all
entities there as plain text files. Running it again picks up exactly
where you left off.

## Testing

The test suite is a small hand-written harness (no JUnit, since no build
tool is available) covering: event creation, event validation, participant
registration, duplicate registration, capacity restriction, sponsor
creation, sponsorship package calculation, budget calculation, expense
validation, the sponsor recommendation algorithm, and analytics
calculations. Each test uses its own throwaway data directory under
`test-data/`, so tests never touch your real `data/`.

Compile and run - works on any OS via `-sourcepath`:

```
javac -cp bin -d bin-test -sourcepath test test/campusconnect/test/TestRunner.java
```

Then run it:

- **Windows (`cmd.exe` or PowerShell):**
  ```
  java -cp bin;bin-test campusconnect.test.TestRunner
  ```
- **macOS / Linux:**
  ```bash
  java -cp bin:bin-test campusconnect.test.TestRunner
  ```

(The classpath separator is `;` on Windows and `:` on macOS/Linux - that's
the only platform difference.)

Expected output ends with a summary line such as `TEST SUMMARY: 22/22 passed`.

## Sample Usage

```
========================================================================
CAMPUSCONNECT
EVENT & SPONSORSHIP MANAGEMENT SYSTEM
========================================================================
1. Event Management
2. Participant & Registration Management
3. Sponsor & Sponsorship Management
4. Budget & Expenses
5. Sponsor Recommendations
6. Reports & Analytics
7. Exit
Enter choice: 1

--- Event Management ---
1. Create event
...
Enter choice: 1
Event name: Tech Fest 2026
Organizer club: Coding Club
Category: Technical
Date (yyyy-MM-dd): 2026-12-01
Venue: Main Auditorium
Capacity: 300
Registration fee: 100
Expected participants: 250
Budget: 150000
Event created: [EVT001] Tech Fest 2026 | Technical | Club: Coding Club | ...
```

A full walkthrough covering every module (event, participant, sponsor,
sponsorship, expense, recommendation, and both report types) was used to
verify this build end-to-end - see **Testing Approach** in the project
report for the exact input sequence and observed output.

## Troubleshooting

- **`javac: command not found`** - you have a JRE but not a JDK installed;
  install a JDK (e.g. `sudo apt install openjdk-21-jdk`).
- **`Error: Could not find or load main class campusconnect.Main`** - make
  sure you compiled with `-d bin` and are running with `-cp bin` from the
  project root (the package path must match `bin/campusconnect/Main.class`).
- **Data looks stale/missing** - the app reads/writes `data/` relative to
  the directory you run `java` from. Always run from the project root.
- **`NumberFormatException`/invalid input never crashes the app** - all
  numeric/date/enum prompts loop until valid input is given; if you pipe a
  script of pre-written answers and it runs out of lines, that's the input
  script ending, not an application bug.

## Future Enhancements

- Optional CSV export of reports for spreadsheet analysis.
- A configuration file for sponsorship package thresholds and the budget
  warning percentage (currently in `util.AppConstants`), so club admins
  can tune them without recompiling.
- Multi-club/multi-tenant support if CampusConnect is adopted campus-wide.
- Migrating persistence to JDBC + SQLite if a future course covers
  databases, without changing the service layer (repositories already
  isolate all persistence behind a stable interface-like API).
