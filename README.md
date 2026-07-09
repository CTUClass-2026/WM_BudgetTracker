# CTU Finance Tracker

A small Java Swing personal finance tracker used in exercises and tests.

## Overview
- Java Swing desktop application to add, view, and delete expenses.
- Persistence: `data/expenses.txt` (plain text CSV-like lines).
- Focus: testability, dependency injection, and UI-level integration tests.

## Recent changes
- `FileManager` made injectable (accepts a `Path`) so tests can use temporary files.
- `ExpenseService` introduced as a service layer; panels and `MainFrame` accept an `ExpenseService` instance.
- `LoginManager` has test helpers: `initForTests(Path)`, `setCredentials(...)`, `setLoggedInForTests(boolean)`, `clearBackupHistoryForTests()`, and `isTestMode()`.
- UI panels include lightweight test helpers to programmatically set fields and click buttons.

## Build & Run (manual Java)
Requires JDK 11+ (tested with JDK 25).

Compile:

```powershell
javac -d target\tmp-classes -cp lib\junit-platform-console-standalone-1.10.0.jar src\main\java\com\mycompany\personalfinancetrackerctu\*.java src\test\java\com\mycompany\personalfinancetrackerctu\*.java
```

Run the app:

```powershell
java -cp target\tmp-classes com.mycompany.personalfinancetrackerctu.PersonalFinanceTracker
```

## Tests (without Maven)
This project ships a JUnit Platform standalone console jar in `lib/` used to run the test suite.

Compile (see above), then run:

```powershell
java -jar lib\junit-platform-console-standalone-1.10.0.jar -cp target\tmp-classes --scan-class-path
```

All tests should pass; example: `9 tests successful`.

Alternatively, use the included automation script `run-tests.ps1` which compiles and runs the tests for you:

```powershell
.\run-tests.ps1
```

## Notes for contributors
- To avoid tests touching your real data, use `LoginManager.initForTests(Path)` and construct `FileManager` with a `Path` to a temp directory when writing tests.
- `MainFrame` and panels now accept an injected `ExpenseService` making UI integration tests easier.
- For headless or CI UI testing consider adding AssertJ-Swing; current integration tests use `SwingUtilities.invokeAndWait` and built-in test hooks.

## Questions / Next steps
If you'd like, I can:
- Add an example `run-tests.ps1` script to automate compilation and running tests.
- Add AssertJ-Swing and example UI tests for end-to-end flows.
- Revert the `ViewExpensesPanel` auto-confirm behavior and implement a more explicit test hook.


---
Generated on 2026-07-06.
