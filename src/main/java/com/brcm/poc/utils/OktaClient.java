package com.brcm.poc.utils;
import java.io.IOException;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.okta.sdk.clients.AuthApiClient;
import com.okta.sdk.exceptions.ApiException;
import com.okta.sdk.framework.ApiClientConfiguration;
import com.okta.sdk.models.auth.AuthResult;

 


@Component
public class OktaClient {
	
	
	
	
	public boolean isValidOktaUser(String username, String password) {
		boolean status = false;
		if (password.equals("")) {
			return false;
		}
		String resultCode = validateUser(username, password);
		
		if(resultCode.equalsIgnoreCase("SUCCESS")){
			status = true;System.out.println("User Authenticated");
		} else if(resultCode.equalsIgnoreCase("E0000004")){
			status = false;System.out.println("userid and/or pwd failed to be authenticated");
		} else if(resultCode.equalsIgnoreCase("E0000007")){
			status = false;System.out.println("URL failed");
		} else if(resultCode.equalsIgnoreCase("E0000011")){
			status = false;System.out.println("API key failed");
		} else {
			status = false;System.out.println("Unknown Response Code");
		}
		
/*		switch(resultCode){
			case "E0000004" : status = false;System.out.println("userid and/or pwd failed to be authenticated"); break; //userid or pwd failed to be authenticated
			case "E0000007" : status = false;System.out.println("URL failed"); break; //URL failed
			case "E0000011" : status = false;System.out.println("API key failed"); break; //API key failed
			case "SUCCESS" : status = true; break; // Successfully validated
			default : status = false;System.out.println("Unknown Response Code"); break;
		}
*/		return status;
	}

	private String validateUser(String username, String password) {
		String status = "BLANK";
		ApiClientConfiguration oktaSettings = new ApiClientConfiguration("https://broadcom.oktapreview.com", "00Zb8EmUxbMGiUAKfgB1dI55wyqK0LlG909JGYbTwz");
		AuthApiClient authClient = new AuthApiClient(oktaSettings);
		AuthResult result = null;
		try{
			System.out.println("before authenticate" + (new Date()));
			result = authClient.authenticate(username, password, "");
			System.out.println("after authenticating" + (new Date()));
		} catch(ApiException e){
			e.printStackTrace();
			status = e.getErrorResponse().getErrorCode();
		} catch(IOException e){
			e.printStackTrace();
			status = "IOException";
		}
		if(result != null){
			status = result.getStatus();
		}
		System.out.println("OKTA Staus: "+status);
		return status;
	}
	
	
}

