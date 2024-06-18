package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.SocialTokenSignupRequest;
import com.oito.auth.common.to.UserSignUpRequest;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SocialTokenSignupRequestMapper extends BaseMapper<SocialTokenSignupRequest, UserSignUpRequest> {

}
