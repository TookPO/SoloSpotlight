package com.legacy.blog.post.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.legacy.blog.post.domain.BlogPost;
import com.legacy.blog.post.vo.BlogPostDtoImpl;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
	
	@Query("SELECT "
			+ "p "
			+ "FROM BlogPost p INNER JOIN p.blogInfo i "
			+ "WHERE i.id = :infoId")
	Optional<BlogPost> findByInfoId(@Param("infoId")Long infoId);
	
	@Query(value= 
			"SELECT "
			+ "p.id AS id, p.title As title, p.content AS content, p.thumbnail AS thumbnail, p.view_count AS viewCount, p.good AS good, p.is_public AS isPublic, p.created_date AS createdDate "
			+ "FROM Blog_Post p "
			+ "WHERE p.blog_Info_Id IN (:infoId) "
			+ "AND p.is_public IN (TRUE) "
			+ "AND p.is_recommend IN (TRUE) "
			+ "ORDER BY rand() "
			+ "LIMIT 0, 4"
			, nativeQuery = true)
	List<BlogPostDtoImpl> findByIsRecommendTrueImpl(@Param("infoId")Long infoId);
	
	@Query("SELECT "
			+ "new Map(p.id AS id, p.title AS title, p.content AS content, p.thumbnail AS thumbnail, p.viewCount AS viewCount, p.good As good, p.isPublic AS ispublic, p.createdDate AS createdDate, c.title AS categoryTitle) "
			+ "FROM BlogPost p INNER JOIN p.blogInfo i INNER JOIN p.blogCategory c "
			+ "WHERE i.id = :infoId "
			+ "ORDER BY p.createdDate DESC")
	List<Map<String, Object>> findAllRecent(@Param("infoId")Long infoId, Pageable paging);
}
