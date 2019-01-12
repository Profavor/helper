package com.favorsoft.shared.config.security.jwt;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String header = request.getHeader(JwtConfig.header);

		if (header == null || !header.startsWith(JwtConfig.prefix)) {
			filterChain.doFilter(request, response); // If not valid, go to the next filter.
			return;
		}
		
		String token = header.replace(JwtConfig.prefix, "");

		try { 
			Claims claims = Jwts.parser()
					.setSigningKey(Base64.getEncoder().encode(JwtConfig.secret.getBytes()))
					.parseClaimsJws(token)
					.getBody();
			
			String username = claims.getSubject();

			if (username != null) {
				@SuppressWarnings("unchecked")
				List<String> authorities = (List<String>) claims.get("authorities");

				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
						username, null,
						authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
				SecurityContextHolder.getContext().setAuthentication(auth);
			}

		} catch (Exception e) {
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}

}
