# Combined Order Visibility and Category Administration Fix

## Changes in this release candidate

- Replaced the basic category list with parent-category and subcategory management: add, edit, activate/deactivate, and delete actions.
- Added an admin-only category listing endpoint so inactive categories remain manageable in the admin screen while public/vendor forms continue to receive active categories only.
- Prevented deletion of a parent category while it still has child subcategories; delete or reassign children first.
- Added product edit/delete actions to vendor product management and delete action to admin product management. Vendor deletion is restricted to the owning vendor; admin deletion is protected by the admin controller role guard.
- Updated My Orders to combine catalogue checkout orders with marketplace PRODUCT/ORDER booking requests. Added loading, refresh, and partial-load/error states.
- Replaced the inline order-history text with a visible timeline dialog that can be closed by button or backdrop click.

## Rental baseline checked in source

The supplied source includes the overdue-unstarted-booking expiry, date-overlap checks with current-booking exclusion, and required positive odometer/final-reading validation. These were retained without modification in this release candidate.

## Verification limitations

- ZIP integrity is verified after packaging.
- Frontend production build was not run because `npm ci` did not finish within the available execution window.
- Backend Maven build was not run because Maven and a Maven wrapper are not present in this environment.
- No live MongoDB/Render end-to-end tests were run.
- Private authenticated Cloudinary delivery/access control for compliance and identity documents remains a mandatory pre-production security gate. Do not treat this release candidate as production-ready until that work and its authorization tests are complete.
