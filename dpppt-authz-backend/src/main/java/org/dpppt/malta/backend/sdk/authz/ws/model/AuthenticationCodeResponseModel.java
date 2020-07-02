package org.dpppt.malta.backend.sdk.authz.ws.model;

public class AuthenticationCodeResponseModel {

	private String accessToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public AuthenticationCodeResponseModel(String accessToken) {
		super();
		this.accessToken = accessToken;
	}
	
	
}
