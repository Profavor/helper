package com.favorsoft.helper.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.entity.ProjectShift;
import com.favorsoft.helper.entity.ShiftHelperRequest;

@Repository
public interface ShiftHelperRequestRepository extends JpaRepository<ShiftHelperRequest, String> {
	
	public ShiftHelperRequest findByProjectShiftAndHelper(ProjectShift projectShift, Helper helper);

}
