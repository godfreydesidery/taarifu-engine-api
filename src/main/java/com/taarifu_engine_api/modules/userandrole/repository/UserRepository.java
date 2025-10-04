package com.taarifu_engine_api.modules.userandrole.repository;

import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUid(String uid);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUid(String uid);
    
    long countByUserType(UserType userType);
    
    long count();
}
