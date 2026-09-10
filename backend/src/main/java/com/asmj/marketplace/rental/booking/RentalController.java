package com.asmj.marketplace.rental.booking;
import com.asmj.marketplace.common.response.ApiResponse;
import com.asmj.marketplace.rental.fare.*;
import com.asmj.marketplace.rental.location.*;
import com.asmj.marketplace.rental.route.*;
import com.asmj.marketplace.rental.vehicle.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/rentals") @RequiredArgsConstructor
public class RentalController {
 private final RentalService s;
 @GetMapping("/locations") public ApiResponse<List<LocationMaster>> locations(){return ApiResponse.ok("Rental locations",s.locations());}
 @GetMapping("/routes") public ApiResponse<List<RentalRoute>> routes(){return ApiResponse.ok("Rental routes",s.routes());}
 @PostMapping("/quote") public ApiResponse<Map<String,Object>> quote(@RequestBody RentalDtos.QuoteRequest r){return ApiResponse.ok("Rental quote",s.quote(r));}
 @GetMapping("/public/vehicles") public ApiResponse<List<Vehicle>> publicVehicles(@RequestParam(required=false) String q,@RequestParam(required=false) String vehicleType){return ApiResponse.ok("Available rental vehicles",s.publicVehicles(q,vehicleType));}
 @PostMapping("/bookings") @PreAuthorize("hasAnyRole('USER','VENDOR','ADMIN')") public ApiResponse<RentalBooking> create(Authentication a,@RequestBody RentalDtos.BookingRequest r){return ApiResponse.ok("Rental booking created",s.create(a.getName(),r));}
 @GetMapping("/vendor/bookings") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<List<RentalBooking>> vendorBookings(Authentication a){return ApiResponse.ok("Vendor rental bookings",s.vendorBookings(a.getName()));}
 @GetMapping("/bookings/my") @PreAuthorize("isAuthenticated()") public ApiResponse<List<RentalBooking>> mine(Authentication a){return ApiResponse.ok("My rental bookings",s.mine(a.getName()));}
 @GetMapping("/bookings/{id}") @PreAuthorize("isAuthenticated()") public ApiResponse<RentalBooking> one(Authentication a,@PathVariable String id){return ApiResponse.ok("Rental booking",s.get(a.getName(),id));}
 @PostMapping("/bookings/{id}/confirm") @PreAuthorize("isAuthenticated()") public ApiResponse<RentalBooking> confirm(Authentication a,@PathVariable String id){return ApiResponse.ok("Rental booking confirmed",s.customerConfirm(a.getName(),id));}
 @PostMapping("/bookings/{id}/cancel") @PreAuthorize("isAuthenticated()") public ApiResponse<RentalBooking> cancel(Authentication a,@PathVariable String id,@RequestParam(required=false) String reason){return ApiResponse.ok("Rental booking cancelled",s.cancelCustomer(a.getName(),id,reason));}
 @PutMapping("/driver/bookings/{id}/status") @PreAuthorize("hasRole('DRIVER')") public ApiResponse<RentalBooking> driverStatus(Authentication a,@PathVariable String id,@RequestBody RentalDtos.StatusRequest r){return ApiResponse.ok("Driver status updated",s.driverStatus(a.getName(),id,r));}
 @PutMapping("/bookings/vendor/{id}/status") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<RentalBooking> vendorStatus(Authentication a,@PathVariable String id,@RequestBody RentalDtos.StatusRequest r){return ApiResponse.ok("Rental status updated",s.vendorStatus(a.getName(),id,r));}
 @PutMapping("/bookings/vendor/{id}/assign") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<RentalBooking> assign(Authentication a,@PathVariable String id,@RequestParam String vehicleId,@RequestParam String driverId){return ApiResponse.ok("Driver assigned",s.assign(a.getName(),id,vehicleId,driverId));}
 @GetMapping("/driver/trips") @PreAuthorize("hasRole('DRIVER')") public ApiResponse<List<RentalBooking>> driverTrips(Authentication a){return ApiResponse.ok("Driver trips",s.driverTrips(a.getName()));}
 @PostMapping("/driver/bookings/{id}/start") @PreAuthorize("hasRole('DRIVER')") public ApiResponse<RentalBooking> start(Authentication a,@PathVariable String id,@RequestBody RentalDtos.KmRequest r){return ApiResponse.ok("Journey started",s.start(a.getName(),id,r));}
 @PostMapping("/driver/bookings/{id}/outbound-end") @PreAuthorize("hasRole('DRIVER')") public ApiResponse<RentalBooking> outboundEnd(Authentication a,@PathVariable String id,@RequestBody RentalDtos.KmRequest r){return ApiResponse.ok("Outbound journey ended",s.outboundEnd(a.getName(),id,r));}
 @PostMapping("/driver/bookings/{id}/return-start") @PreAuthorize("hasRole('DRIVER')") public ApiResponse<RentalBooking> returnStart(Authentication a,@PathVariable String id,@RequestBody RentalDtos.KmRequest r){return ApiResponse.ok("Return journey started",s.returnStart(a.getName(),id,r));}
 @PostMapping("/driver/bookings/{id}/end") @PreAuthorize("hasRole('DRIVER')") public ApiResponse<RentalBooking> end(Authentication a,@PathVariable String id,@RequestBody RentalDtos.KmRequest r){return ApiResponse.ok("Journey completed and fare finalized",s.end(a.getName(),id,r));}
 @GetMapping("/vendor/vehicles") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<List<Vehicle>> vehicles(Authentication a){return ApiResponse.ok("Vendor vehicles",s.vendorVehicles(a.getName()));}
 @PostMapping("/vendor/vehicles") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<Vehicle> saveVehicle(Authentication a,@RequestBody Vehicle v){return ApiResponse.ok("Vehicle saved",s.saveVehicle(a.getName(),v));}
 @DeleteMapping("/vendor/vehicles/{id}") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<Void> deleteVehicle(Authentication a,@PathVariable String id){s.deleteVehicle(a.getName(),id);return ApiResponse.ok("Vehicle deleted",null);}
 @GetMapping("/vendor/drivers") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<List<Driver>> drivers(Authentication a){return ApiResponse.ok("Vendor drivers",s.vendorDrivers(a.getName()));}
 @PostMapping("/vendor/drivers") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<Driver> createDriver(Authentication a,@RequestBody RentalDtos.DriverRequest r){return ApiResponse.ok("Driver created",s.createDriver(a.getName(),r));}
 @PutMapping("/vendor/drivers/{id}") @PreAuthorize("hasRole('VENDOR')") public ApiResponse<Driver> updateDriver(Authentication a,@PathVariable String id,@RequestBody RentalDtos.DriverRequest r){return ApiResponse.ok("Driver updated",s.updateDriver(a.getName(),id,r));}
 @GetMapping("/admin/bookings") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<List<RentalBooking>> adminBookings(Authentication a){return ApiResponse.ok("All rental bookings",s.adminBookings(a.getName()));}
 @GetMapping("/admin/locations") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<List<LocationMaster>> adminLocations(Authentication a){return ApiResponse.ok("Rental location master",s.adminLocations(a.getName()));}
 @PostMapping("/admin/locations") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<LocationMaster> saveLocation(Authentication a,@RequestBody LocationMaster x){return ApiResponse.ok("Location saved",s.saveLocation(a.getName(),x));}
 @DeleteMapping("/admin/locations/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<Void> deleteLocation(Authentication a,@PathVariable String id){s.deleteLocation(a.getName(),id);return ApiResponse.ok("Location deleted",null);}
 @GetMapping("/admin/routes") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<List<RentalRoute>> adminRoutes(Authentication a){return ApiResponse.ok("Rental route master",s.adminRoutes(a.getName()));}
 @PostMapping("/admin/routes") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<RentalRoute> saveRoute(Authentication a,@RequestBody RentalRoute x){return ApiResponse.ok("Route saved",s.saveRoute(a.getName(),x));}
 @DeleteMapping("/admin/routes/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<Void> deleteRoute(Authentication a,@PathVariable String id){s.deleteRoute(a.getName(),id);return ApiResponse.ok("Route deleted",null);}
 @GetMapping("/admin/fares") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<List<FareRule>> adminFares(Authentication a){return ApiResponse.ok("Fare rules",s.adminFares(a.getName()));}
 @PostMapping("/admin/fares") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<FareRule> saveFare(Authentication a,@RequestBody FareRule x){return ApiResponse.ok("Fare rule saved",s.saveFare(a.getName(),x));}
 @DeleteMapping("/admin/fares/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<Void> deleteFare(Authentication a,@PathVariable String id){s.deleteFare(a.getName(),id);return ApiResponse.ok("Fare rule deleted",null);}
 @GetMapping("/admin/discounts") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<List<DiscountRule>> adminDiscounts(Authentication a){return ApiResponse.ok("Discount rules",s.adminDiscounts(a.getName()));}
 @PostMapping("/admin/discounts") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<DiscountRule> saveDiscount(Authentication a,@RequestBody DiscountRule x){return ApiResponse.ok("Discount rule saved",s.saveDiscount(a.getName(),x));}
 @DeleteMapping("/admin/discounts/{id}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<Void> deleteDiscount(Authentication a,@PathVariable String id){s.deleteDiscount(a.getName(),id);return ApiResponse.ok("Discount rule deleted",null);}
 @GetMapping("/admin/audits/{entityId}") @PreAuthorize("hasRole('ADMIN')") public ApiResponse<List<com.asmj.marketplace.rental.audit.RentalAudit>> audits(Authentication a,@PathVariable String entityId){return ApiResponse.ok("Rental audit",s.audits(a.getName(),entityId));}
}
