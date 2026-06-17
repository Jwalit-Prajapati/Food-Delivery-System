package com.fooddelivery.mapper;

import com.fooddelivery.dto.request.UserRegistrationRequest;
import com.fooddelivery.dto.response.UserResponse;
import com.fooddelivery.model.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-17T00:37:30+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserRegistrationRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        if ( request.getRole() != null ) {
            user.role( request.getRole() );
        }
        else {
            user.role( User.Role.CUSTOMER );
        }
        user.name( request.getName() );
        user.email( request.getEmail() );
        user.password( request.getPassword() );
        user.phone( request.getPhone() );

        user.active( true );

        return user.build();
    }

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.id( user.getId() );
        userResponse.name( user.getName() );
        userResponse.email( user.getEmail() );
        userResponse.phone( user.getPhone() );
        userResponse.role( user.getRole() );
        userResponse.active( user.isActive() );

        return userResponse.build();
    }
}
