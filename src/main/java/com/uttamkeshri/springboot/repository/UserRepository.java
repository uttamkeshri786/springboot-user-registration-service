package com.uttamkeshri.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uttamkeshri.springboot.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	
	Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    
    Optional<User> findByMobile(String mobile);

    List<User> findByIdIn(List<Long> userIds);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByMobile(String mobile);
    
    @Query(value = "SELECT * FROM users arp where arp.username=?1  ",nativeQuery = true)
    List<User> findOneByUsername(String username);

    @Query(value = "SELECT * FROM users arp where arp.login_attempt_count=?1  ",nativeQuery = true)
    List<User> findByLoginAttempt(int loginAttemptExceed);

    @Query(value = "SELECT * FROM users arp where arp.status=?1  ",nativeQuery = true)
    List<User> findByPendingStatus(int loginPendingStatus);
}