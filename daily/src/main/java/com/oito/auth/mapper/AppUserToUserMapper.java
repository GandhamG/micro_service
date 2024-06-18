package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.data.User;

@Mapper(uses = { AuditMapper.class,
		UserTypeTOMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppUserToUserMapper extends BaseMapper<AppUserTO, User> {

	void mapVO(User entity, @MappingTarget AppUserTO dto);

	void mapEntity(AppUserTO dto, @MappingTarget User entity);
}
