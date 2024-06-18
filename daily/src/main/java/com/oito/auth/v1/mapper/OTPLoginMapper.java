package com.oito.auth.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.OTPPhoneVerificationRequest;
import com.oito.auth.common.to.UserLoginRequest;
import com.oito.auth.mapper.AuditMapper;

@Mapper(uses = {
		AuditMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OTPLoginMapper {

	OTPPhoneVerificationRequest map(UserLoginRequest request);

	UserLoginRequest map(OTPPhoneVerificationRequest request);

}
