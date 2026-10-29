# ASMJ Marketplace — Payment Proof Fixes

## Included

- Added `PRINTOUT` to the common payment-proof form.
- Added printout request lookup by `requestNumber` to the common payment service.
- Corrected generic reference resolution for Tutor to use the existing `bookings` collection.
- Resolve the signed-in email to the marketplace user ID before checking customer ownership; resolve vendor profiles before checking vendor ownership.
- Save common payment proof state and proof URL/reference back to the linked order/booking/request as `PENDING_REVIEW`, then update it after admin verification/rejection.
- Added a printout-specific admin review queue for the existing dedicated printout payment-proof upload flow. It appears on Admin → Payments & invoices, alongside the common payment transactions.
- Added validation for missing printout proof/reference and prevented re-reviewing a payment transaction that is no longer pending, avoiding duplicate invoice/revenue recording from repeated review actions.

## Important workflow distinction

Printout has both its existing dedicated proof upload and the common payment-proof form. The dedicated flow is now reviewable by an admin and records `PAID` or `REJECTED` on the printout request. The common flow creates a standard payment transaction and uses the normal invoice/revenue process after verification.

## Validation status

- ZIP integrity is checked after packaging.
- Source inspection was performed.
- A full frontend build could not be completed here because `npm ci` timed out.
- Maven is not installed in this environment, so the Spring Boot backend could not be compiled here.
- Please build and test locally before deploying to Render. Test at minimum: product order, service booking, rental booking, event registration, job application, insurance request, generic booking/tutor booking, and printout request. Confirm that unauthorized users cannot submit against another customer's/vendor's reference, proof appears in Admin → Payments & invoices, and approval/rejection updates the linked record.

## Order/booking selector update

- The payment/refund proof page now loads a server-provided dropdown of related orders and bookings instead of asking the user to type an ID.
- Customer accounts see only records linked to their user ID; vendor accounts see records linked to their vendor profile; admins see all records returned from the supported marketplace collections.
- Selecting a reference sets the category and identifier automatically and pre-fills the record's known amount as an editable starting value.
- The API endpoint is `GET /api/payments/references`, requires authentication, and applies role-aware filtering server-side. Do not remove server-side ownership checks in `PaymentService.submit`.
- Build/test limitation: this environment does not have Maven or installed frontend dependencies (`vite`), so compile/build verification must be completed locally before deployment.
