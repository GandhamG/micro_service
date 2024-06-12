package com.practice.daily.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudentVO {

	private Long studentId;

	private String name;

	private String email;

	private UniversityVO vo;

}
