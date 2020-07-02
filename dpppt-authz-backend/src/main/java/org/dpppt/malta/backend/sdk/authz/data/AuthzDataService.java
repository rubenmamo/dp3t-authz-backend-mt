package org.dpppt.malta.backend.sdk.authz.data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.dpppt.malta.backend.sdk.authz.data.model.CovidCode;
import org.dpppt.malta.backend.sdk.authz.data.model.CovidCodesPage;

public interface AuthzDataService {

	
	public CovidCode insertCovidCode(CovidCode covidCode);
	
	public CovidCode updateCovidCode(CovidCode covidCode);
	
	public CovidCode get(int id);
	
	public CovidCode fetchByAuthCode(String authCode);
	
	public CovidCode updateRevoked(int id, Instant at, String by);
	
	public List<CovidCode> getAll(int start, int size, String sort, boolean desc);
	
	public CovidCodesPage search(String query, boolean all, int start, int size, String sort, boolean desc);
	
	public List<CovidCode> fetchBySpecimenNumber(String specimen_no, int start, int size, String sort, boolean desc);
	
	public boolean authCodeExists(String authCode);

	public boolean specimenNumberExists(String specimenNumber);
	
	void updateRedeemed(int id, Instant at);
	
	public int insertTokenIssueLog(CovidCode covidCode, Instant issuedAt, UUID uuid);
	
}
