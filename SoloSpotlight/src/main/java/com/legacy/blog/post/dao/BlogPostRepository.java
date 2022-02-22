package com.legacy.blog.post.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.legacy.blog.info.domain.BlogInfo;
import com.legacy.blog.post.domain.BlogPost;
import com.legacy.blog.post.vo.BlogPostDto;
import com.legacy.blog.post.vo.BlogPostDtoImpl;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
	
	@Query("SELECT "
			+ "new com.legacy.blog.post.vo.BlogPostDto(p.id AS id, p.title AS title, p.content AS content, p.thumbnail AS thumbnail, p.viewCount AS viewCount, p.isPublic AS isPublic, p.createdDate AS createdDate ) "
			+ "FROM BlogPost p ")
	List<BlogPostDto> findByPopPost(Pageable popPaging);
	
	@Query("SELECT "
			+ "p "
			+ "FROM BlogPost p INNER JOIN p.blogInfo i "
			+ "WHERE i.id = :infoId")
	Optional<BlogPost> findByInfoId(@Param("infoId")Long infoId);
	
	@Query(value= 
			"SELECT "
			+ "p.id AS id, p.title As title, p.thumbnail AS thumbnail, p.view_count AS viewCount, p.is_public AS isPublic, p.created_date AS createdDate "
			+ "FROM Blog_Post p "
			+ "WHERE p.blog_Info_Id IN (:infoId) "
			+ "AND p.is_public IN (TRUE) "
			+ "AND p.is_recommend IN (TRUE) "
			+ "ORDER BY rand() "
			, nativeQuery = true)
	List<BlogPostDtoImpl> findByIsRecommendTrueImpl(@Param("infoId")Long infoId, Pageable recommendPaging);

	@Query(value= 
			"SELECT "
			+ "p.id AS id, p.title As title, p.thumbnail AS thumbnail, p.view_count AS viewCount, p.is_public AS isPublic, p.created_date AS createdDate "
			+ "FROM Blog_Post p "
			+ "WHERE p.blog_Info_Id IN (:infoId) "
			+ "AND p.is_public IN (TRUE) "
			+ "AND p.is_recommend IN (TRUE) "
			+ "AND p.id NOT IN (:postId)"			
			+ "ORDER BY rand() "
			, nativeQuery = true)
	List<BlogPostDtoImpl> findByIsRecommendTrueNotThisPostIdImpl(@Param("infoId")Long infoId, @Param("postId")Long postId, Pageable recommendPaging);

	
	@Query(value= 
			"SELECT "
			+ "p.id AS id, p.title As title, p.thumbnail AS thumbnail, p.view_count AS viewCount, p.is_public AS isPublic, p.created_date AS createdDate "
			+ "FROM Blog_Post p "
			+ "WHERE p.blog_Info_Id IN (:infoId) "
			+ "AND p.is_public IN (TRUE) "
			+ "AND p.is_recommend NOT IN (TRUE) "
			+ "AND p.id NOT IN (:postId)"
			+ "ORDER BY rand() "
			, nativeQuery = true)
	List<BlogPostDtoImpl> findByIsRecommendFalseNotThisPostIdImpl(@Param("infoId")Long infoId, @Param("postId")Long postId, Pageable recommendPaging);
	
	@Query("SELECT "
			+ "new Map(p.id AS id, p.title AS title, p.content AS content, p.thumbnail AS thumbnail, p.viewCount AS viewCount, p.good As good, p.isPublic AS isPublic, p.createdDate AS createdDate, c.title AS categoryTitle) "
			+ "FROM BlogPost p INNER JOIN p.blogInfo i INNER JOIN p.blogCategory c "
			+ "WHERE i.id = :infoId "
			+ "ORDER BY p.createdDate DESC")
	List<Map<String, Object>> findAllRecent(@Param("infoId")Long infoId, Pageable paging);
	
	@Query("SELECT "
			+ "new Map(p.id AS id, p.title AS title, p.content AS content, p.thumbnail AS thumbnail, p.viewCount AS viewCount, p.good As good, p.isPublic AS isPublic, p.createdDate AS createdDate, c.title AS categoryTitle) "
			+ "FROM BlogPost p INNER JOIN p.blogCategory c "
			+ "WHERE p.id = :postId ")	
	Optional<Map<String, Object>> findByIdJoinCategoryMap(@Param("postId")Long postId);
	
//	@Query("SELECT "
//			+ "p "
//			+ "FROM BlogPost p INNER JOIN p.blogCategory c "
//			+ "WHERE c.id = :categoryId "
//			+ "ORDER BY p.createdDate DESC")
	@Query(value =  
			"SELECT "
			+ "p.* "
			+ "FROM blog_post p "
			+ "WHERE p.blog_category_id IN (:categoryId) "
			+ "ORDER BY p.id DESC "
			+ "LIMIT 5 "
			+ "OFFSET ((SELECT "
			+ "			(MAX(rownum)-1) "
			+ "			FROM blog_post) "
			+ "		   - "
			+ "		   (SELECT "
			+ "			(MAX(rownum)-1) "
			+ "			FROM BLOG_POST "
			+ "			WHERE id <= :postId))", nativeQuery = true)
	List<BlogPost> findByInfoIdAndCategoryOne(@Param("categoryId")Long categoryId, @Param("postId") Long postId);

	@Query(value =  
			"SELECT "
			+ "p.* "
			+ "FROM blog_post p "
			+ "WHERE p.blog_category_id IN (:categoryId) "
			+ "AND p.id < (:postId+6) "
			+ "ORDER BY p.id DESC "
			+ "LIMIT 5 ", nativeQuery = true)	
	List<BlogPost> findByInfoIdAndCategoryOnePrev(@Param("categoryId")Long categoryId, @Param("postId") Long postId);

	@Query(value =  
			"SELECT "
			+ "p.* "
			+ "FROM blog_post p "
			+ "WHERE p.blog_category_id IN (:categoryId) "
			+ "AND p.id < :postId "
			+ "ORDER BY p.id DESC "
			+ "LIMIT 5 ", nativeQuery = true)	
	List<BlogPost> findByInfoIdAndCategoryOneNext(@Param("categoryId")Long categoryId, @Param("postId") Long postId);
	
	@Query(value =
			"SELECT "
			+ "p.id AS id, p.title AS title, p.content AS content, p.view_count AS viewCount, p.created_date AS createdDate, u.id AS writerId, u.name AS writerName, u.picture AS writerPicture "
			+ "from BLOG_SPOT s "
			+ "inner JOIN \"USER\" u ON s.followee_user_id = u.id "
			+ "inner JOIN BLOG_INFO i ON u.id = i.user_id "
			+ "inner JOIN BLOG_POST p ON i.id = p.blog_info_id "
			+ "where s.follower_user_id = :followerId "
			+ "ORDER BY p.created_date DESC ", nativeQuery = true)
	List<Map<String, Object>> findByFollowerIdJoinInfoAndPost(@Param("followerId")Long followerId, Pageable spotPage);
	
	@Query(value=
			"SELECT "
			+ "p.id AS id, p.title AS title, p.content AS content, p.thumbnail AS thumbnail, p.view_count AS viewCount, p.is_public AS isPublic, p.created_date AS createdDate "
			+ "FROM blog_post p "
			+ "INNER JOIN blog_info i ON p.blog_info_id = i.id "
			+ "INNER JOIN \"USER\" u ON i.user_id = u.id "
			+ "WHERE u.id NOT IN (:userId) "
			+ "ORDER BY rand(), p.created_date ", nativeQuery = true)
	List<Map<String, Object>> findAllRandomNotUserId(@Param("userId")Long userId);
}