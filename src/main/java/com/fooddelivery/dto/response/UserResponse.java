package com.fooddelivery.dto.response;

import java.io.Serializable;

import com.fooddelivery.model.User.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private boolean active;
}
