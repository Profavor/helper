package com.favorsoft.shared.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.favorsoft.shared.entity.Role;
import com.favorsoft.shared.entity.Usr;

public interface UsrService extends UserDetailsService{
	
	public Usr save(Usr usr);
	
	public Role save(Role role);
	
	public Role getRole(String roleCode);
	
	
	public boolean registUsr(Usr usr);
}
