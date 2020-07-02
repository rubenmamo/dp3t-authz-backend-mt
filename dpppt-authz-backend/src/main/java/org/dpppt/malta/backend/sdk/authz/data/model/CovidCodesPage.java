package org.dpppt.malta.backend.sdk.authz.data.model;

import java.util.List;

public class CovidCodesPage {

	private int total;
	private List<CovidCode> covidCodes;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<CovidCode> getCovidCodes() {
		return covidCodes;
	}
	public void setCovidCodes(List<CovidCode> covidCodes) {
		this.covidCodes = covidCodes;
	}
}
