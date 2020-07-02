package org.dpppt.malta.backend.sdk.authz.ws.controller;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.ws.model.AuthenticationCodeRequestModel;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtTokenProvider {

	private static final Log LOG = LogFactory.getLog(JwtTokenProvider.class);

	@Value("${security.jwt.token.jwksUrl}")
	private String jwksUrl = "testing";

	@Value("${security.jwt.token.expire-length:3600000}")
	private long validityInMilliseconds = 3600000; // 1h
	private final KeyPair pair;
	
	public JwtTokenProvider(KeyPair pair) {
		super();
		this.pair = pair;
	}

	public CreateTokenResult createToken(AuthenticationCodeRequestModel codeRequest, CovidCode covidCode) {

		
		Instant issuedAt = Instant.now();
		Instant validity = issuedAt.plusMillis(validityInMilliseconds);
		UUID uuid = UUID.randomUUID();
		
		Claims claims = Jwts.claims()
				.setId(uuid.toString())
				.setSubject(codeRequest.getAuthorizationCode())
				.setIssuer("dpppt-authz-backend")
				.setIssuedAt(Date.from(issuedAt))
				.setExpiration(Date.from(validity));
				
		claims.put("onset", covidCode.getOnsetDate().toString());		
		claims.put("risk", covidCode.getTransmissionRisk());		
		claims.put("fake", codeRequest.getFake());
		claims.put("scope", "exposed");

		
		String jwts = Jwts.builder()//
				.setClaims(claims)//
				.signWith(this.pair.getPrivate())				
				.compact();
		
		return new CreateTokenResult(issuedAt, uuid, jwts);
	}


}
