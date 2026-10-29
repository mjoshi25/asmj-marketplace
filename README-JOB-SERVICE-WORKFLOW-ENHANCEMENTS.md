# Job and Service Workflow Enhancements

## Job interviews
- Vendor transaction workflow now provides a Schedule / update interview action for applications under review, shortlisted, or already at interview stage.
- The form prompts for scheduled date/time, mode (ONLINE/PHONE/IN_PERSON), meeting URL, physical venue, interviewer name/email, duration, and applicant instructions.
- Scheduling uses the existing `/jobs/vendor/applications/{id}/interview` endpoint, persists the interview fields, updates application status to `INTERVIEW`, and triggers the existing applicant notification.
- Applicants can view interview time, duration, mode, interviewer/contact, venue, instructions, and meeting link from Career Activity > Interviews & offers. Meeting links open in a new tab.

## Service bookings
- Customer's My Service Bookings page now displays the latest status-history updates and any booking notes returned by the API, including timestamps and status-change notes.
- Existing vendor status actions and service-booking APIs are retained.

## Test before deployment
1. Run `npm ci` and `npm run build` from `frontend/`.
2. Run `mvn test` from `backend/`.
3. Vendor: schedule an online interview with URL and instructions; then update its schedule.
4. Applicant: open Interviews & offers and verify all details, then open the meeting link.
5. Vendor: update a service booking status with a note; customer should see the status-history note after refresh.
6. Verify notifications in-app for interview scheduling; verify service notifications according to the configured notification implementation.

Build/test execution was not completed during preparation; do not deploy until these checks pass.
