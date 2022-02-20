package com.legacy.blog.good.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.legacy.blog.good.domain.BlogGood;

public interface BlogGoodRepository extends JpaRepository<BlogGood, Long> {
	
	@Query("SELECT "
			+ "g "
			+ "FROM BlogGood g INNER JOIN g.user u INNER JOIN g.blogPost p "
			+ "WHERE u.id = :userId "
			+ "AND p.id = :postId ")
	BlogGood findByUserIdAndPostId(@Param("userId")Long userId, @Param("postId")Long postId);

}
