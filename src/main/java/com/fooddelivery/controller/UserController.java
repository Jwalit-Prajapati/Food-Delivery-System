package com.fooddelivery.controller;

import com.fooddelivery.dto.request.UserRegistrationRequest;
import com.fooddelivery.dto.request.UpdateUserRequest;
import com.fooddelivery.dto.response.UserResponse;
import com.fooddelivery.mapper.UserMapper;
import com.fooddelivery.model.User;
import com.fooddelivery.service.UserService;
import com.fooddelivery.security.JwtAuthResponse;
import com.fooddelivery.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@jakarta.validation.Valid @RequestBody UserRegistrationRequest request) {
        User user = userMapper.toEntity(request);
        User created = userService.register(user);
        return new ResponseEntity<>(userMapper.toResponse(created), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@jakarta.validation.Valid @RequestBody com.fooddelivery.dto.request.LoginRequest creds) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getEmail(),
                        creds.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        User user = userService.getByEmail(creds.getEmail());

        return ResponseEntity.ok(JwtAuthResponse.builder().token(jwt).userId(user.getId()).email(user.getEmail()).role(user.getRole().name()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userMapper.toResponse(userService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll(@RequestParam(required = false) User.Role role) {
        List<User> users = role != null ? userService.getByRole(role) : userService.getAll();
        return ResponseEntity.ok(users.stream().map(userMapper::toResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @jakarta.validation.Valid @RequestBody UpdateUserRequest request) {
        User user = userService.getById(id);
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        return ResponseEntity.ok(userMapper.toResponse(userService.update(user)));
    }

    @PostMapping("/{id}/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@PathVariable Long id,
                                                              @RequestBody Map<String, String> body) {
        userService.changePassword(id, body.get("oldPassword"), body.get("newPassword"));
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
