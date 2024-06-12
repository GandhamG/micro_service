package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.UserLoginAttemptVO;
import com.oito.auth.data.LoginAttempt;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LoginAttemptVoToLoginAttemptMapper {

	UserLoginAttemptVO toVO(LoginAttempt loginAttempt);

	LoginAttempt toEntity(UserLoginAttemptVO vo);

}
