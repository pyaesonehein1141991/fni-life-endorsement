package org.ace.insurance.proxy;

import java.io.Serializable;
import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.Name;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.interfaces.IDataModel;


public class TRA001 implements Serializable, ISorter, IDataModel {
	private static final long serialVersionUID = 1L;

	private String id;
	private String proposalNo;
	private String customerName;
	private Date submittedDate;
	private Date pendingSince;
	private double totalUnit;
	private double totalPremium;
	private double sumInsured;
	
	private String branchName;
	private double unit;

	public TRA001() {
	}

	/** TRAVEL dashboard */
	public TRA001(String id, String proposalNo, String initialId, Name cutomerName, String organizationName, Date submittedDate, double totalUnit, double totalPremium,
			Date pendingSince, String branchName) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.customerName = initialId != null && !initialId.isEmpty() && cutomerName != null ? Utils.getCompleteName(initialId, cutomerName) : organizationName;
		this.submittedDate = submittedDate;
		this.unit = totalUnit;
		this.sumInsured = totalPremium;
		this.pendingSince = pendingSince;
		this.branchName = branchName;
	}
	
	public TRA001(String id, String proposalNo, String initialId, Name cutomerName, String organizationName, Date submittedDate, double totalUnit, double totalPremium,
			Date pendingSince) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.customerName = initialId != null && !initialId.isEmpty() && cutomerName != null ? Utils.getCompleteName(initialId, cutomerName) : organizationName;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.unit = totalUnit;
		this.sumInsured = totalPremium;
	}

	public TRA001(String id, String proposalNo, String customerName, Date submittedDate, Date pendingSince, int totalUnit, double totalPremium) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.customerName = customerName;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.unit = totalUnit;
		this.sumInsured = totalPremium;
	}

	
	/** SPECIAL_TRAVEL dashboard */
	public TRA001(String id, String proposalNo, Date submittedDate, Date pendingSince, double totalUnit, String branchName) {
		this.id = id;
		this.proposalNo = proposalNo;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.unit = totalUnit;
		this.branchName = branchName;
	}
	
	public TRA001(String id, String proposalNo, Date submittedDate, Date pendingSince, double totalUnit) {
		this.id = id;
		this.proposalNo = proposalNo;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
		this.unit = totalUnit;
	}

	public TRA001(String id, String proposalNo, Date submittedDate, Date pendingSince) {
		this.id = id;
		this.proposalNo = proposalNo;
		this.submittedDate = submittedDate;
		this.pendingSince = pendingSince;
	}


		public TRA001(String id, String proposalNo, String customerName, Date submittedDate, Date pendingSince, double unit,String branchName) {
			super();
			this.id = id;
			this.proposalNo = proposalNo;
			this.customerName = customerName;
			this.submittedDate = submittedDate;
			this.pendingSince = pendingSince;
			this.unit = unit;
			this.branchName=branchName;
		
	}

	public String getId() {
		return id;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public Date getPendingSince() {
		return pendingSince;
	}

	/**
	 * @return the totalUnit
	 */

	
	@Override
	public String getRegistrationNo() {
		return proposalNo;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public double getUnit() {
		return unit;
	}

	public void setUnit(double unit) {
		this.unit = unit;
	}

	public void setTotalUnit(double totalUnit) {
		this.totalUnit = totalUnit;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public double getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(double totalPremium) {
		this.totalPremium = totalPremium;
	}

	public double getTotalUnit() {
		return totalUnit;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public void setPendingSince(Date pendingSince) {
		this.pendingSince = pendingSince;
	}
	
}
