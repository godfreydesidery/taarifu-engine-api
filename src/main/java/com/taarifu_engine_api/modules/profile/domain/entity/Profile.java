package com.taarifu_engine_api.modules.profile.domain.entity;

import com.taarifu_engine_api.modules.profile.domain.enums.IdType;
import com.taarifu_engine_api.modules.profile.domain.enums.ProfileType;
import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "profiles", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"id_type", "id_number"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
public class Profile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "uid", unique = true, nullable = false)
    private String uid;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_type", nullable = false)
    private ProfileType profileType;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "display_name")
    private String displayName;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;
    
    @Column(name = "gender")
    private String gender;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "id_type")
    private IdType idType;
    
    @Column(name = "id_number")
    private String idNumber;
    
    @Column(name = "registration_number")
    private String registrationNumber;
    
    @Column(name = "website")
    private String website;
    
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "profile_picture_url")
    private String profilePictureUrl;
    
    @Column(name = "bio")
    private String bio;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "whatsapp_number")
    private String whatsappNumber;
    
    @Column(name = "social_media_links", columnDefinition = "TEXT")
    private String socialMediaLinks;
    
    @Column(name = "civic_interests", columnDefinition = "TEXT")
    private String civicInterests;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
