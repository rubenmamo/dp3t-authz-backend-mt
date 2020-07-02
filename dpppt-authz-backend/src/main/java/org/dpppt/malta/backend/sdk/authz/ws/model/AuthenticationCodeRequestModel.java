package org.dpppt.malta.backend.sdk.authz.ws.model;

public class AuthenticationCodeRequestModel {

	private String authorizationCode;
	
	private int fake;

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public int getFake() {
		return fake;
	}

	public void setFake(int fake) {
		this.fake = fake;
	}

	
}
