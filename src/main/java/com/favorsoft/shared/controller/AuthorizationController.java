package com.favorsoft.shared.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.favorsoft.shared.entity.Usr;
import com.favorsoft.shared.model.ResponseModel;
import com.favorsoft.shared.service.UsrService;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
	@Autowired
	private UsrService usrService;
	
	@PostMapping("/logout")
	public String logout() {
		return "";
	}
	
	@PostMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response) {
		return "";
	}
	
	@RequestMapping(value="/createHelper", method=RequestMethod.POST)
	public ResponseModel createUser(@RequestBody Usr usr) {
		ResponseModel responseModel = new ResponseModel();
		
		if(!usr.getAcceptCode().toUpperCase().equals("PLMCELL")) {
			responseModel.setSuccess(false);
			responseModel.setMessage("승인코드가 올바르지 않습니다.");
			return responseModel;
		}
		
		try {
			responseModel.setSuccess(usrService.registUsr(usr));			
		}catch(Exception e) {
			responseModel.setSuccess(false);
			responseModel.setMessage(e.getMessage());
		}
		return responseModel;
	}

}
