package com.fooddelivery.dao;

import com.fooddelivery.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(User.Role role);

    int countByRole(User.Role role);

    @Modifying
    @Query("UPDATE User u SET u.password = :hashedPassword WHERE u.id = :id")
    int updatePassword(Long id, String hashedPassword);

    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    int setActive(Long id, boolean active);
}
