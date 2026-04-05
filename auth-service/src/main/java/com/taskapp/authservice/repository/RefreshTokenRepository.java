package com.taskapp.authservice.repository;

import com.taskapp.authservice.entity.RefreshToken;
import com.taskapp.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    
    @Modifying
    int deleteByUser(User user);
}
