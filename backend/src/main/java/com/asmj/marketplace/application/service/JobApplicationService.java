package com.asmj.marketplace.application.service;

import com.asmj.marketplace.application.model.JobApplication;
import com.asmj.marketplace.application.repository.JobApplicationRepository;
import com.asmj.marketplace.common.model.StatusHistory;
import com.asmj.marketplace.notification.service.NotificationService;
import com.asmj.marketplace.post.model.Post;
import com.asmj.marketplace.post.repository.PostRepository;
import com.asmj.marketplace.user.repository.UserRepository;
import com.asmj.marketplace.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;

@Service @RequiredArgsConstructor
public class JobApplicationService {
    private final JobApplicationRepository applications; private final PostRepository posts; private final UserRepository users; private final VendorRepository vendors; private final NotificationService notifications;
    public JobApplication apply(String email,String postId,String resumeUrl,String resumePublicId,String coverLetter){
        var user=users.findByEmailIgnoreCase(email).orElseThrow(()->new IllegalArgumentException("User not found")); Post post=posts.findById(postId).orElseThrow(()->new IllegalArgumentException("Listing not found"));
        if(!"JOB".equalsIgnoreCase(post.getType())) throw new IllegalArgumentException("This listing does not accept job applications"); if(post.getStatus()!=Post.PostStatus.PUBLISHED) throw new IllegalArgumentException("This listing is not available for applications"); if(applications.findByPostIdAndUserId(postId,user.getId()).isPresent()) throw new IllegalArgumentException("You have already applied for this job"); if(resumeUrl==null||resumeUrl.isBlank()) throw new IllegalArgumentException("Resume is required");
        Instant now=Instant.now(); JobApplication a=applications.save(JobApplication.builder().postId(postId).userId(user.getId()).vendorId(post.getVendorId()).resumeUrl(resumeUrl).resumePublicId(resumePublicId).coverLetter(coverLetter).status("SUBMITTED").createdAt(now).updatedAt(now).statusHistory(new ArrayList<>(List.of(history("SUBMITTED",user,"Application submitted",now)))).build());
        vendors.findById(post.getVendorId()).ifPresent(v->notifications.create(v.getUserId(),"New job application",user.getName()+" applied for "+post.getTitle(),"JOB_APPLICATION")); return enrich(a);
    }
    public List<JobApplication> mine(String email){return enrich(applications.findByUserIdOrderByCreatedAtDesc(users.findByEmailIgnoreCase(email).orElseThrow().getId()));}
    public List<JobApplication> vendor(String email){var user=users.findByEmailIgnoreCase(email).orElseThrow();var vendor=vendors.findByUserId(user.getId()).orElseThrow(()->new IllegalArgumentException("Vendor profile not found"));return enrich(applications.findByVendorIdOrderByCreatedAtDesc(vendor.getId()));}
    public JobApplication one(String email,String id){var user=users.findByEmailIgnoreCase(email).orElseThrow();JobApplication a=applications.findById(id).orElseThrow(()->new IllegalArgumentException("Application not found"));var vendor=vendors.findByUserId(user.getId()).orElse(null);if(!Objects.equals(a.getUserId(),user.getId())&&(vendor==null||!Objects.equals(a.getVendorId(),vendor.getId()))&&!user.getRoles().contains(com.asmj.marketplace.user.model.User.Role.ADMIN))throw new IllegalArgumentException("You cannot view this application");return enrich(a);}
    private List<JobApplication> enrich(List<JobApplication> list){for(var a:list)enrich(a);return list;} private JobApplication enrich(JobApplication a){if(a.getStatusHistory()==null)a.setStatusHistory(new ArrayList<>());posts.findById(a.getPostId()).ifPresent(p->{a.setListingTitle(p.getTitle());a.setListingType(p.getType());a.setListingImage(p.getImages()!=null&&!p.getImages().isEmpty()?p.getImages().get(0):null);});users.findById(a.getUserId()).ifPresent(u->{a.setApplicantName(u.getName());a.setApplicantEmail(u.getEmail());a.setApplicantMobile(u.getMobile());});return a;}
    public JobApplication updateVendorStatus(String email,String id,String status,String note,Instant interviewDate,String interviewNotes){
        var user=users.findByEmailIgnoreCase(email).orElseThrow(); JobApplication a=applications.findById(id).orElseThrow(()->new IllegalArgumentException("Application not found")); var vendor=vendors.findByUserId(user.getId()).orElseThrow(()->new IllegalArgumentException("Vendor profile not found")); if(!Objects.equals(a.getVendorId(),vendor.getId()))throw new IllegalArgumentException("You cannot update this application");
        String next=status==null?a.getStatus():status.toUpperCase(); if(!allowed(a.getStatus(),next))throw new IllegalArgumentException("Cannot change application from "+a.getStatus()+" to "+next);
        if("INTERVIEW".equals(next)){if(interviewDate==null)throw new IllegalArgumentException("Interview date is required");a.setInterviewDate(interviewDate);a.setInterviewNotes(interviewNotes);} else if(interviewNotes!=null&&!interviewNotes.isBlank())a.setInterviewNotes(interviewNotes);
        Instant now=Instant.now();a.setStatus(next);a.setUpdatedAt(now);if(a.getStatusHistory()==null)a.setStatusHistory(new ArrayList<>());a.getStatusHistory().add(history(next,user,note==null?"Status updated":note,now));JobApplication saved=applications.save(a);
        posts.findById(a.getPostId()).ifPresent(p->notifications.create(a.getUserId(),"Job application updated","Your application for "+p.getTitle()+" is now "+next.toLowerCase().replace('_',' ')+".","JOB_APPLICATION_STATUS"));return enrich(saved);
    }
    private boolean allowed(String c,String n){if(c==null||c.equals(n))return true;if(c.equals("SUBMITTED"))return Set.of("UNDER_REVIEW","SHORTLISTED","REJECTED").contains(n);if(c.equals("UNDER_REVIEW"))return Set.of("SHORTLISTED","INTERVIEW","REJECTED").contains(n);if(c.equals("SHORTLISTED"))return Set.of("INTERVIEW","ACCEPTED","REJECTED").contains(n);if(c.equals("INTERVIEW"))return Set.of("OFFERED","ACCEPTED","REJECTED").contains(n);if(c.equals("OFFERED"))return Set.of("ACCEPTED","REJECTED").contains(n);return false;}
    private StatusHistory history(String status,com.asmj.marketplace.user.model.User actor,String note,Instant at){return StatusHistory.builder().status(status).actorId(actor.getId()).actorEmail(actor.getEmail()).note(note).changedAt(at).build();}
    public boolean hasApplied(String email,String postId){var user=users.findByEmailIgnoreCase(email).orElseThrow();return applications.findByPostIdAndUserId(postId,user.getId()).isPresent();}
}
