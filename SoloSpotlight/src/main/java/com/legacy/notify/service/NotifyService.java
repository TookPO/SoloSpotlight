package com.legacy.notify.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.legacy.notify.dao.NotifyRepository;
import com.legacy.notify.domain.Notify;

@Service
public class NotifyService {
	
	private static Logger logger = LoggerFactory.getLogger(NotifyService.class);
	
	@Autowired
	NotifyRepository notifyRepository;
	
	public List<Map<String, Object>> selectThreeRecent(Long id) {
		logger.debug("[리스트 전 id]=="+id);
		
		Pageable paging = PageRequest.of(0, 2);
		List<Map<String, Object>> notifyList;
		List<Notify> notiTest = notifyRepository.findAll();
		
		notiTest.forEach( noti -> logger.debug("[노티 파이:]"+ noti));

		try {
			
			notifyList = notifyRepository.findByUserId(id, paging);
		}catch(Exception e) {
			return new ArrayList<Map<String, Object>>();
		}
		return notifyList;
	}

}
