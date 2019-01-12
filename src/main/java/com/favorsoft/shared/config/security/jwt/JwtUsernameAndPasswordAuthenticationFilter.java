package com.favorsoft.shared.config.security.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.favorsoft.shared.entity.Usr;
import com.favorsoft.shared.service.UsrService;
import com.google.gson.Gson;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authManager;
	
	private UsrService usrService;

	public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager, UsrService usrService) {
		this.authManager = authenticationManager;
		this.usrService = usrService;
		this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(JwtConfig.Uri, "POST"));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		Gson gson = new Gson();
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		@SuppressWarnings("unchecked")
		Map<String, String> map = gson.fromJson(stringBuilder.toString(), HashMap.class);
		
		String username = map.get("username");
		String password = map.get("password");
		
		Usr usr = (Usr) usrService.loadUserByUsername(username);
		
		if(usr != null && passwordEncoder.matches(password, usr.getPassword())) {		
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					username, password, usr.getAuthorities());			
			return authManager.authenticate(authToken);
		}else {
			throw new BadCredentialsException("Can not find user > "+username);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		Long now = System.currentTimeMillis();

		@SuppressWarnings("deprecation")
		String token = Jwts.builder()
				.setSubject(auth.getName())
				.claim("authorities", auth.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + JwtConfig.expiration * 1000))
				.signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encode(JwtConfig.secret.getBytes()))
				.compact();

		// Add token to header
		response.addHeader(JwtConfig.header, JwtConfig.prefix + token);
		try {
			Gson gson = new Gson();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("success", true);	
			map.put("token", token);
			
			response.addHeader("Content-type", "application/json");
			response.getOutputStream().print(gson.toJson(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
