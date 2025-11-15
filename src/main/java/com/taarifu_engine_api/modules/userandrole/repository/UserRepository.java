package com.taarifu_engine_api.modules.userandrole.repository;

import com.taarifu_engine_api.modules.userandrole.domain.entity.User;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserStatus;
import com.taarifu_engine_api.modules.userandrole.domain.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUid(String uid);
    
    Optional<User> findByUidAndUserType(String uid, UserType userType);
    
    Optional<User> findByPasswordResetToken(String passwordResetToken);
    
    // Soft delete query methods
    Optional<User> findByUidAndDeletedFalse(String uid);
    
    Optional<User> findByUsernameAndDeletedFalse(String username);
    
    Optional<User> findByEmailAndDeletedFalse(String email);
    
    @Query("SELECT u FROM User u WHERE u.uid = :uid AND (u.deleted = false OR u.deleted IS NULL)")
    Optional<User> findByUidExcludingDeleted(@Param("uid") String uid);
    
    @Query("SELECT u FROM User u WHERE u.username = :username AND (u.deleted = false OR u.deleted IS NULL)")
    Optional<User> findByUsernameExcludingDeleted(@Param("username") String username);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND (u.deleted = false OR u.deleted IS NULL)")
    Optional<User> findByEmailExcludingDeleted(@Param("email") String email);
    
    // Email verification query methods
    Optional<User> findByEmailVerificationToken(String emailVerificationToken);
    
    // Account lockout query methods
    @Query("SELECT u FROM User u WHERE u.accountLockedUntil > :now")
    Page<User> findByAccountLockedUntilAfter(@Param("now") LocalDateTime now, Pageable pageable);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUid(String uid);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType AND (u.deleted = false OR u.deleted IS NULL)")
    long countByUserTypeExcludingDeleted(@Param("userType") UserType userType);
    
    long countByUserType(UserType userType);
    
    @Query("SELECT u FROM User u WHERE u.userType = :userType AND (u.deleted = false OR u.deleted IS NULL)")
    Page<User> findByUserTypeExcludingDeleted(@Param("userType") UserType userType, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.userType = :userType AND u.status = :status AND (u.deleted = false OR u.deleted IS NULL)")
    Page<User> findByUserTypeAndStatusExcludingDeleted(
        @Param("userType") UserType userType, 
        @Param("status") UserStatus status, 
        Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.userType = :userType AND (u.deleted = false OR u.deleted IS NULL) AND " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "(u.phoneNumber IS NOT NULL AND LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))))")
    Page<User> searchAdminUsersByQuery(
        @Param("userType") UserType userType,
        @Param("searchTerm") String searchTerm,
        Pageable pageable);
    
    Page<User> findByUserType(UserType userType, Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.deleted = false OR u.deleted IS NULL")
    long countExcludingDeleted();
    
    long count();
}
