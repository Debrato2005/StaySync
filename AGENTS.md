# Repository Guidelines

## Project Structure & Module Organization
Core code is in `src/main/java/com/staysync`, organized by MVC-style packages:
- `model/`: domain entities (`Room`, `Guest`, `Booking`, `RoomType`)
- `controller/`: application logic per feature tab
- `view/`: JavaFX tab views (`RoomTab`, `GuestTab`, `BookingTab`, `CheckoutTab`)
- `util/`: shared services (`DataStore`, `PersistenceManager`, threading helpers)
- `main/`: entry point (`MainApp`)

Tests are in `src/test/java/com/staysync`. Runtime data files are stored in `data/*.dat`. Build output goes to `target/`.

## Build, Test, and Development Commands
Run commands from `StaySync/` (where `pom.xml` exists):
- `mvn clean compile`: clean and compile Java sources.
- `mvn test`: run unit tests.
- `mvn clean javafx:run`: launch the JavaFX app via Maven plugin.
- `mvn -f /abs/path/to/StaySync/pom.xml clean compile`: compile from outside the module directory.

## Coding Style & Naming Conventions
- Java 17 is required (`maven.compiler.source/target=17`).
- Use 4-space indentation and standard Java formatting.
- Class names: `PascalCase` (`BookingController`); methods/fields: `camelCase`.
- Keep package boundaries clear: UI code in `view`, business logic in `controller`, state/domain in `model`.
- Prefer descriptive method names (`calculateTotal`, `loadBookings`) over abbreviations.

## Testing Guidelines
- Framework: JUnit (current test scaffold in `AppTest`).
- Add tests under matching package paths in `src/test/java`.
- Test class naming: `<ClassName>Test` (example: `BookingControllerTest`).
- Focus new tests on booking rules, checkout billing, serialization load/save, and room availability transitions.
- Run `mvn test` before opening a PR.

## Commit & Pull Request Guidelines
Current history uses short messages (`first`, `half structure`), but contributors should use clear, imperative commits:
- `feat: add checkout validation for same-day billing`
- `fix: prevent booking of occupied rooms`

PRs should include:
- concise summary of changes
- why the change is needed
- test evidence (`mvn test` output)
- screenshots/GIFs for JavaFX UI changes
- linked issue/task if available
