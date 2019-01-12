package com.favorsoft.shared.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.favorsoft.shared.config.security.jwt.JwtConfig;
import com.favorsoft.shared.entity.Usr;
import com.favorsoft.shared.service.UsrService;

@RestController
@RequestMapping("/api/usr")
public class UsrController {
	
	@Autowired
	private UsrService usrService;	
	
	@GetMapping
	public Map<String, Object> getUsr(HttpServletRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Map<String, Object> map = new HashMap<String, Object>();
		if(authentication != null) {
			Usr usr = (Usr)usrService.loadUserByUsername(authentication.getName());
			Map<String, Object> params = new HashMap<String, Object>();
			if(usr != null) {
				params.put("id", usr.getId());
				params.put("loginId", usr.getLoginId());
				params.put("roles", usr.getAuthorities());
				params.put("token", request.getHeader(JwtConfig.header).replace(JwtConfig.prefix, ""));
			}			
			map.put("usr",  params);
		}		
		
		return map;
	}
}
