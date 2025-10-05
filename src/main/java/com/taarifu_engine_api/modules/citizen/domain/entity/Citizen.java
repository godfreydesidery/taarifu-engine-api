package com.taarifu_engine_api.modules.citizen.domain.entity;

import com.taarifu_engine_api.modules.profile.domain.entity.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "citizens",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"profile_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"profile"})
@ToString(exclude = {"profile"})
public class Citizen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;
    
    @Column(name = "date_of_citizenship", nullable = false)
    private LocalDateTime dateOfCitizenship;
    
    @Column(name = "citizenship_type")
    private String citizenshipType;
    
    @Column(name = "citizenship_status")
    private String citizenshipStatus;
}
