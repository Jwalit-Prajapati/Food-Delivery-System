package com.fooddelivery.service.impl;

import com.fooddelivery.service.*;

import com.fooddelivery.service.UserService;

import com.fooddelivery.dao.UserRepository;
import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    @Transactional
    public User update(User user) {
        User existingUser = getById(user.getId());
        existingUser.setName(user.getName());
        existingUser.setPhone(user.getPhone());
        existingUser.setRole(user.getRole());
        existingUser.setActive(user.isActive());
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Old password is incorrect");
        }
        userRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
    }

    @Override
    @Transactional
    public void setActive(Long userId, boolean active) {
        getById(userId);
        userRepository.setActive(userId, active);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getById(id);
        userRepository.deleteById(id);
    }
}
