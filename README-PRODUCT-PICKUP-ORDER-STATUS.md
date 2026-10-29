# Product pickup, delivery and order/payment status updates

## What's changed
- Product checkout now offers Home Delivery or Pickup from Shop.
- Delivery requires the full delivery address; pickup does not require a delivery address.
- Order records save `fulfillmentType` (`DELIVERY` or `PICKUP`). Existing orders without the field display as `DELIVERY`.
- Pickup order lifecycle supports `PACKED -> READY_FOR_PICKUP -> COLLECTED`.
- Vendor dashboard has a Product Orders page at `/vendor/product-orders` to update fulfilment status and mark payment proof as submitted.
- Admin Product Administration > Orders allows admin to update order status and payment/refund status.
- Payment statuses supported: `PENDING`, `PROOF_SUBMITTED`, `PAID`, `FAILED`, `REFUND_REQUESTED`, `REFUND_APPROVED`, `REFUNDED`.
- Only admin can mark payment received (`PAID`) or approve/complete a refund. Vendor updates are restricted to `PENDING` / `PROOF_SUBMITTED`.
- Customer My Orders shows fulfilment and payment statuses.

## Test before production
1. Install frontend dependencies in `frontend` with `npm ci`, then run `npm run build`.
2. Build backend with Java 17 and Maven: `mvn test` / `mvn package` from `backend`.
3. Place a product order using Home Delivery; confirm address is required and the order shows DELIVERY.
4. Place another order using Pickup; confirm address is not required and the order shows PICKUP.
5. Vendor should progress a pickup order through CONFIRMED, PROCESSING, PACKED, READY_FOR_PICKUP, COLLECTED.
6. Vendor can submit payment proof status but cannot mark PAID or REFUNDED.
7. Admin can change order status and verify payment/refund status. Confirm the customer order page reflects changes.
8. Confirm cancellation and stock restoration in a test database before production deployment.

Payment gateway is not added by this change; `PAID` means an admin has verified the payment received through the existing manual workflow.
