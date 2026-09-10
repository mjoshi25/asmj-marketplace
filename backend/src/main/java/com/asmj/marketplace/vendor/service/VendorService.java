package com.asmj.marketplace.vendor.service;

import com.asmj.marketplace.vendor.model.Vendor;
import com.asmj.marketplace.vendor.repository.VendorRepository;
import com.asmj.marketplace.user.repository.UserRepository;
import com.asmj.marketplace.user.model.User;
import com.asmj.marketplace.enquiry.repository.EnquiryRepository;
import com.asmj.marketplace.enquiry.model.Enquiry;
import com.asmj.marketplace.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorService {
    private final VendorRepository repo; private final UserRepository users; private final EnquiryRepository enquiries; private final PostRepository posts;

    public Vendor create(String email, Vendor v) { User u=users.findByEmailIgnoreCase(email).orElseThrow(); if(repo.findByUserId(u.getId()).isPresent()) throw new IllegalArgumentException("Vendor profile already exists"); u.getRoles().add(User.Role.VENDOR); users.save(u); v.setUserId(u.getId()); v.setVerificationStatus(Vendor.VerificationStatus.PENDING); v.setCreatedAt(Instant.now()); v.setUpdatedAt(Instant.now()); return repo.save(v); }
    public Vendor update(String email, Vendor incoming) { Vendor v=me(email); v.setBusinessName(incoming.getBusinessName()); v.setBusinessType(incoming.getBusinessType()); v.setDescription(incoming.getDescription()); v.setPhone(incoming.getPhone()); v.setEmail(incoming.getEmail()); v.setLogo(incoming.getLogo()); v.setAddress(incoming.getAddress()); v.setDocuments(incoming.getDocuments()); v.setUpdatedAt(Instant.now()); return repo.save(v); }

    public List<Enquiry> enquiries(String email) {
        User u=users.findByEmailIgnoreCase(email).orElseThrow();
        Vendor v=repo.findByUserId(u.getId()).orElseThrow();
        List<Enquiry> raw=enquiries.findByVendorIdOrderByCreatedAtDesc(v.getId());
        Map<String,Enquiry> unique=raw.stream().collect(Collectors.toMap(Enquiry::getId,Function.identity(),(a,b)->a,LinkedHashMap::new));
        for(Enquiry e:unique.values()) {
            posts.findById(e.getPostId()).ifPresent(p->{e.setListingTitle(p.getTitle());e.setListingType(p.getType());e.setListingImage(p.getImages()!=null&&!p.getImages().isEmpty()?p.getImages().get(0):null);});
            users.findById(e.getUserId()).ifPresent(c->{e.setCustomerName(c.getName());e.setCustomerEmail(c.getEmail());e.setCustomerMobile(c.getMobile());});
        }
        return new ArrayList<>(unique.values());
    }

    public Vendor me(String email){User u=users.findByEmailIgnoreCase(email).orElseThrow();return repo.findByUserId(u.getId()).orElseThrow(()->new IllegalArgumentException("Vendor profile not found"));}
    public Vendor get(String id){return repo.findById(id).orElseThrow();}
    public List<Vendor> all(){return repo.findAll();}
    public List<Vendor> pending(){return repo.findByVerificationStatus(Vendor.VerificationStatus.PENDING);}
    public Vendor setApproval(String id,boolean ok){Vendor v=get(id);v.setVerificationStatus(ok?Vendor.VerificationStatus.APPROVED:Vendor.VerificationStatus.REJECTED);return repo.save(v);}
}
