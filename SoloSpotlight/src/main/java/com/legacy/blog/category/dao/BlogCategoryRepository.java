package com.legacy.blog.category.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.legacy.blog.category.domain.BlogCategory;

public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {

}
