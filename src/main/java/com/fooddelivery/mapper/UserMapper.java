package com.fooddelivery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.fooddelivery.dto.request.UserRegistrationRequest;
import com.fooddelivery.dto.response.UserResponse;
import com.fooddelivery.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", defaultValue = "CUSTOMER")
    User toEntity(UserRegistrationRequest request);

    UserResponse toResponse(User user);
}
