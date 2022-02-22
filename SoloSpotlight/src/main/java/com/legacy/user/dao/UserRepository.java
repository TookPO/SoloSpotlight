package com.legacy.user.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.legacy.user.domain.User;
import com.legacy.user.vo.UserDto;

public interface UserRepository extends JpaRepository<User, Long> {
	
	// 메소드 쿼리
	Optional<User> findByEmail(String email);
	
	@Query("SELECT "
			+ "new map(u.id AS id, u.name AS name, u.email AS email, u.picture AS picture, SIZE(i.blogPostList) AS maxPost )"
			+ "FROM User u INNER JOIN u.blogInfo i "
			+ "ORDER BY maxPost DESC " )// 이거 안됨
	List<UserDto> findByPopUser(Pageable popUserPaging);
}
