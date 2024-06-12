package com.practice.daily.service;

import com.practice.daily.vo.UniversityVO;

public interface UniversityService {

	Object save(UniversityVO vo);

	Object getAll();

	Object update(UniversityVO vo);

}
