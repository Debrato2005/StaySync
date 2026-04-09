# compileman Final Prompt (for Claude)

```text
You are a senior Java engineer. Apply a focused fix batch to this repository:

Repo root: /home/debrato/OSDL_Mini_Project/StaySync

Use these verified reports as source-of-truth:
- /home/debrato/OSDL_Mini_Project/StaySync/reports/error_master_report.md
- /home/debrato/OSDL_Mini_Project/StaySync/reports/rechecker_report.md

Goal
Implement the verified fixes with minimal, safe changes. Do not refactor unrelated code.

Required code changes
1) Fix test build failure (High)
- In `pom.xml`, add test dependency for JUnit.
- Prefer JUnit 5 (`org.junit.jupiter:junit-jupiter`) and update `AppTest.java` imports/annotation/assertion accordingly.
- Ensure `mvn test` passes.

2) Resolve same-day booking policy inconsistency (High)
- Pick ONE consistent policy and enforce it across docs + code.
- Recommended: allow same-day check-in/check-out and charge minimum 1 night (matches existing `Booking.getNights()`).
- Update `BookingController.createBooking` validation accordingly (allow equal dates).
- Keep/align data-layer validation in `DataStore.bookRoom`.
- Update any user-facing validation message text to match final policy.

3) Add positive room price validation (Medium)
- In `RoomController.addRoom`, reject `price <= 0` with clear error alert.

4) Align documented features with implementation (Medium)
- Either implement or correct docs; choose implementation-first where feasible with low risk:
  - Add MenuBar in `MainApp` with:
    - File -> Save (calls `saveData()`)
    - File -> Exit (saves then closes)
    - Help -> About (simple dialog)
- For claims that are not implemented and risky to add now, update README to accurately reflect current behavior.

5) Checkout/guest behavior mismatch (Medium)
- Resolve mismatch for “remove guest records on checkout”:
  - Option A (preferred for consistency): implement guest removal only if the guest has no other active bookings.
  - Option B: keep guest history and update README to remove that claim.
- Apply one option consistently in code + docs.

6) Active bookings wording mismatch (Medium)
- README says booking tab shows active bookings, but code shows all bookings.
- Either filter booking tab to active only OR update README wording to “all bookings with active status”.
- Ensure final behavior and docs match.

Execution constraints
- Keep changes targeted.
- Add/adjust tests for changed behavior:
  - same-day booking acceptance
  - invalid room price rejection
  - menu save action (at least unit-level behavior where practical)
- Preserve existing serialization format compatibility.

Validation commands (must run and report)
- `cd /home/debrato/OSDL_Mini_Project/StaySync && mvn -q test`
- `cd /home/debrato/OSDL_Mini_Project/StaySync && mvn -q -DskipTests compile`

Output format
1) Summary of implemented fixes
2) File-by-file change list with rationale
3) Test results (commands + key output)
4) Remaining deferred items (if any), each with reason
```
