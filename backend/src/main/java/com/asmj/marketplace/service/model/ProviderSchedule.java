package com.asmj.marketplace.service.model;
import lombok.*;import org.springframework.data.annotation.Id;import org.springframework.data.mongodb.core.index.Indexed;import org.springframework.data.mongodb.core.mapping.Document;import java.time.*;import java.util.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("provider_schedules") public class ProviderSchedule{@Id String id;@Indexed String vendorId;@Indexed String serviceId;DayOfWeek dayOfWeek;LocalTime startTime,endTime;boolean active;}
