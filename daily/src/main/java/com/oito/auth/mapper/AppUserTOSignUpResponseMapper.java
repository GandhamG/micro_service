package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.common.to.SignUpResponse;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppUserTOSignUpResponseMapper extends BaseMapper<SignUpResponse, AppUserTO> {

	@Override
	@Mapping(target = "status", ignore = true)
	SignUpResponse toVO(AppUserTO appUserTo);

	@Override
	@Mapping(target = "status", ignore = true)
	AppUserTO toEntity(SignUpResponse response);
}
