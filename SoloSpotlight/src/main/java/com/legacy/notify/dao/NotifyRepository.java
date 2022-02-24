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
	  		  + "new Map(n.division AS division, n.message AS message, n.information AS information) " 
			  + "FROM Notify n INNER JOIN n.user u " 
			  + "WHERE u.id = :userId " 
			  + "ORDER BY n.createdDate DESC") 
	  List<Map<String, Object>> findByUserId(@Param("userId") Long id, Pageable paging);

	 @Query("SELECT "
	  		  + "new Map(n.division AS division, n.message AS message, n.information AS information) "
			  + "FROM Notify n INNER JOIN n.user u " 
			  + "WHERE u.id = :userId "
			  + "AND filter = :filter " 
			  + "ORDER BY n.createdDate DESC ") 	 
	List<Map<String, Object>> findByUserIdFilter(@Param("userId")Long userId,@Param("filter")String filter, Pageable page); 
}
