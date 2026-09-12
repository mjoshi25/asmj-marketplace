package com.asmj.marketplace.printout.dto;
import java.math.BigDecimal; import java.util.*;
public final class PrintoutDtos { private PrintoutDtos(){}
 public record ServiceRequest(String serviceName,String description,Boolean pickupAvailable,Boolean homeDeliveryAvailable,BigDecimal minimumHomeDeliveryAmount,BigDecimal homeDeliveryChargePercent,BigDecimal bwSingleRate,BigDecimal bwDoubleRate,BigDecimal colorSingleRate,BigDecimal colorDoubleRate,BigDecimal a3AdditionalCharge,BigDecimal bindingCharge,BigDecimal laminationCharge){}
 public record FileRequest(String fileName,String url,String publicId){}
 public record RequestCreate(String printoutServiceId,String documentName,List<FileRequest> files,Integer copies,Integer pageCount,String printMode,String paperSize,String printSide,String bindingType,Boolean laminationRequired,String fulfilmentType,String deliveryAddress,String deliveryLandmark,String deliveryCity,String deliveryState,String deliveryPincode,String contactNumber,String specialInstructions){}
 public record StatusRequest(String status,String vendorRemarks,BigDecimal quotedAmount){}
 public record PaymentProofRequest(String paymentProofUrl,String paymentProofPublicId,String paymentReference,BigDecimal amount){}
}
