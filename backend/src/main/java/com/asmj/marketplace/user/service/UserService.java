package com.asmj.marketplace.user.service;

import com.asmj.marketplace.user.dto.UserDtos;
import com.asmj.marketplace.user.model.User;
import com.asmj.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public User me(String email){return users.findByEmailIgnoreCase(email).orElseThrow(()->new IllegalArgumentException("User not found"));}
    public List<User> all(){return users.findAll();}
    public User updateProfile(String email, UserDtos.UpdateProfileRequest r){
        User u=me(email);
        users.findByMobile(r.mobile()).ifPresent(existing->{if(!existing.getId().equals(u.getId()))throw new IllegalArgumentException("Mobile number is already registered");});
        u.setName(r.name().trim());u.setMobile(r.mobile().trim());if(r.profileImage()!=null&&!r.profileImage().isBlank())u.setProfileImage(r.profileImage().trim());u.setUpdatedAt(Instant.now());return users.save(u);
    }
    public User updateProfileImage(String email,String imageUrl){User u=me(email);u.setProfileImage(imageUrl);u.setUpdatedAt(Instant.now());return users.save(u);}
    public void changePassword(String email, UserDtos.ChangePasswordRequest r){
        User u=me(email);
        if(!passwordEncoder.matches(r.currentPassword(),u.getPassword()))throw new IllegalArgumentException("Current password is incorrect");
        if(passwordEncoder.matches(r.newPassword(),u.getPassword()))throw new IllegalArgumentException("New password must be different from current password");
        u.setPassword(passwordEncoder.encode(r.newPassword()));u.setUpdatedAt(Instant.now());users.save(u);
    }
    public User setStatus(String id,User.Status status){User u=users.findById(id).orElseThrow(()->new IllegalArgumentException("User not found"));u.setStatus(status);u.setUpdatedAt(Instant.now());return users.save(u);}
    public User addRole(String id,User.Role role){User u=users.findById(id).orElseThrow(()->new IllegalArgumentException("User not found"));u.getRoles().add(role);u.setUpdatedAt(Instant.now());return users.save(u);}
    public User removeRole(String id,User.Role role){User u=users.findById(id).orElseThrow(()->new IllegalArgumentException("User not found"));if(role==User.Role.ADMIN)throw new IllegalArgumentException("ADMIN role cannot be removed through this API");u.getRoles().remove(role);u.setUpdatedAt(Instant.now());return users.save(u);}
}
