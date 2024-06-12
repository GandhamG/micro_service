package com.practice.daily.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.daily.service.UniversityService;
import com.practice.daily.vo.UniversityVO;

@RestController
@RequestMapping("/university")
public class UniversityController {

	@Autowired
	private UniversityService uService;

	@PostMapping
	public Object save(@RequestBody final UniversityVO vo) {
		return uService.save(vo);
	}

	@GetMapping
	public Object getAll() {
		return uService.getAll();
	}

	@PutMapping
	public Object update(@RequestBody final UniversityVO vo) {
		return uService.update(vo);
	}

}
