# Rental date-overlap availability fix

## What changed

- Vehicle assignment and customer booking validation now compare rental date ranges instead of treating every active-status booking as a permanent vehicle lock.
- The current booking is excluded from the assignment conflict check. This is essential because a rental request already stores its selected vehicle before the vendor assigns it.
- A vehicle in `BOOKED` state can be considered for a different, non-overlapping rental period. `ON_TRIP`, `MAINTENANCE`, and `INACTIVE` vehicles remain unavailable.
- Driver conflicts are checked against date overlap too, and the booking being assigned is excluded from that check.
- Round-trip bookings reserve through `returnDate`; one-way bookings reserve through `travelDate`.
- In-progress trips continue to block assignment regardless of their scheduled dates.

## Notes

Date ranges are treated as inclusive calendar days. This is deliberately conservative: two bookings on the same calendar day conflict even if their pickup times differ.

The source ZIP integrity was checked. A Maven production build could not be run in this environment because Maven is not installed. Deploy to a staging environment and test a same-vehicle non-overlapping booking, an overlapping booking, and an active-trip conflict before relying on the change in production.
