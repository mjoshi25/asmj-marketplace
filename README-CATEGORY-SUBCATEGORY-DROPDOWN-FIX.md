# ASMJ Marketplace – Category / Subcategory dropdown fix

## Changes
- Vendor listing creation and editing now use the active category hierarchy returned by `GET /api/categories`.
- Selecting a parent category clears the previous subcategory and filters child categories by `parentId`.
- Selecting a subcategory stores both `subcategoryId` and its display name in listing attributes.
- Existing listings with a saved `subcategoryId` restore the selection when edited.
- Category loading, request failure/retry, and categories with no configured subcategories have explicit UI states.
- Subcategory-specific product fields from the category-fields implementation are retained.

## API/data requirement
The category API must return active category records with `id`, `name`, `parentId`, and `slug`. A child category's `parentId` must equal the selected parent's `id`. The backend `GET /api/categories` currently returns active categories and the category model contains `parentId`.

## Verification
Source-level integration completed. Automated frontend production build and backend Maven build could not be run in this environment: `npm ci` timed out and Maven is not installed. Verify create/edit flows against the deployed API before release.
