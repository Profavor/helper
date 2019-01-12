package com.favorsoft.helper.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.helper.entity.Helper;

@Repository
public interface HelperRepository extends JpaRepository<Helper, String> {
	
	public Helper findByKnoxId(String knoxId);

}
