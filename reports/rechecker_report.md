# rechecker Report

Input reviewed: `reports/error_master_report.md`
Repository: `/home/debrato/OSDL_Mini_Project/StaySync`
Date: 2026-04-09

## 1) Verification of error_master findings

| Finding ID | Verdict | Evidence |
|---|---|---|
| High-1 (Missing JUnit dependency breaks `mvn test`) | VERIFIED | `pom.xml:18-32` has no test dependency; `AppTest.java:3,5` imports JUnit; `mvn -q test` fails with `package org.junit does not exist`. |
| High-2 (Same-day policy conflict) | VERIFIED | README one-night policy at `README.md:67`; UI booking rejects non-`isAfter` at `BookingController.java:39-41`; data layer allows equal dates via `isBefore` check at `DataStore.java:38`. |
| Medium-1 (No positive price guard) | VERIFIED | `RoomController.java:31-33` parses only; no constraint before room creation `:39-45`. |
| Medium-2 (Menu bar features missing) | VERIFIED | README lists menu features at `README.md:72,76`; `MainApp.java:28-53` has tabs only, no `MenuBar`. |
| Medium-3 (Guest removal on checkout missing) | VERIFIED | README claim `README.md:55`; `DataStore.checkout` only toggles booking/room at `DataStore.java:49-56`. |

## 2) New findings discovered by rechecker

### Medium-N1: README says "View all active bookings" but Bookings tab shows all bookings
- Why it matters: user-facing behavior differs from documented feature.
- Evidence:
  - Claim: `README.md:61`
  - Actual list source includes all bookings: `BookingController.java:29-30`
  - Table includes active flag instead of active-only filter: `BookingTab.java:151-154`, `BookingTab.java:160-166`

## 3) Consolidated issue list (severity ordered)
1. High: Missing JUnit dependency breaks test phase (`pom.xml`, `AppTest.java`, `mvn test` output).
2. High: Same-day one-night policy inconsistent across README/controller/data layer (`README`, `BookingController`, `DataStore`).
3. Medium: Room price accepts zero/negative values (`RoomController`).
4. Medium: Menu bar features documented but not implemented (`README`, `MainApp`).
5. Medium: Guest removal on checkout documented but not implemented (`README`, `DataStore`).
6. Medium: "Active bookings" claim mismatches Booking tab behavior (`README`, `BookingController`, `BookingTab`).

