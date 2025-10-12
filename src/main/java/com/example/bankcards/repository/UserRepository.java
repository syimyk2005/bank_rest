package org.example.boxy.auth_service.repository;

import org.example.boxy.auth_service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    void deleteById(Long id);

    Optional<User> findByEmail(String email);
}