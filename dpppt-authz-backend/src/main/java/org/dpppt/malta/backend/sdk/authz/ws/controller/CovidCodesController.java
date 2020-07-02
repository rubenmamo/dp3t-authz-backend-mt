package org.dpppt.malta.backend.sdk.authz.ws.controller;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.text.DateFormatter;

import org.dpppt.malta.backend.sdk.authz.data.AuthzDataService;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCodesPage;
import org.dpppt.malta.backend.sdk.authz.ws.model.CovidCodeRequestModel;
import org.dpppt.malta.backend.sdk.authz.ws.model.CovidCodeResponseModel;
import org.dpppt.malta.backend.sdk.authz.ws.model.CovidCodesPageResponseModel;
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
		
		final CovidCodeMapper mapper = CovidCodeMapper.INSTANCE;
		CovidCodesPage page = covidCodesDataService.search(query, "Y".equals(all), start, size, sort, "DESC".equals(order));
		
		List<CovidCodeResponseModel> codes = page.getCovidCodes().stream()
				.map(c -> {
					return mapper.covidCodeToResponseModel(c);
				})
				.collect(Collectors.toList());
				
		
		return ResponseEntity.ok(new CovidCodesPageResponseModel(codes, page.getTotal()));
		
	}
	
	@CrossOrigin(origins = { "*" })
	@DeleteMapping(value = "/codes/revoked/{id}", 
			produces="application/json")
	public @ResponseBody ResponseEntity<CovidCodeResponseModel> revokeCode(@PathVariable int id,
			Authentication authentication) {
		
		final Jwt token = ((JwtAuthenticationToken) authentication).getToken();
		
		final CovidCodeMapper mapper = CovidCodeMapper.INSTANCE;

		CovidCode cc = covidCodesDataService.get(id);
		if (cc.isClosed()) return ResponseEntity.badRequest().build();
		
		return ResponseEntity.ok(
				mapper.covidCodeToResponseModel(
						covidCodesDataService.updateRevoked(id, Instant.now(), token.getClaimAsString("SamAccountName"))));
		
	}

	@CrossOrigin(origins = { "*" })
	@DeleteMapping(value = "/codes/redeemed/{authCode}", 
			produces="application/json")
	public @ResponseBody ResponseEntity<?> redeemCode(@PathVariable String authCode) {
		
		CovidCode cc = covidCodesDataService.fetchByAuthCode(authCode);
		if (null == cc || cc.isClosed()) return ResponseEntity.badRequest().body("CovidCode does not exist or is closed");

		covidCodesDataService.updateRedeemed(cc.getId(), Instant.now());
		
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
		
		final Jwt token = ((JwtAuthenticationToken) authentication).getToken();
		
		if (covidCodesDataService.specimenNumberExists(covidCode.getSpecimenNumber())) {
			return ResponseEntity.badRequest().body("Specimen number already exists");
		}
		
		CovidCode cc = new CovidCode();
		cc.setSpecimenNumber(covidCode.getSpecimenNumber());		
		cc.setReceiveDate(LocalDate.parse(covidCode.getReceiveDate()));		
		cc.setOnsetDate(LocalDate.parse(covidCode.getOnsetDate()));
		cc.setTransmissionRisk(covidCode.getTransmissionRisk());
		cc.setAuthorisationCode(generateAuthCode());
		Instant reg = Instant.now();		
		cc.setRegisteredAt(reg);
		cc.setExpiresAt(reg.plus(1, ChronoUnit.DAYS));
		cc.setRegisteredBy(token.getClaimAsString("SamAccountName"));		
				
		cc = covidCodesDataService.insertCovidCode(cc);
		
		CovidCodeMapper mapper = CovidCodeMapper.INSTANCE;
		return ResponseEntity.ok(mapper.covidCodeToResponseModel(cc));
		
	}
	
	private String generateAuthCode() {
		SecureRandom rand = new SecureRandom();
		String res = "";
		
		do {
			StringBuilder authCode = new StringBuilder();
			for (int i = 0; i < 4; i++) {
				authCode.append(String.format("%03d", rand.nextInt(1000)));
			}
			res = authCode.toString();
		} while (covidCodesDataService.authCodeExists(res));
		
		return res;
		
	}
	
}
