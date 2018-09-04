package com.brcm.poc.entity;  

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "clients")
public class Client extends BaseEntity{
	
	
	@Field("switchnum")
	private String switchnum;
	
	@DBRef
	private User user;
	
	
	@Field("authtoken")
	private String authtoken;


	public String getSwitchnum() {
		return switchnum;
	}


	public void setSwitchnum(String switchnum) {
		this.switchnum = switchnum;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getAuthtoken() {
		return authtoken;
	}


	public void setAuthtoken(String authtoken) {
		this.authtoken = authtoken;
	}

	
	
	
}
