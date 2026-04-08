# StaySync — Hotel Management System

> A full-featured, standalone JavaFX desktop application for managing hotel rooms, guests, bookings, and checkouts — built as the capstone project of a 10-week Java programming course.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Core Modules](#core-modules)
- [Data Persistence Strategy](#data-persistence-strategy)
- [Multithreading Design](#multithreading-design)
- [Collections & Generics](#collections--generics)
- [GUI Design](#gui-design)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Running the Application](#running-the-application)
- [Usage Guide](#usage-guide)
- [OOP Design Highlights](#oop-design-highlights)
- [Error Handling](#error-handling)
- [Week-by-Week Concept Map](#week-by-week-concept-map)
- [Marking Scheme Alignment](#marking-scheme-alignment)
- [Screenshots](#screenshots)
- [Future Enhancements](#future-enhancements)
- [Author](#author)
- [License](#license)

---

## Overview

**StaySync** is a standalone JavaFX desktop application that automates and centralises hotel management operations. It allows hotel staff to manage room inventory, register and track guests, create bookings, and process checkouts — all through a clean, tab-based graphical interface. Data is persisted between sessions using Java's Object Serialization, and concurrent operations (like background billing calculations) are handled with multithreading.

StaySync was built as the **Week 10 capstone project** for the *Object-Oriented Software Development Lab (OSDL)* course, integrating every concept covered across 10 weeks: OOP, Java library classes, multithreading, synchronization, file I/O, serialization, generics, collections, and JavaFX.

---

## Features

### Room Management
- Add new rooms with Room Number, Room Type (Standard / Deluxe / Suite), price per night, and availability status
- View all rooms in a sortable `TableView`
- Filter to show only available rooms
- Update room details and booking status in-place
- Prevent double-booking via availability validation

### Guest Management
- Register guests with name, contact number, and ID
- View all registered guests
- Auto-assign Room Number on booking
- Remove guest records on checkout

### Booking Management
- Book any available room with a single click
- Automatically mark room as `OCCUPIED` after booking
- Prevent rebooking of an already occupied room with alert dialogs
- View all active bookings in a dedicated tab

### Checkout & Billing
- Check out a guest and release their room back to `AVAILABLE`
- Background billing thread calculates final bill (nights × tariff) without freezing the UI
- Display itemised bill in a confirmation dialog
- **Minimum One-Night Billing Policy**: StaySync enforces a minimum one-night charge on all checkouts. If a guest checks out on the same day they checked in — regardless of the hour — they are billed for exactly one night. This is calculated in `Booking.getNights()` using `ChronoUnit.DAYS.between(checkInDate, checkOutDate)`, with the result floored to a minimum of 1. Since all billing flows through this single method, the policy is enforced consistently across the UI, the background billing thread, and any printed receipts.

### Data Persistence
- All room, guest, and booking data serialized to `.dat` files on disk
- Data loaded automatically on application startup — no data loss between sessions
- Option to manually trigger a Save from the menu bar

### Navigation
- Tab-based interface: **Rooms | Guests | Bookings | Checkout**
- Menu bar with **File → Save**, **File → Exit**, **Help → About**
- Input fields cleared automatically after each successful operation

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| GUI Framework | JavaFX 17+ |
| Concurrency | Java `Thread`, `synchronized`, `wait/notify` |
| Data Storage | Java Object Serialization (`ObjectOutputStream` / `ObjectInputStream`) |
| Collections | `ArrayList`, `HashMap`, `Collections` utility |
| Build Tool | **Maven** (JavaFX dependency management via `pom.xml`) |
| IDE Recommended | IntelliJ IDEA 2023+ or Eclipse 2023+ with e(fx)clipse plugin |

---

## Architecture

StaySync follows a **Model-View-Controller (MVC)** pattern, cleanly separating concerns across three packages:

```
MVC Pattern
───────────────────────────────────────────────────────────
  MODEL                 CONTROLLER              VIEW
  ─────                 ──────────              ────
  Room.java    ←──────  RoomController  ──────► RoomTab.java
  Guest.java   ←──────  GuestController ──────► GuestTab.java
  Booking.java ←──────  BookingController────► BookingTab.java
               ←──────  CheckoutController───► CheckoutTab.java

  DataStore.java (singleton, holds all in-memory lists)
  PersistenceManager.java (handles serialization/deserialization)
  BillingThread.java (background thread for bill calculation)
───────────────────────────────────────────────────────────
```

- **Model**: Plain Java classes implementing `Serializable` and OOP principles
- **Controller**: Event-handling logic; calls model methods; updates the view
- **View**: JavaFX FXML or code-based layouts using `TabPane`, `TableView`, `GridPane`, etc.
- **DataStore**: A singleton class holding the authoritative `ArrayList<Room>`, `ArrayList<Guest>`, and `ArrayList<Booking>` shared across all controllers
- **PersistenceManager**: Responsible for all file I/O using `ObjectOutputStream` and `ObjectInputStream`

---

## Project Structure

```
StaySync/
│
├── src/
│   └── main/
│       └── java/
│           └── com/staysync/
│               ├── main/
│               │   └── MainApp.java              # JavaFX Application entry point
│               │
│               ├── model/
│               │   ├── Room.java                 # Abstract base class (Serializable)
│               │   ├── StandardRoom.java         # Extends Room — no surcharge
│               │   ├── DeluxeRoom.java           # Extends Room — Wi-Fi + breakfast, 20% surcharge
│               │   ├── SuiteRoom.java            # Extends Room — luxury, 40% premium
│               │   ├── Guest.java                # Guest entity (Serializable)
│               │   ├── Booking.java              # Booking entity (Serializable)
│               │   └── RoomType.java             # Enum: STANDARD, DELUXE, SUITE
│               │
│               ├── controller/
│               │   ├── RoomController.java       # Add/view/filter rooms
│               │   ├── GuestController.java      # Register/view guests
│               │   ├── BookingController.java    # Create/view bookings
│               │   └── CheckoutController.java   # Checkout + billing
│               │
│               ├── view/
│               │   ├── RoomTab.java              # JavaFX tab for Room Management
│               │   ├── GuestTab.java             # JavaFX tab for Guest Management
│               │   ├── BookingTab.java           # JavaFX tab for Bookings
│               │   └── CheckoutTab.java          # JavaFX tab for Checkout
│               │
│               └── util/
│                   ├── DataStore.java            # Singleton: in-memory data store
│                   ├── PersistenceManager.java   # Serialization / Deserialization
│                   ├── BillingThread.java        # Background billing thread
│                   └── Repository.java           # Generic <T> repository class
│
├── data/                                         # Auto-created at runtime; stores .dat files
│   ├── rooms.dat
│   ├── guests.dat
│   └── bookings.dat
│
├── pom.xml                                       # Maven build — JavaFX dependency only
├── README.md
└── StaySync.iml                                  # IntelliJ project file
```

---

## Core Modules

### `pom.xml` — Maven Build

JavaFX is the only external dependency. No web server, no database framework.

```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.6</version>
</dependency>
```

### `Room.java` — Abstract Model

```java
public class Room implements Serializable {
    private int roomNumber;
    private RoomType roomType;       // enum
    private double pricePerNight;
    private boolean available;

    // Constructor overloading (Week 1)
    public Room(int roomNumber, RoomType roomType) { ... }
    public Room(int roomNumber, RoomType roomType, double pricePerNight) { ... }

    // Getters / Setters (Encapsulation — Week 1)
    public double getPricePerNight() { return pricePerNight; }
    public void setAvailable(boolean available) { this.available = available; }
}
```

### `RoomType.java` — Enum with Constructor and Methods

```java
public enum RoomType {
    STANDARD(2000),
    DELUXE(3500),
    SUITE(5000);

    private final int basePrice;

    RoomType(int basePrice) { this.basePrice = basePrice; }

    public int getBasePrice() { return basePrice; }

    public double calculateCost(int nights) { return basePrice * nights; }
}
```

### `Booking.java` — Model

```java
public class Booking implements Serializable {
    private int bookingId;
    private Guest guest;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private boolean active;

    public double calculateTotal() {
        long nights = Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
        return room.getPricePerNight() * nights;
    }
}
```

### `Repository.java` — Generic Class (Week 7)

```java
public class Repository<T> {
    private final List<T> items = new ArrayList<>();

    public void add(T item) { items.add(item); }
    public T get(int index) { return items.get(index); }
    public List<T> getAll() { return Collections.unmodifiableList(items); }
    public boolean remove(T item) { return items.remove(item); }
    public int size() { return items.size(); }
}
```

Used as: `Repository<Room>`, `Repository<Guest>`, `Repository<Booking>`.

### `BillingThread.java` — Background Thread (Week 3 & 4)

```java
public class BillingThread extends Thread {
    private final Booking booking;
    private final Consumer<Double> onComplete;

    public BillingThread(Booking booking, Consumer<Double> onComplete) {
        this.booking = booking;
        this.onComplete = onComplete;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(800); // Simulate processing delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        double total = booking.calculateTotal();
        Platform.runLater(() -> onComplete.accept(total));
    }
}
```

### `PersistenceManager.java` — Serialization (Week 6)

```java
public class PersistenceManager {
    public static <T> void save(List<T> data, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath))) {
            oos.writeObject(data);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> load(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath))) {
            return (List<T>) ois.readObject();
        }
    }
}
```

---

## Data Persistence Strategy

StaySync uses **Java Object Serialization** to persist all data between application sessions — no database required.

| File | Contents | Format |
|---|---|---|
| `data/rooms.dat` | `List<Room>` | Serialized Java object |
| `data/guests.dat` | `List<Guest>` | Serialized Java object |
| `data/bookings.dat` | `List<Booking>` | Serialized Java object |

**On startup**: `PersistenceManager.load()` deserializes each file and populates the `DataStore` singleton.

**On save / exit**: `PersistenceManager.save()` serializes the current in-memory lists back to disk.

All model classes implement `java.io.Serializable` and declare a `serialVersionUID` for forward compatibility.

---

## Multithreading Design

StaySync uses multithreading in two ways:

### 1. Background Billing Calculation

When a checkout is triggered, a `BillingThread` computes the total bill without freezing the JavaFX UI thread. It uses `Platform.runLater()` to safely update the UI once the computation is done.

### 2. Concurrent Room Booking (Synchronization)

The `DataStore` class uses `synchronized` methods to prevent race conditions when multiple booking threads attempt to book the same room simultaneously:

```java
public synchronized boolean bookRoom(int roomNumber, Guest guest) {
    Room room = findRoom(roomNumber);
    if (room == null || !room.isAvailable()) return false;
    room.setAvailable(false);
    // create and store booking
    return true;
}
```

Inter-thread communication uses `wait()` / `notify()` in the booking queue when no rooms are available, following the producer-consumer pattern.

---

## Collections & Generics

| Usage | Class / Interface | Purpose |
|---|---|---|
| Room storage | `ArrayList<Room>` | Dynamic resizable list of all rooms |
| Guest storage | `ArrayList<Guest>` | Dynamic resizable list of all guests |
| Booking storage | `ArrayList<Booking>` | Active and historical bookings |
| Room lookup | `HashMap<Integer, Room>` | O(1) lookup by room number |
| Sorting | `Collections.sort()` with `Comparator` | Sort rooms by price or number |
| Generic store | `Repository<T>` | Type-safe generic repository |
| Wrapper use | `Integer`, `Double` | Stored in collections, autoboxed from primitives |
| Iteration | `Iterator<T>`, enhanced for | Traversing lists safely |

Sorting example:
```java
// Sort rooms by price ascending
rooms.sort(Comparator.comparingDouble(Room::getPricePerNight));

// Sort rooms by room number
rooms.sort(Comparator.comparingInt(Room::getRoomNumber));
```

---

## GUI Design

StaySync uses a **tab-based JavaFX interface** with the following layout:

```
┌─────────────────────────────────────────────────────────┐
│  StaySync                        File  Help             │  ← MenuBar
├──────────┬──────────┬────────────┬─────────────────────┤
│  Rooms   │  Guests  │  Bookings  │  Checkout            │  ← TabPane
├──────────┴──────────┴────────────┴─────────────────────┤
│                                                          │
│  [TableView — dynamically populated from DataStore]      │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │  GridPane Form: Room No | Type | Price | Status   │  │
│  └────────────────────────────────────────────────────┘  │
│                                                          │
│  [ Add Room ]  [ View All ]  [ Show Available ]          │  ← HBox buttons
└─────────────────────────────────────────────────────────┘
```

**JavaFX Controls Used:**

| Control | Usage |
|---|---|
| `TabPane` + `Tab` | Main navigation between modules |
| `TableView<T>` + `TableColumn<T,?>` | Display rooms, guests, bookings |
| `GridPane` | Form layout for data entry |
| `VBox` / `HBox` | Grouping labels, buttons, sections |
| `TextField` | Room number, price, guest name, contact |
| `ComboBox<RoomType>` | Select room type from enum values |
| `Button` | Add, Book, Checkout, Save, Clear |
| `Label` | Field labels and status messages |
| `Alert` | Confirmation and error dialogs |
| `MenuBar` / `Menu` / `MenuItem` | File → Save / Exit, Help → About |

---

## Getting Started

### Prerequisites

- **Java 17 or higher** — [Download JDK](https://adoptium.net/)
- **Maven 3.8+** — [Download Maven](https://maven.apache.org/download.cgi) *(handles JavaFX automatically via pom.xml)*
- IntelliJ IDEA (recommended) or Eclipse with e(fx)clipse

### Running the Application

#### Option 1 — Maven (Recommended)

```bash
# Clone the repo, then:
mvn clean javafx:run
```

Maven downloads JavaFX automatically — no manual SDK setup required.

#### Option 2 — IntelliJ IDEA (without Maven)

1. Clone or download the project
2. Go to **File → Project Structure → Libraries** and add the JavaFX SDK `lib` folder
3. Edit **Run Configuration** → VM Options:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```
4. Run `MainApp.java`

#### Option 3 — Command Line (without Maven)

```bash
# Compile
javac --module-path /path/to/javafx-sdk/lib \
      --add-modules javafx.controls,javafx.fxml \
      -d out \
      src/main/java/com/staysync/**/*.java

# Run
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp out \
     com.staysync.main.MainApp
```

#### Option 4 — Eclipse

1. Install the **e(fx)clipse** plugin (Help → Eclipse Marketplace)
2. Import the project as a Maven project
3. Run `MainApp.java` — VM arguments are picked up from `pom.xml` automatically

---

## Usage Guide

### Adding a Room
1. Navigate to the **Rooms** tab
2. Enter Room Number, select Room Type from the dropdown, and enter Price per Night
3. Click **Add Room** — the room appears immediately in the table

### Booking a Room
1. Navigate to the **Bookings** tab
2. Select a guest from the Guest dropdown (or register one in the Guests tab first)
3. Select an available room from the Room dropdown
4. Enter Check-in and Check-out dates
5. Click **Book Room** — the room status changes to `OCCUPIED`
   - If the selected room is already occupied, an error alert is shown

### Checking Out
1. Navigate to the **Checkout** tab
2. Select the active booking from the list
3. Click **Checkout** — a background thread calculates the total bill
4. A confirmation dialog displays the itemised bill (nights × rate)
5. The room is released back to `AVAILABLE`

### Saving Data
- Data is auto-saved on application exit
- To manually save: **File → Save**

---

## OOP Design Highlights

| Concept | Where Applied |
|---|---|
| **Encapsulation** | All model fields are `private`; accessed via getters/setters |
| **Inheritance** | `StandardRoom`, `DeluxeRoom`, `SuiteRoom` all extend abstract `Room` |
| **Polymorphism** | `Room room = new DeluxeRoom(...)` — `calculateTariff()` resolves at runtime |
| **Abstraction** | `abstract class Room` with abstract `calculateTariff(int nights)` |
| **Interface** | `Amenities` interface with `provideWifi()`, `provideBreakfast()` |
| **Constructor Overloading** | `Room(int, RoomType)` and `Room(int, RoomType, double)` |
| **`this` / `super`** | `super(roomNumber, roomType)` in `DeluxeRoom` constructor |
| **Enum** | `RoomType` with constructor, field, and methods |
| **Generics** | `Repository<T>` and `Pair<T, U>` generic classes |
| **Wrapper Classes** | `Integer`, `Double` used in collections with autoboxing |
| **Serialization** | All model classes implement `Serializable` |
| **Multithreading** | `BillingThread extends Thread`; synchronized `bookRoom()` |
| **Collections** | `ArrayList`, `HashMap`, `Collections.sort()` with `Comparator` |
| **JavaFX Events** | `button.setOnAction(e -> handler())` lambda event handling |

---

## Error Handling

StaySync handles errors gracefully at every layer:

- **Input Validation**: Fields checked for empty values and invalid types before processing; `NumberFormatException` caught for numeric fields
- **Booking Conflict**: Attempting to book an occupied room shows an `Alert.AlertType.ERROR` dialog
- **File I/O Errors**: `IOException` caught in `PersistenceManager`; user notified via status label if data cannot be loaded or saved
- **Class Not Found**: `ClassNotFoundException` handled during deserialization (e.g., if model class changes)
- **Thread Interruption**: `InterruptedException` caught in all thread sleep/join blocks; thread re-interrupted appropriately
- **Null Safety**: `null` checks before all model lookups; graceful fallback messages shown in the UI

All streams use **try-with-resources** to guarantee closure even on exceptions:

```java
try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
    oos.writeObject(data);
} catch (IOException e) {
    showError("Could not save data: " + e.getMessage());
}
```

---

## Week-by-Week Concept Map

| Week | Topic | Applied in StaySync |
|---|---|---|
| 1 | OOP — Classes, Inheritance, Polymorphism, Abstraction | Abstract `Room`; `StandardRoom`, `DeluxeRoom`, `SuiteRoom`; abstract `calculateTariff()`; `Amenities` interface |
| 2 | Wrapper Classes, Autoboxing, Enums | `RoomType` enum with constructor; `Integer`/`Double` in collections |
| 3 | Multithreading Basics | `BillingThread extends Thread`; `sleep()`, `join()` |
| 4 | Synchronization, wait/notify | `synchronized bookRoom()`; wait/notify for booking queue |
| 5 | File I/O — Byte & Character Streams | FileInputStream/FileOutputStream for backup export; FileReader/Writer for log files |
| 6 | RandomAccessFile, Serialization | `ObjectOutputStream`/`ObjectInputStream` for all data persistence |
| 7 | Generics | `Repository<T>` generic class; `Pair<T, U>` for room-guest association |
| 8 | Collections Framework | `ArrayList`, `HashMap`, `Collections.sort()`, `Iterator` |
| 9 | JavaFX GUI | `TabPane`, `TableView`, `GridPane`, `Button`, `ComboBox`, event handling |
| 10 | Integration | Full application tying all weeks together |


## Future Enhancements

- **Search & Filter**: Full-text search across rooms and guests
- **Report Generation**: Export billing history to PDF using iText
- **Database Integration**: Migrate persistence from serialization to SQLite via JDBC
- **Login System**: Role-based access (Admin vs Receptionist) with password hashing
- **Room Images**: Associate room photos using JavaFX `ImageView`
- **Date Picker**: Replace manual date entry with `DatePicker` control
- **Dashboard**: Overview tab showing occupancy rate, revenue, and upcoming checkouts
- **Notifications**: Alert for upcoming checkouts using a background scheduler thread

---

## Author

**Debrato Ghosh**
B.Tech — Information Technology
Manipal Institute of Technology
Sem-4

Course: Object-Oriented Software Development Lab (OSDL)
Project: Week 10 Capstone — Complete Hotel Management Application

---

## License

This project was developed as an academic assignment.
You are free to use, study, and adapt the code for educational purposes.

---

*StaySync — Sync your stays, simplify your operations.*