# ASMJ Marketplace — Rental Management Enhancements

This update builds on the uploaded Location Picker Fix source and preserves the existing rental booking/driver trip/final-KM workflow.

## Changes

- Vendor Fleet screen now supports editing an existing vehicle and changing its operational availability to Available, Maintenance, or Inactive. Vendor ownership checks remain enforced. Busy vehicles cannot be changed to an unrelated status through the edit form.
- Admin rental master screens now expose Edit actions for cities, routes, fare rules, and discount rules. Existing IDs are retained so POST save endpoints update the same MongoDB records.
- Corrected route interstate detection: a route is interstate when origin and destination state codes differ.
- Admin route save validates different origin/destination and sensible distance bounds.
- Discount rules support `minKm` and `maxKm` distance bands, plus optional vehicle-type, route-ID, and journey-type eligibility. With a blank discount code, the best eligible active distance-band rule is applied automatically. Entering a code applies that specific rule only if its restrictions pass.
- Customer rental page now includes search by vehicle make/model/type, capacity filtering, vehicle type selection, and an explicit vehicle selection. Selected vehicle ID is sent with the quote and booking; the backend checks availability and route permit/insurance requirements before quoting/booking.
- Admin rental booking monitor has a text search for booking number, customer, route and status.

## Distance discount setup

For example, create separate active discount rules with blank code:

- 100–199 KM: 3% discount
- 200–399 KM: 5% discount
- 400 KM and above: 8% discount (set maximum KM to 0)

Set a maximum discount amount to cap the savings. If distance bands overlap, the system selects the eligible rule with the largest calculated discount. Use non-overlapping bands for predictable results. Existing rules with no distance limits are not auto-applied; they remain code-based.

## Local validation

- Frontend source syntax checked for the changed JSX/JS files using the installed TypeScript parser.
- Full Vite production build could not be run because `npm ci` timed out and the required dependency archive was not available in the local npm cache.
- Maven is not installed in this environment, so the Spring Boot build and Java tests could not be run here.

## Test before deployment

1. `cd frontend && npm ci && npm run build`
2. `cd backend && mvn test` (or `mvn package`)
3. Vendor: add vehicle, edit registration/model/seats/insurance/permit, set Maintenance/Inactive, restore Available, delete a non-busy vehicle, and confirm a booked/on-trip vehicle cannot be improperly changed or deleted.
4. Admin: edit a city, route, fare and discount; save and reload to confirm edits persisted.
5. Configure routes in two different states and confirm route displays INTERSTATE; same-state routes display INTRASTATE.
6. Create distance discount rules and test quotes below, inside and above the configured KM bands; test code-based discounts and maximum discount caps.
7. Search vehicles by make/model/type; select a vehicle and confirm the quote/booking references that vehicle. Test expired insurance and insufficient interstate permit coverage.
8. Test one-way and round-trip bookings, actual KM finalisation, and the admin booking search.

Keep the current Render deployment and MongoDB data backed up until these tests pass.
