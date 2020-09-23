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

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.text.DateFormatter;

import org.dpppt.malta.backend.sdk.authz.data.AuthzDataService;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCodesPage;
import org.dpppt.malta.backend.sdk.authz.ws.model.CovidCodeRequestModel;
import org.dpppt.malta.backend.sdk.authz.ws.model.CovidCodeResponseModel;
import org.dpppt.malta.backend.sdk.authz.ws.model.CovidCodesPageResponseModel;
import org.dpppt.malta.backend.sdk.authz.ws.util.CovidCodeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/v1")
public class CovidCodesController {

	private static final String REGEX_CODES_ORDER = "^(ASC|asc|DESC|desc){1}$";
	private static final String REGEX_CODES_SORT = "^[a-zA-Z0-9_]+$";
	private static final String REGEX_CODES_QUERY = "^[A-Z]{2}[0-9]{6}[A-Z]{1}$";
	private static final String CLAIM_USER_IDENTIFIER = "SamAccountName";
	private static final String CLAIM_USER_IDENTIFIER_FALLBACK = "name";

	@Value("${authz.covidcode.validity.days:1}")
	private int covidCodeValidityDays;
	
	private AuthzDataService covidCodesDataService;
	
	public CovidCodesController(AuthzDataService covidCodesDataService) {
		super();
		this.covidCodesDataService = covidCodesDataService;
	}

	@CrossOrigin(origins = { "*" })
	@GetMapping(value = "/codes", 
			produces="application/json")
	public @ResponseBody ResponseEntity<CovidCodesPageResponseModel> getCodes(
			@RequestParam(name="q", required=false, defaultValue="") String query,
			@RequestParam(name="all", required=false, defaultValue="N") String all,
			@RequestParam(name="start", required=false, defaultValue="0") int start,
			@RequestParam(name="size", required=false, defaultValue="0") int size,
			@RequestParam(name="sort", required=false, defaultValue="specimen_no") String sort,
			@RequestParam(name="order", required=false, defaultValue="ASC") String order,
			Authentication authentication) {
		
		if (!validateCodesInput(query, sort, order)) {
			return ResponseEntity.badRequest().build();
		}
		
		final CovidCodeMapper mapper = CovidCodeMapper.INSTANCE;
		CovidCodesPage page = covidCodesDataService.search(query, "Y".equals(all), start, size, sort, "DESC".equals(order));
		
		List<CovidCodeResponseModel> codes = page.getCovidCodes().stream()
				.map(c -> {
					return mapper.covidCodeToResponseModel(c);
				})
				.collect(Collectors.toList());
				
		
		return ResponseEntity.ok(new CovidCodesPageResponseModel(codes, page.getTotal()));
		
	}

	private boolean validateCodesInput(String query, String sort, String order) {
		if (!Pattern.compile(REGEX_CODES_QUERY).matcher(query).matches()) {
			return false;
		}
		if (null != sort && sort.length() > 0 && !Pattern.compile(REGEX_CODES_SORT).matcher(query).matches()) {
			return false;
		}
		if (null != order && order.length() > 0 && !Pattern.compile(REGEX_CODES_ORDER).matcher(query).matches()) {
			return false;
		}
		return true;
	}
	
	private String getUserIdentifier(Authentication authentication) {
		
		final Jwt token = ((JwtAuthenticationToken) authentication).getToken();
		
		String ident = token.getClaimAsString(CLAIM_USER_IDENTIFIER);
		if (null == ident) {
			ident = token.getClaimAsString(CLAIM_USER_IDENTIFIER_FALLBACK);
		}
		if (null == ident) {
			throw new IllegalStateException("User identifier could not be obtained from token");
		}
		return ident;
		
	}
	
	@CrossOrigin(origins = { "*" })
	@DeleteMapping(value = "/codes/revoked/{id}", 
			produces="application/json")
	public @ResponseBody ResponseEntity<CovidCodeResponseModel> revokeCode(@PathVariable int id,
			Authentication authentication) {
		
		final CovidCodeMapper mapper = CovidCodeMapper.INSTANCE;

		CovidCode cc = covidCodesDataService.get(id);
		if (cc.isClosed()) return ResponseEntity.badRequest().build();
		
		Instant reg = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC).toInstant();

		return ResponseEntity.ok(
				mapper.covidCodeToResponseModel(
						covidCodesDataService.updateRevoked(id, reg, getUserIdentifier(authentication))));
		
	}

	@CrossOrigin(origins = { "*" })
	@DeleteMapping(value = "/codes/redeemed/{authCode}", 
			produces="application/json")
	public @ResponseBody ResponseEntity<?> redeemCode(@PathVariable String authCode) {
		
		CovidCode cc = covidCodesDataService.fetchByAuthCode(authCode);
		if (null == cc) return ResponseEntity.notFound().build();
		if (cc.isRedeemed()) return ResponseEntity.noContent().build();
		if (cc.isClosed()) return ResponseEntity.badRequest().body("CovidCode is closed");

		Instant reg = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC).toInstant();
		covidCodesDataService.updateRedeemed(cc.getId(), reg);
		
		return ResponseEntity.noContent().build();
		
	}

	@CrossOrigin(origins = { "*" })
	@GetMapping(value = "/codes/{id}", 
			produces="application/json")
	public @ResponseBody ResponseEntity<CovidCodeResponseModel> getCode(@PathVariable int id) {
		
		final CovidCodeMapper mapper = CovidCodeMapper.INSTANCE;
		
		return ResponseEntity.ok(
				mapper.covidCodeToResponseModel(
						covidCodesDataService.get(id)));
		
	}
	
	@CrossOrigin(origins = { "*" })
	@PostMapping(value = "/codes", 
			consumes="application/json", 
			produces="application/json")
	public @ResponseBody ResponseEntity<?> registerCode(
			@RequestBody(required = true) CovidCodeRequestModel covidCode,
			Authentication authentication) {
		
		if (covidCodesDataService.specimenNumberExists(covidCode.getSpecimenNumber())) {
			return ResponseEntity.badRequest().body("Specimen number already exists");
		}
		
		CovidCode cc = new CovidCode();
		cc.setSpecimenNumber(covidCode.getSpecimenNumber());		
		cc.setReceiveDate(LocalDate.parse(covidCode.getReceiveDate()));		
		cc.setOnsetDate(LocalDate.parse(covidCode.getOnsetDate()));
		cc.setTransmissionRisk(covidCode.getTransmissionRisk());
		cc.setAuthorisationCode(generateAuthCode());
		Instant reg = OffsetDateTime.now().withOffsetSameInstant(ZoneOffset.UTC).toInstant();
		cc.setRegisteredAt(reg);
		cc.setExpiresAt(reg.plus(covidCodeValidityDays, ChronoUnit.DAYS));
		cc.setRegisteredBy(getUserIdentifier(authentication));		
				
		cc = covidCodesDataService.insertCovidCode(cc);
		
		CovidCodeMapper mapper = CovidCodeMapper.INSTANCE;
		return ResponseEntity.ok(mapper.covidCodeToResponseModel(cc));
		
	}
	
	private String generateAuthCode() {
		String res = "";
		
		do {
			res = CovidCodeUtils.generateRandom12DigitCode();
		} while (covidCodesDataService.authCodeExists(res));
		
		return res;
		
	}
	
}
