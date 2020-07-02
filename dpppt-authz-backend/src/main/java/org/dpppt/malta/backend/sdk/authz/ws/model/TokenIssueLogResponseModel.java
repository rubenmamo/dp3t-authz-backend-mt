package org.dpppt.malta.backend.sdk.authz.ws.model;

public class TokenIssueLogResponseModel {
	private String uuid;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getIssuedAt() {
		return issuedAt;
	}
	public void setIssuedAt(String issuedAt) {
		this.issuedAt = issuedAt;
	}
	private String issuedAt;

}
