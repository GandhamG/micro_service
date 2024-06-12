package com.practice.daily.vo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.practice.daily.entity.Student;
import com.practice.daily.vo.StudentVO;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentMapper {
	// void toVO(Student entity, @MappingTarget StudentVO dto);

	void toEntity(StudentVO vo, @MappingTarget Student entity);

	@Mapping(source = "vo", target = "university")
	Student toEntity(StudentVO vo);

}
