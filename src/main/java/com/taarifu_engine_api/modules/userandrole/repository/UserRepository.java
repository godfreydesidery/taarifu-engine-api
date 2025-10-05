package com.taarifu_engine_api.modules.userandrole.repository;

import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUid(String uid);
    
    Optional<User> findByUidAndUserType(String uid, UserType userType);
    
    Optional<User> findByPasswordResetToken(String passwordResetToken);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUid(String uid);
    
    long countByUserType(UserType userType);
    
    Page<User> findByUserType(UserType userType, Pageable pageable);
    
    long count();
}
