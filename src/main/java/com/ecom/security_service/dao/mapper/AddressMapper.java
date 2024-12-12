package com.ecom.security_service.dao.mapper;

import com.ecom.security_service.dao.entity.Address;
import com.ecom.security_service.model.AddressModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressModel entityToModel(Address entity);
}
