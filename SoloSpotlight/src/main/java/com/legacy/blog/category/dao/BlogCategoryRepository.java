package com.legacy.blog.category.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.legacy.blog.category.domain.BlogCategory;

public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {
	@Query("SELECT "
			+ "c.title AS title "
			+ "FROM BlogCategory c INNER JOIN c.blogInfo i "
			+ "WHERE i.id = :infoId "
			+ "ORDER BY c.id ASC")
	List<String> findByBlogInfoIdString(@Param("infoId")Long infoId);
	
	@Query("FROM BlogCategory c INNER JOIN c.blogInfo i "
			+ "WHERE i.id = :infoId "
			+ "ORDER BY c.modifiedDate ASC")
	List<BlogCategory> findByBlogInfoId(@Param("infoId")Long infoId);	
}
