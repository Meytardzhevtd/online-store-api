package com.online.store.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.online.store.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findById(Long id);
}
