package com.legacy.blog.info.dao;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.blog.info.vo.BlogInfoDto;

public interface BlogInfoRepository extends JpaRepository<BlogInfo, Long> {
	
	@Query("SELECT "
			+ "i "
			+ "FROM BlogInfo i INNER JOIN i.user u "
			+ "WHERE u.id = :user_id")
	Optional<BlogInfo> findByUserId(@Param("user_id") Long userId);
	
	@Query("SELECT "
			+ "new com.legacy.blog.info.vo.BlogInfoDto(i.id AS id, i.name AS name, i.intro AS intro, i.headerColor AS headerColor, i.revenue AS revenue) "
			+ "FROM BlogInfo i INNER JOIN i.user u "
			+ "WHERE u.id = :user_id")	
	Optional<BlogInfoDto> findByUserIdDto(@Param("user_id") Long userId);
}
