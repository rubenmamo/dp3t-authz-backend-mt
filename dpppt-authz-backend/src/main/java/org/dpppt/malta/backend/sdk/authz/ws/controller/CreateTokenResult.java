package org.dpppt.malta.backend.sdk.authz.ws.controller;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class CreateTokenResult {

	private Instant issuedAt;
	private UUID uuid;
	private String token;
	public Instant getIssuedAt() {
		return issuedAt;
	}
	public UUID getUuid() {
		return uuid;
	}
	public String getToken() {
		return token;
	}
	public CreateTokenResult(Instant issuedAt, UUID uuid, String token) {
		super();
		this.issuedAt = issuedAt;
		this.uuid = uuid;
		this.token = token;
	}
	
	
}
