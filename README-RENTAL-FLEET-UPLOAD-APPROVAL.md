# ASMJ Marketplace – Rental fleet uploads and approval

## Included
- Vendor fleet form supports multiple vehicle display photos.
- Vehicle document upload slots: RC, insurance, fitness certificate, permit and PUC.
- Driver form supports photo/licence/identity file uploads and persists uploaded URLs in the existing `documents` list.
- Vehicle and driver records carry separate approval status, review note, reviewer and review timestamp fields.
- Admin Rental configuration includes a Fleet approvals tab with document/photo links and Approve, Reject and Suspend actions.
- New vehicle and driver registrations start in PENDING approval.
- Vendor assignment rejects vehicles and drivers with a non-approved review status.
- Public vehicle search excludes vehicles explicitly pending, rejected or suspended; legacy records with no approval field remain visible for backward compatibility.

## Cloudinary configuration
Uses the existing authenticated `/api/uploads` endpoint and existing Cloudinary configuration. Upload folders are `asmj-marketplace/rental/vehicle-images`, `.../vehicle-documents`, and `.../driver-documents`.

**Security note:** the existing generic Cloudinary uploader returns `secure_url` values for ordinary Cloudinary delivery. This update does not implement authenticated/signed private document delivery. Treat uploaded compliance and identity documents as URL-accessible to anyone who obtains the URL. Before collecting highly sensitive identity documents in production, configure Cloudinary authenticated delivery and a server-side authorised document proxy. Do not expose these URLs in customer-facing APIs.

## Deployment
No new environment variables are introduced. Existing Cloudinary credentials must be configured in the backend environment. New uploads use the existing `/api/uploads` endpoint.

## Verification status
ZIP integrity is checked. Frontend production build was not run because `npm ci` timed out in this environment. Maven is not installed here, so backend compilation/tests were not run. Please build locally and test vendor upload, admin review, public vehicle visibility, and assignment gating before production deployment.
