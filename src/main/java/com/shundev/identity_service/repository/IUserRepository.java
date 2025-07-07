package com.shundev.identity_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shundev.identity_service.entity.User;

@Repository
public interface IUserRepository extends JpaRepository<User, String>{
    boolean existsByUsername(String username); 
    Optional<User> findByUsername(String username);
}
