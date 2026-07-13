package com.fooddelivery.service.impl;

import com.fooddelivery.service.UserService;
import com.fooddelivery.repository.UserRepository;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * NOT cached – registration is a one-time write; caching it would risk
     * serving stale existence-check state and has no read-performance benefit.
     */
    @Override
    @Transactional
    public User register(User user) {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new BusinessException("Email and password are required");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email already registered: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * NOT cached – authentication involves credential verification and must
     * always hit the database to detect deactivation / password changes.
     */
    @Override
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid email or password"));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }
        if (!user.isActive()) {
            throw new BusinessException("Account is deactivated");
        }
        user.setPassword(null);
        return user;
    }

    /**
     * Cached by user ID. Cache key is the numeric ID for maximum cache-hit
     * efficiency (ID lookups dominate in order-placement and profile flows).
     */
    @Override
    @Cacheable(value = "users", key = "#id")
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Cached by email. Used by Spring Security's UserDetailsService on every
     * authenticated request; caching eliminates a DB round-trip per call.
     */
    @Override
    @Cacheable(value = "users", key = "#email")
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Cached as a stable admin list. Evicted when any user is updated or deleted.
     */
    @Override
    @Cacheable(value = "users", key = "'all'")
    public List<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * Cached per role. Useful for role-based dashboards (e.g. listing all DELIVERY_PARTNER users).
     */
    @Override
    @Cacheable(value = "users", key = "#role.name()")
    public List<User> getByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * @CachePut keeps the per-ID and per-email cache entries fresh immediately
     * after an update, so the very next read serves the new data without
     * a DB hit. The stale "all" and role-based lists are evicted so they
     * will be recomputed on next access.
     */
    @Override
    @Transactional
    @Caching(
        put = {
            @CachePut(value = "users", key = "#user.id")
        },
        evict = {
            @CacheEvict(value = "users", key = "#user.email"),
            @CacheEvict(value = "users", key = "'all'"),
            @CacheEvict(value = "users", key = "#user.role.name()")
        }
    )
    public User update(User user) {
        User existingUser = getById(user.getId());
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        existingUser.setActive(user.isActive());
        return userRepository.save(existingUser);
    }

    /**
     * NOT cached – password operations must never be served from cache.
     * The password hash in cache could become dangerously stale.
     */
    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Old password is incorrect");
        }
        userRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    /**
     * Evicts the user's individual cache entries so the updated active-state
     * is reflected on the next read.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "users", key = "#userId"),
        @CacheEvict(value = "users", key = "'all'")
    })
    public void setActive(Long userId, boolean active) {
        getById(userId);
        userRepository.setActive(userId, active);
    }

    /**
     * Evicts all user-related cache entries on deletion to prevent stale data.
     */
    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "users", key = "#id"),
        @CacheEvict(value = "users", key = "'all'"),
        @CacheEvict(value = "users", allEntries = true)
    })
    public void delete(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }
}
