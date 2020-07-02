package org.dpppt.malta.backend.sdk.authz.ws.model;

public class CovidCodeRequestModel {

	private String specimenNumber;
	private String receiveDate;	
	private String onsetDate;
	private String transmissionRisk;
	
	public String getSpecimenNumber() {
		return specimenNumber;
	}
	public void setSpecimenNumber(String specimenNumber) {
		this.specimenNumber = specimenNumber;
	}
	public String getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(String receiveDate) {
		this.receiveDate = receiveDate;
	}
	public String getOnsetDate() {
		return onsetDate;
	}
	public void setOnsetDate(String onsetDate) {
		this.onsetDate = onsetDate;
	}
	public String getTransmissionRisk() {
		return transmissionRisk;
	}
	public void setTransmissionRisk(String transmissionRisk) {
		this.transmissionRisk = transmissionRisk;
	}

	
}
