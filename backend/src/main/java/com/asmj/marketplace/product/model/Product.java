package com.asmj.marketplace.product.model;
import lombok.*;import org.springframework.data.annotation.Id;import org.springframework.data.mongodb.core.index.Indexed;import org.springframework.data.mongodb.core.mapping.Document;import java.time.Instant;import java.util.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("products") public class Product{
 @Id private String id; @Indexed private String vendorId; @Indexed private String categoryId; @Indexed private String name; @Indexed(unique=true) private String sku; private String brand,description,unit; private double price,mrp; private double taxRate; private boolean active,approved,featured; private List<String> images; private List<Variant> variants; private Instant createdAt,updatedAt;
 @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class Variant{private String id,name,sku;private Map<String,String> attributes;private double price,mrp;private int stock;private int lowStockThreshold;private boolean active;}
}
