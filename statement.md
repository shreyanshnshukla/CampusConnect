# Problem Statement

College clubs and committees at VIT Bhopal currently manage campus events -
technical fests, workshops, cultural nights, alumni meets - using a mix of
spreadsheets, WhatsApp messages, paper forms and personal memory. This makes
it hard to answer basic questions during event planning: How many people
have registered? Has this sponsor already paid? Are we over budget? Which
sponsor is the best fit for this event?

CampusConnect is a single, centralized Java command-line application that
replaces those disconnected tools with one consistent system covering an
event's full lifecycle: planning, registration, sponsorship, spending, and
post-event reporting.

## Scope

CampusConnect covers, for a single campus/club context:

- Creating and managing events (status, capacity, venue, budget)
- Registering participants and tracking their attendance
- Managing sponsors and sponsorship deals, including a rule-based
  package recommendation (SILVER/GOLD/PLATINUM) and a sponsor-ranking
  algorithm for a given event
- Recording and categorizing event expenses, and tracking revenue
  (registration fees + confirmed sponsorships) against budget
- Generating event, sponsor, and financial reports, and comparing two
  events side by side

Out of scope: payment gateway integration, multi-club/multi-tenant
accounts, a GUI or web front end, and external notifications (email/SMS).
The application runs entirely from the terminal and persists data to local
text files between runs.

## Target Users

- **Club/event organizers** - the primary users - who create events, manage
  registrations, chase sponsors, and track budgets.
- **Club treasurers/finance leads** who need expense and revenue visibility.
- **Faculty coordinators or auditors** reviewing a club's event reports
  after the fact.

## High-Level Features

1. Event Management - create, update, search, cancel, and track the status
   of campus events.
2. Participant & Registration Management - register participants, enforce
   capacity and duplicate-registration rules, and track attendance.
3. Sponsor & Sponsorship Management - manage sponsor contacts, sponsorship
   deals, package tiers, and payment status.
4. Sponsor Recommendation Engine - a transparent, rule-based scoring
   algorithm (not machine learning) that ranks sponsors for an event.
5. Budget & Expense Management - categorized expenses, revenue tracking,
   net balance, and budget-utilization warnings.
6. Reports & Analytics - per-event, per-sponsor, and financial reports,
   plus a side-by-side comparison of two events.
