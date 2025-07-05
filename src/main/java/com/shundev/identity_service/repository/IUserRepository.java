package com.shundev.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shundev.identity_service.entity.User;

@Repository
public interface IUserRepository extends JpaRepository<User, String>{
    boolean existsByUsername(String userName); 
}
