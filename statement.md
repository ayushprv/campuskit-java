# CampusKit: Campus Equipment Lending Manager

## Problem statement
Student clubs and laboratory desks lend calculators, cameras and other equipment. Paper records make it difficult to know which units are available, who has borrowed them, and which returns are overdue. CampusKit provides a terminal-based lending register with durable records and explicit borrowing rules.

## Objectives and scope
Maintain inventory and member records, issue and return units, preserve loan history, and report availability and overdue items. The application runs locally for one operator and one data directory. Equipment quantities represent interchangeable units of the same type; individual serial numbers, payments, authentication and network access are outside scope.

## Target users
Student club coordinators, laboratory assistants and campus lending-desk operators.

## Functional modules
1. Inventory: register equipment types and inspect available/total quantities.
2. Membership: register and list borrowers using generated IDs.
3. Circulation: issue loans for 1-30 days and record returns; reject unavailable stock, overdue borrowers and more than three active loans per member.
4. Reporting: list loan history, inventory totals and overdue borrowers with days late.

## Non-functional requirements
- Portability: compile for Java 17; use only Java standard-library APIs.
- Reliability: publish complete snapshots using atomic replacement; failed saves must not change in-memory state.
- Integrity: reject corrupt records, duplicate IDs, missing references and over-allocated inventory on load.
- Usability: numbered menu, explicit prompts and recovery after ordinary invalid input; EOF exits safely.
- Maintainability: separate immutable domain records, persistence interface, business rules, reports and console I/O.
- Resource efficiency: intended for small club datasets (hundreds of records); no server or third-party runtime dependencies. Large-dataset performance has not been benchmarked.
- Concurrency: acquire a process-level file lock before opening the register; only one CLI instance can use a directory.

## Acceptance criteria
From an empty data directory, register equipment and a member, issue a loan, verify reduced stock, return the loan, and verify restored stock. Restart and confirm that records remain. Invalid IDs, insufficient stock, invalid dates/durations and duplicate returns must be rejected without corrupting data.
