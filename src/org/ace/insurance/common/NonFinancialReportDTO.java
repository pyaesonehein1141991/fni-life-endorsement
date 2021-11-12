package org.ace.insurance.common;

import java.text.SimpleDateFormat;

public class NonFinancialReportDTO {

	private String proposalNo;

	private String policyNo;

	private String customerId;

	private String endorsementDate;
	
	private String oldInsuranceName;

	private String newInsuranceName;

	private String oldNRC;

	private String newNRC;

	private String oldAddress;

	private String newAddress;

	private String oldTownShip;

	private String newTownShip;

	private String startDate;

	private String endDate;

	public NonFinancialReportDTO() {
		
	}

	public NonFinancialReportDTO(Object[] obj) {
		this.endorsementDate = formattedDate(obj[0]);
		this.policyNo = obj[1].toString();
		this.customerId = obj[2].toString();
		this.proposalNo = obj[3].toString();
		this.startDate = formattedDate(obj[4]);
		this.endDate = formattedDate(obj[5]);
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getEndorsementDate() {
		return endorsementDate;
	}

	public void setEndorsementDate(String endorsementDate) {
		this.endorsementDate = endorsementDate;
	}

	public String getOldInsuranceName() {
		return oldInsuranceName;
	}

	public void setOldInsuranceName(String oldInsuranceName) {
		this.oldInsuranceName = oldInsuranceName;
	}

	public String getNewInsuranceName() {
		return newInsuranceName;
	}

	public void setNewInsuranceName(String newInsuranceName) {
		this.newInsuranceName = newInsuranceName;
	}

	public String getOldNRC() {
		return oldNRC;
	}

	public void setOldNRC(String oldNRC) {
		this.oldNRC = oldNRC;
	}

	public String getNewNRC() {
		return newNRC;
	}

	public void setNewNRC(String newNRC) {
		this.newNRC = newNRC;
	}

	public String getOldAddress() {
		return oldAddress;
	}

	public void setOldAddress(String oldAddress) {
		this.oldAddress = oldAddress;
	}

	public String getNewAddress() {
		return newAddress;
	}

	public void setNewAddress(String newAddress) {
		this.newAddress = newAddress;
	}

	public String getOldTownShip() {
		return oldTownShip;
	}

	public void setOldTownShip(String oldTownShip) {
		this.oldTownShip = oldTownShip;
	}

	public String getNewTownShip() {
		return newTownShip;
	}

	public void setNewTownShip(String newTownShip) {
		this.newTownShip = newTownShip;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public static String formattedDate(Object obj) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(obj);
	}
}
