package com.FRM.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByEmailAndIsActiveTrue(String email);

    Optional<User> findByUserIdAndIsActiveTrue(Long userId);
}
