package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.social.user.SocialUserProfile;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SocialProfileSignupRequestMapper {

	@Mapping(target = "useremail", source = "email")
	@Mapping(target = "fullName", source = "name")
	void mapEntity(SocialUserProfile dto, @MappingTarget UserSignUpRequest entity);
}
