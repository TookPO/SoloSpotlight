package com.legacy.user.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.legacy.notify.dao.NotifyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final NotifyRepository notifyRepository;

	public List<Map<String, Object>> recentThree(Long id) {
		Pageable paging = PageRequest.of(0, 2, Sort.Direction.DESC, "id");
		return notifyRepository.findByUserId(id, paging);
	}
	
	
}
