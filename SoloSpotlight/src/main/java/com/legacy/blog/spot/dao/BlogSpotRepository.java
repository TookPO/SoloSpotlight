package com.legacy.blog.spot.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.legacy.blog.post.domain.BlogPost;
import com.legacy.blog.spot.domain.BlogSpot;

public interface BlogSpotRepository extends JpaRepository<BlogSpot, Long> {
	
	@Query(value = 
			"SELECT "
			+ "s.* "
			+ "FROM blog_spot s "
			+ "WHERE s.follower_user_id = :followerId "
			+ "AND s.followee_user_id = :followeeId ", nativeQuery = true)
	Optional<BlogSpot> findByFolloweeAndFollowerId(@Param("followerId")Long followerId, @Param("followeeId")Long followeeId);
	
	@Query(value = 
			"SELECT "
			+ "s.* "
			+ "FROM blog_spot s "
			+ "WHERE s.follower_user_id = :followerId "
			+ "ORDER BY rand() ", nativeQuery = true)
	List<BlogSpot> findByFollowerIdRandom(@Param("followerId")Long followerId, Pageable spotPaging);

	@Query(value = 
			"SELECT "
			+ "s.* "
			+ "FROM blog_spot s "
			+ "WHERE s.follower_user_id = :followerId ", nativeQuery = true)	
	List<BlogSpot> findByFollowerId(@Param("followerId")Long followerId, Pageable spotPaging);
	

}
