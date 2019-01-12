package com.favorsoft.shared.entity.key;

import java.io.Serializable;

import com.favorsoft.shared.entity.Role;
import com.favorsoft.shared.entity.Usr;

public class UsrRoleKey implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Usr usr;
    private Role role;
    
	public Usr getUsr() {
		return usr;
	}
	public void setUsr(Usr usr) {
		this.usr = usr;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
    
    
}