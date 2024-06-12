package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.SocialLoginRequest;
import com.oito.auth.social.user.SocialUserProfile;

@Mapper(uses = {}, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class SocialProfileLoginRequestMapper {

	@Mapping(target = "useremail", source = "email")
	@Mapping(target = "fullName", source = "name")
	public abstract void mapEntity(SocialUserProfile dto, @MappingTarget SocialLoginRequest entity);
}
