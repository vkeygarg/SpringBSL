package com.brcm.poc.entity;  


import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "users")
public class User extends BaseEntity{
	
	
	@Field("uname")
	private String uname;
	
	
	@Field("password")
	private String password;
	
	
	@Field("validuser")
	private boolean validuser;


	public String getUname() {
		return uname;
	}


	public void setUname(String uname) {
		this.uname = uname;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public boolean isValiduser() {
		return validuser;
	}


	public void setValiduser(boolean validuser) {
		this.validuser = validuser;
	}
	
	
	
}
