package com.oito.auth.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.UserLoginRequest;
import com.oito.auth.mapper.AuditMapper;
import com.oito.auth.v1.common.to.OTPPhoneVerificationRequestV1;

@Mapper(uses = {
		AuditMapper.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OTPLoginV1Mapper {

	OTPPhoneVerificationRequestV1 toV1VO(UserLoginRequest request);

	UserLoginRequest toVO(OTPPhoneVerificationRequestV1 requestV1);

}
