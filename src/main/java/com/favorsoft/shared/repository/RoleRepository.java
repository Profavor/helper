package com.favorsoft.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.favorsoft.shared.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
	
	public Role findByRoleCode(String roleCode);
}
