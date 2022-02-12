package com.legacy.notify.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.legacy.notify.domain.Notify;

public interface NotifyRepository extends JpaRepository<Notify, Long> {
	
	  @Query("SELECT "
	  		  + "new Map(n.message AS message) " 
			  + "FROM Notify n INNER JOIN n.user u " 
			  + "WHERE u.id = :user_id " 
			  + "ORDER BY n.occurDate DESC") 
	  List<Map<String, Object>> findByUserId(@Param("user_id") Long id, Pageable paging);
	 
}
