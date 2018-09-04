package com.brcm.poc.security.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.brcm.poc.adaptors.OktaAdapter;
import com.brcm.poc.entity.User;
import com.brcm.poc.repositroy.UserRepository;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OktaAdapter oktaAdapter;

	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override

	public Authentication authenticate(Authentication authentication) {
		String name = authentication.getName().split(":")[0];
		String switchNum =  authentication.getName().split(":")[1];
		String pass = authentication.getCredentials().toString();
		Authentication auth = null;


		System.out.println("name:" + authentication.getName());

		Optional<User> userFromDatabase1 = userRepository.findByUserNamePass(name, passwordEncoder.encode(pass));
		if (userFromDatabase1.isPresent()) {
			auth = new UsernamePasswordAuthenticationToken(name+":"+switchNum, pass, new ArrayList<>());
		} else {
				
				auth = authAndSave(name, pass, switchNum);
		}
		return auth;
	}

	private synchronized Authentication authAndSave(String name, String pass, String switchNum) {
		Authentication auth = null;
		User usr = null;
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add( new SimpleGrantedAuthority("ROLE_USER"));
		
		
		Optional<User> userFromDatabase = userRepository.findByUserName(name);
		if (userFromDatabase.isPresent()) {// First time generate token and set in database
			System.out.println("User found in DB");
			usr = userFromDatabase.get();
			if(BCrypt.checkpw(pass, usr.getPassword()))
				return new UsernamePasswordAuthenticationToken(name+":"+switchNum, pass, grantedAuthorities);
		}
		
		if (oktaAdapter.validatedUser(name, pass)) {
			auth = new UsernamePasswordAuthenticationToken(name+":"+switchNum, pass, grantedAuthorities);
			if (usr == null) {
				usr = new User();
				usr.setUname(name);
			}
			usr.setPassword(passwordEncoder.encode(pass));
			usr.setValiduser(true);
			userRepository.save(usr);
			System.out.println("User saved in DB...");
		} 
		return auth;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
