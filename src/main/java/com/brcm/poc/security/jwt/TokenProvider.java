package com.brcm.poc.security.jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.brcm.poc.dto.UserInfo;
import com.brcm.poc.security.config.ConfigProperties;
import com.fasterxml.jackson.databind.ObjectMapper;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class TokenProvider {

	private final Logger log = LoggerFactory.getLogger(TokenProvider.class);
	private static final String AUTHORITIES_KEY = "auth";
	private String secretKey;
	private long tokenValidityInMilliseconds;
	private final ConfigProperties configProperties;

	public TokenProvider(ConfigProperties configProperties) {
		this.configProperties = configProperties;
	}

	@PostConstruct
	public void init() {
		this.secretKey = configProperties.getSecurity().getAuthentication().getJwt().getSecret();

		this.tokenValidityInMilliseconds = 1000
				* configProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();

	}

	public String createToken(Authentication authentication, String userAgent) {
		String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		long now = (new Date()).getTime();
		Date validity = new Date(now + this.tokenValidityInMilliseconds);

		return Jwts.builder().setSubject(authentication.getName()).claim(AUTHORITIES_KEY, authorities)
				.signWith(SignatureAlgorithm.HS512, secretKey).setExpiration(validity).compact();
	}

	public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

            Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            
            
            ObjectMapper objectMapper = new ObjectMapper();
           
            String login = objectMapper.convertValue(claims.get("LOGIN_INFO"), String.class);
            		//(claims.get("COMPANY_INFO").toString())
            //User principal = new User(claims.getSubject(), "", authorities);
            User principal = new UserInfo(claims.getSubject(), "", authorities,login);

            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        }

	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.info("Invalid JWT signature.");
			log.trace("Invalid JWT signature trace: {}", e);
		} catch (MalformedJwtException e) {
			log.info("Invalid JWT token.");
			log.trace("Invalid JWT token trace: {}", e);
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token.");
			log.trace("Expired JWT token trace: {}", e);
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token.");
			log.trace("Unsupported JWT token trace: {}", e);
		} catch (IllegalArgumentException e) {
			log.info("JWT token compact of handler are invalid.");
			log.trace("JWT token compact of handler are invalid trace: {}", e);
		}
		return false;
	}
	
	public String expireToken(String token) {
		 
		 System.out.println("Expire now>>>>>>>>>> "+token);
		 try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().setExpiration(new Date());
				
				
			} catch (Exception e) {
				System.out.println(e);
			}
		 
		 return "Token in expired now";
		 
	 }

	public ConfigProperties getConfig() {
		// TODO Auto-generated method stub
		return this.configProperties;
	}
}
