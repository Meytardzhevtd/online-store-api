package com.online.store.mapper;

import org.mapstruct.Mapper;

import com.online.store.dto.user.UserResponse;
import com.online.store.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);

}
