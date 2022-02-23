package com.legacy.user.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.legacy.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	// 메소드 쿼리
	Optional<User> findByEmail(String email);
	
	@Query(value = 
			"SELECT "
			+ "u.id AS id, u.name AS name, u.picture AS picture "
			+ "FROM \"USER\" u "
			+ "INNER JOIN blog_info i ON u.id = i.user_id "
			+ "INNER JOIN blog_post p ON i.id = p.blog_info_id "
			+ "GROUP BY u.id "
			+ "ORDER BY SUM(p.view_count) DESC ", nativeQuery = true)
	List<Map<String, Object>> findByPopUser(Pageable popUserPaging);

	@Query("SELECT "
			+ "new Map(u.id AS id, u.name AS name, u.picture AS picture, u.age AS age, u.location AS location, u.job AS job, SIZE(u.followeeList) AS followeeSize ) "
			+ "FROM User u "
			+ "WHERE u.id = :userId ")
	Map<String, Object> findByUserAndSpotSize(@Param("userId")Long userId);	
}
