# ASMJ Local Business Network — implementation notes

## What was added to this marketplace baseline

- A responsive local business landing page at `/local`; the same page is served at `/` when the frontend hostname is `local.asmj.co.in`.
- Public business directory API that returns only approved, active vendor profiles and deliberately omits account IDs and uploaded verification documents.
- Local directory cards with business contact/WhatsApp links. The directory limits displayed profiles to addresses containing “Bhawanipatna” or “Kalahandi”; shops should include their town/district in the address.
- Vendor promotion requests with four pilot packages (₹99/7 days, ₹149/7 days, ₹299/7 days and ₹599/30 days), payment-proof upload to Cloudinary, admin verification, campaign dates and view/click counters.
- Vendor promotion page at `/vendor/local-promotions` and admin review queue at `/admin/local-promotions`.
- Vendor registration now sends newly registered vendors to their business-profile form so they can submit a profile for approval.

## Existing platform preserved

The existing marketplace categories, products, services, printout workflow, jobs, events, insurance, rentals, vendor/admin tools, payments, chat and revenue modules were retained. The Local Business Network reuses the existing account, vendor verification, listing, upload and admin infrastructure.

## Local run

1. Configure MongoDB and Cloudinary environment variables as documented in `backend/README.md` and `backend/.env.example`.
2. Run the backend from `backend/` with Java 17 and Maven: `mvn spring-boot:run`.
3. Copy `frontend/.env.example` to `frontend/.env`, install packages with `npm ci`, then run `npm run dev`.
4. Open `http://localhost:5173/local` for the local-network page. The API runs at `http://localhost:8080/api` by default.

## Render / GoDaddy deployment

- Deploy the backend and frontend as the existing services; keep the existing service names and environment variables to avoid disrupting current production features.
- Set `CORS_ALLOWED_ORIGINS` on the backend to the exact deployed frontend origins, including `https://local.asmj.co.in` and any existing ASMJ frontend origin.
- Set the frontend `VITE_API_URL` to the full deployed backend API base URL (for example `https://YOUR-BACKEND.onrender.com/api`). Rebuild/redeploy the frontend after changing it.
- Point `local.asmj.co.in` at the existing frontend service in Render and add that hostname to the frontend service's custom domains. The frontend selects the Local Business Network home page for this hostname.
- Do not change the existing apex-domain routing until the local subdomain is tested.

## Promotion workflow

1. A vendor must have an approved business profile.
2. Vendor uploads payment proof and submits a package request.
3. Request stays `PENDING_VERIFICATION`; no campaign is active yet.
4. Admin opens `/admin/local-promotions`, inspects the proof and approves or rejects it.
5. Approval activates the campaign for its configured duration; end date and view/click metrics are recorded.

The displayed package prices are pilot suggestions, not payment instructions. Configure and publish the official UPI/payment instructions before accepting real payments. Verify the transaction independently before activating a campaign.

## Current pilot limitations / next validation

- This iteration provides the directory and promotion workflow. It does not add online checkout, delivery management or a separate new user database.
- Vendor profiles are listed publicly only after admin approval and when the address contains Bhawanipatna or Kalahandi. Confirm profile data and public contact consent before publishing real shops.
- Build/runtime validation must be completed in an environment with npm dependencies and Maven available. The current offline execution environment did not finish `npm ci`, and Maven is not installed here, so a successful compile cannot be claimed from this workspace.
