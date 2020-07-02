/*
 * Copyright (c) 2020 Ubique Innovation AG <https://www.ubique.ch>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.security;

import java.io.IOException;
import java.net.URL;
import java.security.PublicKey;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.tomcat.util.http.parser.Vary;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

public class AuthzJwtDecoder implements JwtDecoder {
	private OAuth2TokenValidator<Jwt> validator;

	private String jwkUrl;
	
	public AuthzJwtDecoder(String jwkUrl) {
		this.jwkUrl = jwkUrl;
	}

	public void setJwtValidator(OAuth2TokenValidator<Jwt> validator) {
		this.validator = validator;
	}

	@Override
	public Jwt decode(String token) throws JwtException {

		try {
			SignedJWT jwt = SignedJWT.parse(token);
			
			JWKSet publicKeys = JWKSet.load(new URL(jwkUrl));
			JWKSource keySource = new ImmutableJWKSet(publicKeys);			
			JWSKeySelector<SecurityContext> keySelector =
				    new JWSVerificationKeySelector<SecurityContext>(jwt.getHeader().getAlgorithm(), keySource);

			ConfigurableJWTProcessor<SecurityContext> jwtProcessor =
				    new DefaultJWTProcessor<>();
			
			jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<SecurityContext>());
			
			jwtProcessor.setJWSKeySelector(keySelector);
						
			SecurityContext ctx = null; // optional context parameter, not required here
			JWTClaimsSet claimsSet = jwtProcessor.process(token, ctx);
			

			Map<String, Object> headers = new HashMap<>();
			headers.put("kid", jwt.getHeader().getKeyID());
			headers.put("alg", jwt.getHeader().getAlgorithm().getName());
			headers.put("typ", jwt.getHeader().getType().getType());
			headers.putAll(jwt.getHeader().getCustomParams());
			
			Map<String, Object> claims = claimsSet.getClaims();
			Date iat = claimsSet.getIssueTime();

			Jwt springJwt = new Jwt(token, iat.toInstant(), claimsSet.getExpirationTime().toInstant(), headers, claims);

			if (validator != null) {
				OAuth2TokenValidatorResult validationResult = validator.validate(springJwt);
				if (validationResult.hasErrors()) {
					String errorMessage = "";
					for (OAuth2Error error : validationResult.getErrors()) {
						errorMessage += error.getDescription() + "\n";
					}
					throw new JwtException(errorMessage);
				}
			}
			return springJwt;
		} catch (io.jsonwebtoken.JwtException | IllegalArgumentException | IOException | ParseException | BadJOSEException | JOSEException ex) {
			throw new JwtException(ex.getMessage());
		}

	}
}