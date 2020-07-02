package org.dpppt.malta.backend.sdk.authz.data.model;

import java.time.Instant;
import java.util.UUID;

public class TokenIssueLog {

	private UUID uuid;
	private Instant issuedAt;
		
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public Instant getIssuedAt() {
		return issuedAt;
	}
	public void setIssuedAt(Instant issuedAt) {
		this.issuedAt = issuedAt;
	}
	
	
}
