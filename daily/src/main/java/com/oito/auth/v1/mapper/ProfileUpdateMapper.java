package com.oito.auth.v1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.AppUserTO;
import com.oito.auth.v1.common.to.ProfileUpdateRequestVO;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileUpdateMapper {

	AppUserTO toEntity(ProfileUpdateRequestVO vo);

}
