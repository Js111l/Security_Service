package com.ecom.security_service.dao.mapper;

import com.ecom.security_service.controller.UserModel;
import com.ecom.security_service.dao.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User modelToEntity(UserModel userModel);

    UserModel entityToModel(User user);
}
