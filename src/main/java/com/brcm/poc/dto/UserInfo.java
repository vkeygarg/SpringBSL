package com.brcm.poc.dto;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;



public class UserInfo extends User{
	
	private static final long serialVersionUID = 1L;
	

	private Long editId;
	
	private String login;
	public UserInfo(String username, String password, 
			Collection<? extends GrantedAuthority> authorities, String login) {
		super(username, password, authorities);
		
		this.login = login;
	}
	
	
	/**
	 * @return the editId
	 */
	public Long getEditId() {
		return editId;
	}
	/**
	 * @param editId the editId to set
	 */
	public void setEditId(Long editId) {
		this.editId = editId;
	}


	

	
	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	
	
}
