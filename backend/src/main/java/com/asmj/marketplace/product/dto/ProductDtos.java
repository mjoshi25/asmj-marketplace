package com.asmj.marketplace.product.dto;import com.asmj.marketplace.product.model.Order;import java.util.*;
public class ProductDtos{
 public record ProductRequest(String name,String sku,String categoryId,String brand,String description,String unit,double price,double mrp,double taxRate,List<String> images,List<Map<String,Object>> variants,Integer stock,Integer lowStockThreshold,boolean featured){}
 public record CartRequest(String productId,String variantId,int quantity){}
 public record AddressRequest(String name,String mobile,String line1,String line2,String area,String city,String state,String pincode,String landmark){}
 public record CheckoutRequest(AddressRequest address,String couponCode){}
 public record StatusRequest(Order.Status status,String note){}
 public record CouponRequest(String code,String name,String type,double value,double minOrder,double maxDiscount,java.time.Instant validFrom,java.time.Instant validTo,int usageLimit,boolean active,Set<String> vendorIds){}
}
