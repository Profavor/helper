package com.favorsoft.shared.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.favorsoft.helper.entity.Helper;
import com.favorsoft.helper.service.HelperService;
import com.favorsoft.shared.entity.Role;
import com.favorsoft.shared.entity.Usr;
import com.favorsoft.shared.repository.RoleRepository;
import com.favorsoft.shared.repository.UsrRepository;
import com.favorsoft.shared.service.UsrService;

@Service
@Transactional
public class UsrServiceImpl implements UsrService {
	private final static String HELPER_ROLE = "ROLE_HELPER_USER";	
	
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    public UsrRepository usrRepository;
    
    @Autowired
    public HelperService helperService;
    

    @Autowired
    public RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usrRepository.findByLoginId(username);
    }

	@Override
	public Usr save(Usr usr) {
		return usrRepository.saveAndFlush(usr);
		
	}

	@Override
	public Role getRole(String roleCode) {
		return roleRepository.findByRoleCode(roleCode);
	}

	@Override
	public Role save(Role role) {
		return roleRepository.saveAndFlush(role);
	}

	@Override
	public boolean registUsr(Usr usr) {
		Helper helper = new Helper();
		helper.setKnoxId(usr.getLoginId());
		helper.setUserName(usr.getUserName());
		helper.setDeptCode(usr.getAcceptCode());
		helper.setEnable(true);
		helper.setSite1365("N");
		usr.setPassword(passwordEncoder.encode(usr.getPassword()));
		usr.setAccountNonExpired(true);
		usr.setAccountNonLocked(true); 
		usr.setEnabled(true);
		usr.setCredentialsNonExpired(true);
		
		Role role = roleRepository.findByRoleCode(HELPER_ROLE);
		if(role == null) {
			Role tempRole = new Role();
			tempRole.setRoleCode(HELPER_ROLE);
			tempRole.setRoleName("자원봉사 사용자");
			tempRole.setRoleDescription("자원봉사 신청 및 확인하는 권한입니다.");
			role = roleRepository.save(tempRole);
		}

		Helper tempHelper = helperService.getHelper(usr.getLoginId());		
		if(tempHelper == null) {
			helperService.saveHelper(helper);
		}

		Collection<Role> usrRoles = new ArrayList<Role>();
		usrRoles.add(role);
		
		usr.setRoles(usrRoles);
		usrRepository.save(usr);		
		return true;
	}
}
