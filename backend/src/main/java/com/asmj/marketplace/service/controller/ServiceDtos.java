package com.asmj.marketplace.service.controller;
import java.time.*;import java.util.*;
public class ServiceDtos{
 public record PackageRequest(String id,String name,String description,double price,int durationMinutes,boolean active){}
 public record ServiceRequest(String name,String description,String categoryId,String serviceMode,List<String> images,List<String> serviceAreas,List<PackageRequest> packages,int slotDurationMinutes,int bufferMinutes,int maxBookingsPerSlot,double taxRate,double commissionRate,int freeCancellationHours,double cancellationFeePercent,boolean featured){}
 public record ScheduleRequest(String serviceId,DayOfWeek dayOfWeek,LocalTime startTime,LocalTime endTime,boolean active){}
 public record BookingRequest(String serviceId,String packageId,LocalDate serviceDate,LocalTime startTime,String customerName,String customerMobile,String address,String city,String state,String pincode,String landmark,String notes,String discountCode){}
 public record StatusRequest(String status,String note){}
 public record RescheduleRequest(LocalDate serviceDate,LocalTime startTime,String note){}
 public record CompletionRequest(String text,String proofUrl){}
}
