package com.brcm.poc.file.controllers;

import java.security.Principal;
import java.util.Collections;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brcm.poc.entity.vm.LoginVM;
import com.brcm.poc.security.jwt.JWTConfigurer;
import com.brcm.poc.security.jwt.TokenProvider;

@RestController
@RequestMapping("/supportlink")
public class LoginController {
	static int i = 1;

	private final TokenProvider tokenProvider;

	private final AuthenticationManager authenticationManager;

	public LoginController(TokenProvider tokenProvider, AuthenticationManager authenticationManager) {
		this.tokenProvider = tokenProvider;
		this.authenticationManager = authenticationManager;
	}

	@GetMapping
	public String index(@RequestHeader HttpHeaders headers, Principal user) {
		System.out.println(i++);
		return "Hi All -- " + user.getName();
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginVM loginVM,
			@RequestHeader(value = "User-Agent") String userAgent, HttpServletResponse response) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginVM.getUsername() + ":" + userAgent.replace(":", ""), loginVM.getPassword());// TBC

		try {
			Authentication authentication = this.authenticationManager.authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = tokenProvider.createToken(authentication, userAgent);

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add(JWTConfigurer.AUTHORIZATION_HEADER, "Bearer " + jwt);
			return new ResponseEntity<>("Login Success!!!", responseHeaders, HttpStatus.OK);

		} catch (AuthenticationException ae) {
			ae.printStackTrace();
			return new ResponseEntity<>(Collections.singletonMap("AuthenticationException", ae.getLocalizedMessage()),
					HttpStatus.UNAUTHORIZED);
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(@RequestHeader(value = "Authorization") String authHeader) {
		ResponseEntity<String> rs = null;
		if (StringUtils.isEmpty(authHeader)) {
			rs = new ResponseEntity<>("AUthorization token is missing.", HttpStatus.BAD_REQUEST);
		} else {
			tokenProvider.expireToken(authHeader.substring(7, authHeader.length()));
			rs = new ResponseEntity<>("LoggedOut Successfully...", HttpStatus.OK);
		}
		return rs;

	}

}
