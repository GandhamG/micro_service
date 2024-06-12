package com.oito.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.oito.auth.common.to.UserSignUpRequest;
import com.oito.auth.data.User;

@Mapper(uses = { AuditMapper.class,
		MapStructHelperHandler.class }, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserSignUpRequestToUserMapper extends BaseMapper<UserSignUpRequest, User> {

	@Override
	@Mapping(source = "source", target = "source", qualifiedByName = "JsonStrToObject")
	UserSignUpRequest toVO(User entity);

	@Override
	@Mapping(source = "source", target = "source", qualifiedByName = "ObjectToJsonStr")
	User toEntity(UserSignUpRequest vo);

}
