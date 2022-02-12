package com.legacy.blog.info.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.legacy.blog.info.domain.BlogInfo;

public interface BlogInfoRepository extends JpaRepository<BlogInfo, Long> {

}
