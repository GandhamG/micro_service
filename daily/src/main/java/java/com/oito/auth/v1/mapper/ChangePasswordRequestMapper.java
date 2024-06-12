package com.oito.auth.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.ChangePasswordRequest;
import com.oito.auth.v1.common.to.ChangePasswordRequestV1;
import com.oito.auth.v1.common.to.ResetPasswordRequestV1;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ChangePasswordRequestMapper {

	ChangePasswordRequest toRequest(ResetPasswordRequestV1 requst);

	ChangePasswordRequest toRequest(ChangePasswordRequestV1 requst);

}
