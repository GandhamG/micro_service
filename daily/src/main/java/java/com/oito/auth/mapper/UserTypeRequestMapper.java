package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.UserTypeRequest;
import com.oito.auth.data.UserType;

@Mapper(uses = {
		AuditMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface UserTypeRequestMapper extends BaseMapper<UserTypeRequest, UserType> {

	void mapEntity(UserTypeRequest dto, @MappingTarget UserType entity);

	void mapVO(UserType entity, @MappingTarget UserTypeRequest dto);

}
