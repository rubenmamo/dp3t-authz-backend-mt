/*
 * Copyright (c) 2020 Malta Information Technology Agency <https://mita.gov.mt>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package org.dpppt.malta.backend.sdk.authz.ws.controller;

import java.time.LocalDate;

import org.dpppt.malta.backend.sdk.authz.data.AuthzDataService;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.ws.model.AuthenticationCodeRequestModel;
import org.dpppt.malta.backend.sdk.authz.ws.model.AuthenticationCodeResponseModel;
import org.dpppt.malta.backend.sdk.authz.ws.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/v1")
public class DPPPTAuthzController {

	private static final Logger logger = LoggerFactory.getLogger(DPPPTAuthzController.class);

	private JwtTokenProvider jwtTokenProvider;	
	private AuthzDataService covidCodesDataService;
	private boolean allowFakes;
	
	public DPPPTAuthzController(JwtTokenProvider jwtTokenProvider, AuthzDataService covidCodesDataService, boolean allowFakes) {
		super();
		this.jwtTokenProvider = jwtTokenProvider;
		this.covidCodesDataService = covidCodesDataService;
		this.allowFakes = allowFakes;
	}

	//@CrossOrigin(origins = { "https://editor.swagger.io" })
	@CrossOrigin(origins = { "*" })
	@GetMapping(value = "")
	public @ResponseBody String hello() {
		return "Hello from DP3T Authz WS";
	}

	@CrossOrigin(origins = { "*" })
	@PostMapping(value = "/onset", 
	consumes="application/json", 
	produces="application/json")
	public @ResponseBody ResponseEntity<AuthenticationCodeResponseModel> getAuthCode(@RequestBody(required = true) AuthenticationCodeRequestModel codeRequest) {
		
		if (!allowFakes && codeRequest.getFake() == 1) {
			return ResponseEntity.badRequest().build();
		}
		
		CovidCode covidCode = null;
		if (codeRequest.getFake() != 1) {
			covidCode = covidCodesDataService.fetchByAuthCode(codeRequest.getAuthorizationCode());
			
			if (covidCode == null || covidCode.isClosed()) {
				return ResponseEntity.notFound().build();
			}
			
		} else {
			covidCode = new CovidCode();
			covidCode.setAuthorisationCode(codeRequest.getAuthorizationCode());		
			covidCode.setTransmissionRisk("0");
			covidCode.setOnsetDate(LocalDate.now().minusDays(14));
		}		
		
		CreateTokenResult result = jwtTokenProvider.createToken(codeRequest, covidCode);
		
		if (codeRequest.getFake() != 1) {
			covidCodesDataService.insertTokenIssueLog(covidCode, result.getIssuedAt(), result.getUuid());
		}
		
		return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(new AuthenticationCodeResponseModel(result.getToken()));
	}

}
