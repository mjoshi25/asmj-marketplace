package com.asmj.marketplace.job.model;
import lombok.*; import org.springframework.data.annotation.*; import org.springframework.data.mongodb.core.index.Indexed; import org.springframework.data.mongodb.core.mapping.Document; import java.time.Instant; import java.util.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor @Document("job_listings")
public class JobListing { @Id private String id; @Indexed(unique=true) private String postId; @Indexed private String vendorId; private String employmentType,workMode,experienceLevel,department,education,locationText; private Double minSalary,maxSalary; private String salaryPeriod,currency; private boolean salaryVisible; private Integer openings; private Instant applicationDeadline; private List<String> skills,benefits; private List<ScreeningQuestion> screeningQuestions; private boolean resumeRequired; private String applicationInstructions; private Instant createdAt,updatedAt;
 @Data @Builder @NoArgsConstructor @AllArgsConstructor public static class ScreeningQuestion { private String id,question,type; private boolean required; private List<String> options; }
}
