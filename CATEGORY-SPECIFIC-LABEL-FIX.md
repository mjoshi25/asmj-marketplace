# Category-specific listing labels

This update replaces the generic “Chat vendor” and “with the vendor” wording on listing details and the user's enquiry list with category-specific labels.

Examples:
- Product: Chat seller
- Service: Chat service provider
- Event: Chat event organiser
- Tutor: Chat tutor
- Rental: Chat rental provider
- Property: Chat property owner/agent
- Vehicle rental: Chat vehicle rental provider
- Job: Chat employer/recruiter
- Insurance: Chat insurance provider
- Printout: Chat printout shop

The question heading, enquiry description, message placeholder, no-price contact text, and enquiry-list reply action now use the category label. A generic provider fallback remains for unknown category values.

Testing note: source changes were reviewed, but a Vite production build could not be run in this environment because the local frontend dependencies do not contain the Vite executable. Run `npm ci` and `npm run build` from `frontend/` before deployment.
