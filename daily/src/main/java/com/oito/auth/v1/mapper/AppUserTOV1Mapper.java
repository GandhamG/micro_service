package com.oito.auth.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.UserTokenTO;
import com.oito.auth.mapper.AuditMapper;
import com.oito.auth.v1.common.to.AppUserTOV1;

@Mapper(uses = {
		AuditMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppUserTOV1Mapper {
	@Mapping(source = "userTokenTO.token", target = "refreshToken")
	AppUserTOV1 appAndTokenTO(AppUserTO appUserTO, UserTokenTO userTokenTO);

	AppUserTOV1 toEntity(AppUserTO appUserTO);
}
