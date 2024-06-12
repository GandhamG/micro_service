package com.practice.daily.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UniversityVO {

	private Long id;
	private String name;

	private String email;

	private List<StudentVO> studentVO;
}
