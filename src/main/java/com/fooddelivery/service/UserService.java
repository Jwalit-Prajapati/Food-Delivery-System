package com.fooddelivery.service;

import com.fooddelivery.exception.BusinessException;
import com.fooddelivery.exception.ResourceNotFoundException;
import com.fooddelivery.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface UserService {
    User register(User user);
    User login(String email, String rawPassword);
    User getById(Long id);
    User getByEmail(String email);
    List<User> getAll();
    List<User> getByRole(User.Role role);
    User update(User user);
    void changePassword(Long userId, String oldPassword, String newPassword);
    void setActive(Long userId, boolean active);
    void delete(Long id);
}
