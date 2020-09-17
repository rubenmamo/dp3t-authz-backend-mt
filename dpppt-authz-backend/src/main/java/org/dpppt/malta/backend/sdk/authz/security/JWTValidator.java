/*
 * Copyright (c) 2020 Malta Information Technology Agency <https://mita.gov.mt>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.security;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;


public class JWTValidator implements OAuth2TokenValidator<Jwt> {

    private static final String ACCOUNT_NAME_CLAIM = "SamAccountName";
	public static final String AUD_CLAIM = "aud";


    private Duration maxJwtValidity;
    private String audience;
    private List<String> allowlist;

    public JWTValidator(Duration maxJwtValidity, String audience, List<String> allowlist) {
        this.maxJwtValidity = maxJwtValidity;
        this.audience = audience;
        this.allowlist = allowlist;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
    	if (!(allowlist.contains(token.getClaimAsString(ACCOUNT_NAME_CLAIM)) || allowlist.contains("ALL"))) {
    		return OAuth2TokenValidatorResult.failure(new OAuth2Error(OAuth2ErrorCodes.ACCESS_DENIED));
    	}
        if (token.getExpiresAt() == null || Instant.now().plus(maxJwtValidity).isBefore(token.getExpiresAt())) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST));
        }
        if(token.containsClaim(AUD_CLAIM) && token.getClaimAsStringList(AUD_CLAIM).contains(audience)) {
            return OAuth2TokenValidatorResult.success();
        }
        return OAuth2TokenValidatorResult.failure(new OAuth2Error(OAuth2ErrorCodes.INVALID_SCOPE));
    }

}