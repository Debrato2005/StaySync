# error_master Report

Repository: `/home/debrato/OSDL_Mini_Project/StaySync`
Date: 2026-04-09

## 1) Findings (severity ordered)

### High-1: Test phase is broken due to missing JUnit dependency
- Why this is an issue: CI/test validation cannot run; `mvn test` fails at `testCompile`.
- Evidence:
  - `mvn -q test` output includes:
    - `package org.junit does not exist`
    - `cannot find symbol class Test`
- Code references:
  - Test imports JUnit 4 APIs: `src/test/java/com/staysync/AppTest.java:3`, `:5`
  - POM has no `junit`/`junit-jupiter` test dependency block: `pom.xml:18-32`

### High-2: Business-rule inconsistency for same-day booking/one-night policy
- Why this is an issue: Documentation states minimum one-night billing should apply to same-day checkout, but booking creation blocks same-day booking.
- Evidence:
  - README explicitly states minimum one-night policy for same-day checkout: `README.md:67`
  - Controller rejects `checkOut == checkIn` via strict `isAfter`: `src/main/java/com/staysync/controller/BookingController.java:39-41`
  - Data layer allows equal dates (`isBefore` only): `src/main/java/com/staysync/util/DataStore.java:38`
- Impact: UI/business behavior is inconsistent; policy cannot be exercised through the booking UI path.

### Medium-1: Invalid room price values are accepted (e.g., zero/negative)
- Why this is an issue: invalid tariff/billing outcomes become possible.
- Evidence:
  - `RoomController.addRoom` parses number but does not enforce `price > 0`: `src/main/java/com/staysync/controller/RoomController.java:31-33`, `:39-45`

### Medium-2: Documented navigation/menu features not implemented in main UI
- Why this is an issue: feature gap between delivered app and stated behavior.
- Evidence:
  - README claims menu bar `File->Save`, `File->Exit`, `Help->About`: `README.md:76`
  - `MainApp` builds only tab layout with no `MenuBar`: `src/main/java/com/staysync/main/MainApp.java:28-53`

### Medium-3: Documented checkout behavior "remove guest records" not implemented
- Why this is an issue: stale guest records remain, contradicting product behavior claim.
- Evidence:
  - README claims remove guest record on checkout: `README.md:55`
  - Checkout only marks booking inactive and room available: `src/main/java/com/staysync/util/DataStore.java:49-56`

## 2) Pending / Unimplemented Items
- Room edit/in-place update feature is documented but no edit flow is present.
  - Claim: `README.md:48`
  - Current room controller supports add/list/filter only: `src/main/java/com/staysync/controller/RoomController.java:14-51`
- Manual save option via menu is documented but UI has no menu action.
  - Claim: `README.md:72`, `README.md:76`
  - Save happens only on window close hook: `src/main/java/com/staysync/main/MainApp.java:47`

## 3) Commands executed + key outputs

```bash
cd /home/debrato/OSDL_Mini_Project/StaySync && mvn -q test
```
Key output:
- `[ERROR] package org.junit does not exist`
- `[ERROR] cannot find symbol class Test`
- Build exits with code 1.

```bash
cd /home/debrato/OSDL_Mini_Project/StaySync && mvn -q -DskipTests compile
```
Key output:
- Exits successfully (code 0), indicating main sources compile while test phase is broken.

## 4) False positives considered and rejected
- Singleton thread-safety in `DataStore.getInstance()` could be a theoretical risk; not promoted as a core defect because current app flow is mostly single-UI-thread startup.
- `PersistenceManager.save()` creating `data/` unconditionally was considered; not promoted because current code paths always use `data/*.dat` and functionally work as-is.
