package com.legacy.user.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.legacy.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	// 메소드 쿼리
	Optional<User> findByEmail(String email);
	
}
