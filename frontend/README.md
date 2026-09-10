# ASMJ All-in-One Marketplace Frontend

React + Vite + Tailwind CSS frontend for the ASMJ Spring Boot + MongoDB marketplace.

## Features
- Landing page with Login and Registration
- Registration includes Name, Email, Contact Number, Password and Role
- USER / VENDOR role selection
- JWT token storage and protected routes
- User dashboard
- Vendor dashboard
- Admin dashboard
- Product, Service, Event, Insurance, Job, Tutor and Rental categories
- Category-specific listing form with dedicated fields for Products, Services, Events, Insurance, Jobs, Tutors and Rentals
- Cloudinary image gallery and category-specific document upload
- Search/filter UI
- Favorites, enquiries and reviews UI
- Notifications UI
- Analytics charts
- Responsive mobile/desktop layout

## Run

```bash
npm install
copy .env.example .env
npm run dev
```

Linux/macOS:

```bash
cp .env.example .env
npm install
npm run dev
```

Default API:
`http://localhost:8080/api`

## Backend expectation

The frontend expects the Spring Boot backend endpoints described in the ASMJ backend README. API adapters are centralized under `src/api`.

## Registration

The landing-page registration form sends:

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "contactNumber": "9876543210",
  "password": "Password@123",
  "role": "USER"
}
```

If the backend uses `mobile` instead of `contactNumber`, change the property in `src/api/authApi.js`.

For production, do not keep JWT tokens in localStorage if your security requirements call for httpOnly cookie-based authentication; the current implementation uses localStorage for straightforward integration with the supplied MVP backend.

## Routes
- `/` Landing page
- `/discover` Marketplace discovery/search
- `/dashboard` User dashboard
- `/vendor` Vendor dashboard
- `/admin` Admin dashboard
- `/admin/approvals` Vendor + listing approval center
