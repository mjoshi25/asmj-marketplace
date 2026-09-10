package com.asmj.marketplace.application.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.*;
import com.asmj.marketplace.common.model.StatusHistory;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@Document("job_applications")
public class JobApplication {
    @Id private String id;
    @Indexed private String postId;
    @Indexed private String userId;
    @Indexed private String vendorId;
    private String resumeUrl;
    private String resumePublicId;
    private String coverLetter;
    private String status;
    private Map<String,String> screeningAnswers;
    private String source;
    private Double expectedCtc;
    private String recruiterNotes;
    @Builder.Default private List<StatusHistory> statusHistory = new ArrayList<>();
    private Instant interviewDate;
    private String interviewNotes;
    private Instant createdAt, updatedAt;
    @Transient private String listingTitle;
    @Transient private String listingType;
    @Transient private String listingImage;
    @Transient private String applicantName;
    @Transient private String applicantEmail;
    @Transient private String applicantMobile;
}
