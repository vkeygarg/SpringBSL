package com.brcm.poc.security;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.brcm.poc.adaptors.OktaAdapter;
import com.brcm.poc.entity.User;
import com.brcm.poc.repositroy.UserRepository;


@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

	private final UserRepository userRepository;

	/*@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private OktaAdapter oktaAdapter;*/

	public DomainUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String login) {
		log.debug("Authenticating {}", login);
		System.out.println("in userdetails ervice");
		String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
		Optional<User> userFromDatabase = userRepository.findByUserName(lowercaseLogin);
		return null;

		/*return userFromDatabase.map(user -> {
			
			List<GrantedAuthority> grantedAuthorities = user.getUserRoles().stream()
					.map(authority -> new SimpleGrantedAuthority(authority.getRoleName())).collect(Collectors.toList());
			return new User();
		}).orElseThrow(
				() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " + "database"));*/
	}



}
