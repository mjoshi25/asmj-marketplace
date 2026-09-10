# ASMJ All-in-One Marketplace Backend

Spring Boot 3.5 + Java 17 + MongoDB + JWT + WebSocket/STOMP.

## Included
- USER / VENDOR / ADMIN roles
- Registration with name, email, contact number, password and role
- Admin blocked from public registration
- JWT authentication and `/api/auth/me`
- Vendor verification and admin approval
- Generic posts: PRODUCT, SERVICE, EVENT, INSURANCE, JOB, TUTOR, RENTAL
- Dynamic post attributes
- Post approval/rejection workflow
- Search, views, favorites, enquiries, reviews and notifications
- Chat REST + WebSocket/STOMP foundation
- Vendor and admin analytics foundation
- Swagger/OpenAPI
- Dockerfile and docker-compose
- MongoDB indexes/auto-indexing
- Cloudinary image/document uploads for vendor listings
- Seed admin and seven top-level categories including Rentals

## Run locally
```bash
mvn clean package -DskipTests
mvn spring-boot:run
```
Swagger: http://localhost:8080/swagger-ui.html

## Registration
```json
{
  "name": "Vendor One",
  "email": "vendor@example.com",
  "contactNumber": "9876543210",
  "password": "Password@123",
  "role": "VENDOR"
}
```
Public registration accepts `USER` or `VENDOR`. `ADMIN` must be seeded/managed by the server.

## MongoDB Atlas
Set `MONGODB_URI` in Render/IDE/environment variables.

## Production
Set a strong JWT secret, admin password, Atlas network controls and exact frontend CORS origin. Do not commit secrets.

## Architecture notes

- `user/model`, `user/repository`, and `user/service` are intentionally separate. `UserService` owns profile/password/status/role business logic.
- There is no separate `Admin` model/repository. An administrator is a `User` whose roles contain `ADMIN`.
- Admin-specific business orchestration lives in `admin/service/AdminService`.
- Admin actions are persisted in `admin/model/AdminAuditLog` and `admin/repository/AdminAuditLogRepository`.
- Vendor registration supports both `USER` and `VENDOR` accounts; creating a vendor profile also ensures the `VENDOR` role is present.

## Integrated management endpoints

- `/api/users/me` - user profile
- `/api/vendors/me` - vendor profile
- `/api/vendors/enquiries` - vendor enquiries
- `/api/analytics/vendor` - vendor analytics
- `/api/analytics/admin` - platform analytics
- `/api/admin/users` - admin user management
- `/api/admin/vendors` - vendor management
- `/api/admin/reviews` - review moderation
- `/api/admin/audit-logs` - audit history

## Cloudinary uploads

Set these environment variables before using the vendor listing media uploader:

```text
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
```

Vendor media is uploaded through `POST /api/uploads` as multipart form data with `file` and `purpose`. The listing stores Cloudinary URLs and metadata in MongoDB rather than binary file contents.

Supported listing media purposes include product-images, service-images, event-images, insurance-images, job-images, tutor-images, rental-images and their corresponding document folders.
