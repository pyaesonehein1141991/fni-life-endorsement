package org.ace.insurance.travel.expressTravel;

import java.util.Date;
import java.util.List;

import org.ace.insurance.common.ISorter;

public class TPL001 implements ISorter {
	private static final long serialVersionUID = 1L;
	private String id;
	private String proposalNo;
	private String policyNo;
	private String branch;
	private int noOfPassenger;
	private double totalUnit;
	private double premium;
	private Date submittedDate;
	private List<TravelExpress> expressList;
	

	public TPL001() {
		super();
	}

	public TPL001(String id, String proposalNo, String policyNo, String branch, int noOfPassenger, double totalUnit, double premium, Date submittedDate) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.policyNo = policyNo;
		this.branch = branch;
		this.noOfPassenger = noOfPassenger;
		this.totalUnit = totalUnit;
		this.premium = premium;
		this.submittedDate = submittedDate;
	}

	public TPL001(String id, String proposalNo, String branch, int noOfPassenger, double totalUnit, double premium, Date submittedDate) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.branch = branch;
		this.noOfPassenger = noOfPassenger;
		this.totalUnit = totalUnit;
		this.premium = premium;
		this.submittedDate = submittedDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getTotalUnit() {
		return totalUnit;
	}

	public void setTotalUnit(double totalUnit) {
		this.totalUnit = totalUnit;
	}

	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	public void setNoOfPassenger(int noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	@Override
	public String getRegistrationNo() {
		return proposalNo;
	}

	public List<TravelExpress> getExpressList() {
		return expressList;
	}

	public void setExpressList(List<TravelExpress> expressList) {
		this.expressList = expressList;
	}

	

}
