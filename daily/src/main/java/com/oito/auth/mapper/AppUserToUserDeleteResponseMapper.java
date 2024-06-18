package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.UserDeleteResponse;

@Mapper(uses = { AuditMapper.class,
		UserTypeTOMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppUserToUserDeleteResponseMapper extends BaseMapper<AppUserTO, UserDeleteResponse> {

	@Override
	@Mapping(target = "status", ignore = true)
	AppUserTO toVO(UserDeleteResponse entity);

	@Override
	@Mapping(target = "status", ignore = true)
	UserDeleteResponse toEntity(AppUserTO vo);
}
