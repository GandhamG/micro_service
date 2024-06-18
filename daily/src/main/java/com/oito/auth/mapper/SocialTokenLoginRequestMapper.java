package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.SocialLoginRequest;
import com.oito.auth.common.to.SocialTokenLoginRequest;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SocialTokenLoginRequestMapper extends BaseMapper<SocialTokenLoginRequest, SocialLoginRequest> {

}
