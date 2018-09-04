package com.brcm.poc.adaptors; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brcm.poc.utils.OktaClient;


@Service
public class OktaAdapter {
	@Autowired
	private OktaClient serviceClient;
	
	public boolean validatedUser(String user, String pass)  {
		return   serviceClient.isValidOktaUser(user,pass); 

	}
}
