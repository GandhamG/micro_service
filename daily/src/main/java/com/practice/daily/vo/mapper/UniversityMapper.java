package com.practice.daily.vo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.practice.daily.entity.University;
import com.practice.daily.vo.UniversityVO;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UniversityMapper {

	@Mapping(source = "studentVO", target = "student")
	University toEntity(UniversityVO vo);

}
