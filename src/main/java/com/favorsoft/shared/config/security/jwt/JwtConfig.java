package com.favorsoft.shared.config.security.jwt;

public class JwtConfig {	
    public static String Uri = "/auth/login";

    public static String header = "Authorization";

    public static String prefix = "bearer ";

    public static int expiration = 24*60*60;

    public static String secret = "JwtSecretKey-favorsoft-mScheduler-profavor0123456789";

	public String getUri() {
		return Uri;
	}

	public void setUri(String uri) {
		Uri = uri;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		JwtConfig.header = header;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		JwtConfig.prefix = prefix;
	}

	public int getExpiration() {
		return expiration;
	}

	public void setExpiration(int expiration) {
		JwtConfig.expiration = expiration;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		JwtConfig.secret = secret;
	}
    
    

}
