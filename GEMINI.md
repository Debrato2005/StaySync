# Gemini Context: StaySync — Hotel Management System

This document provides a comprehensive overview of the **StaySync** project to assist Gemini in understanding its architecture, technologies, and current development state.

## Project Overview
StaySync is a standalone JavaFX desktop application designed to manage hotel operations, including room inventory, guest registration, bookings, and checkouts. It follows the **Model-View-Controller (MVC)** architectural pattern.

- **Primary Technologies:** Java 17+, JavaFX 17+, Maven.
- **Architecture:** MVC with a singleton `DataStore` for in-memory management.
- **Persistence Strategy (Planned):** Java Object Serialization (`.dat` files) via `PersistenceManager`.
- **Concurrency (Planned):** Multithreading for background billing calculations (`BillingThread`) and synchronized booking operations.

## Project Structure
```text
StaySync/
├── src/main/java/com/staysync/
│   ├── main/           # Entry point: MainApp.java
│   ├── model/          # Entities: Room, Booking, Guest, RoomType, etc.
│   ├── controller/     # Business logic and event handlers (currently skeletons)
│   ├── view/           # JavaFX UI components (currently skeletons)
│   └── util/           # Core utilities: DataStore, PersistenceManager, Repository, etc.
├── data/               # Serialized data storage (target for persistence)
└── pom.xml             # Maven configuration
```

## Implementation Roadmap

### ✅ Completed (Development Phase 1)
- **Project Structure:** Fully initialized with Maven and JavaFX configurations (`pom.xml`).
- **Domain Models:** Implementation of the entire entity hierarchy:
    - `Room` (Abstract) + `StandardRoom`, `DeluxeRoom`, `SuiteRoom`.
    - `RoomType` (Enum) with pricing logic.
    - `Guest` and `Booking` models with serialization support.
- **Centralized Data Store:** Singleton `DataStore.java` implemented with basic synchronization.
- **Application Skeleton:** `MainApp.java` stage configuration (900x600 resolution).

### 🚧 Remaining Tasks (Next Steps)
- **Utility Layer:**
    - [ ] `PersistenceManager.java`: Implement `ObjectOutputStream` / `ObjectInputStream` logic.
    - [ ] `BillingThread.java`: Implement multithreaded calculation logic.
    - [ ] `Repository<T>`: Implement generic storage utilities.
- **Controller Layer (Business Logic):**
    - [ ] `RoomController.java`: Add, view, and filter rooms.
    - [ ] `GuestController.java`: Register and track guests.
    - [ ] `BookingController.java`: Handle new booking requests.
    - [ ] `CheckoutController.java`: Process room releases and billing.
- **View Layer (JavaFX UI):**
    - [ ] `RoomTab.java`, `GuestTab.java`, `BookingTab.java`, `CheckoutTab.java`: Implement UI layouts using `TableView`, `GridPane`, and control bindings.
    - [ ] `MainApp.java`: Integrate all tabs into the `TabPane`.
- **Validation & Testing:**
    - [ ] Input validation for forms (numeric checks, null checks).
    - [ ] Persistence testing to ensure data survives application restarts.

## ⚠️ Critical Logic Bugs Found
- **Incorrect Billing Logic:** `Booking.calculateTotal()` currently ignores the room's specific tariff logic (e.g., Deluxe/Suite surcharges). It should call `room.calculateTariff((int)getNights())` instead of directly multiplying by base price.
- **Missing Checkout Logic:** The `DataStore` is missing a `checkout()` method to handle room status restoration and marking bookings as inactive.
- **Billing Thread Redundancy:** `BillingThread` currently recalculates the total instead of fetching a pre-validated total from the model, potentially leading to inconsistencies if the logic is updated in only one place.

## Building and Running
The project uses Maven with the `javafx-maven-plugin`.

- **To run the application:**
  ```bash
  mvn clean javafx:run
  ```
- **To compile the project:**
  ```bash
  mvn compile
  ```

## Development Conventions
- **MVC Pattern:** Strictly separate UI (view), logic (controller), and entities (model).
- **Encapsulation:** All model fields must be `private` with appropriate getters/setters.
- **Thread Safety:** Use the `DataStore` singleton and `synchronized` methods for data mutations to prevent race conditions.
- **Persistence:** All entities must implement `java.io.Serializable` for use with `PersistenceManager`.
- **Clean UI:** Leverage JavaFX layouts (`GridPane`, `VBox`, `HBox`) for a responsive and clean design as outlined in the `README.md`.

## Key Files
- `src/main/java/com/staysync/util/DataStore.java`: The central hub for in-memory data management.
- `src/main/java/com/staysync/model/Room.java`: Base abstract class for the room hierarchy.
- `src/main/java/com/staysync/main/MainApp.java`: The main entry point for the JavaFX application.
