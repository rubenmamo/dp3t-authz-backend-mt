package org.dpppt.malta.backend.sdk.authz.ws.model;

import java.util.List;

public class CovidCodesPageResponseModel {

	private List<CovidCodeResponseModel> covidCodes;
	private int total;
	
	
	public CovidCodesPageResponseModel(List<CovidCodeResponseModel> covidCodes, int total) {
		super();
		this.covidCodes = covidCodes;
		this.total = total;
	}
	
	public List<CovidCodeResponseModel> getCovidCodes() {
		return covidCodes;
	}
	public int getTotal() {
		return total;
	}
	
}
