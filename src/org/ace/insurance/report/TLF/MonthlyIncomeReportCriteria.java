package org.ace.insurance.report.TLF;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.web.common.SaleChannelType;

public class MonthlyIncomeReportCriteria {

	private String salePointId;
	private String salePointName;
	
	private Date startDate;
	private Date endDate;
	private String branchName;
	private String branchId;
	private boolean isIncludeAgent;
	private int requiredYear;
	private int requiredMonth;
	private int periodOfYears;
	
	@Enumerated(EnumType.STRING)
	private SaleChannelType saleChannelType;
	
	
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGENTID", referencedColumnName = "ID")
	private Agent agent;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALEBANKID", referencedColumnName = "ID")
	private BankBranch saleBank;
	

	public String getSalePointName() {
		return salePointName;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
		if (salePointName == null || salePointName.isEmpty()) {
			this.salePointName = null;
			this.salePointId = null;
		}
	}

	

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public boolean isIncludeAgent() {
		return isIncludeAgent;
	}

	public void setIncludeAgent(boolean isIncludeAgent) {
		this.isIncludeAgent = isIncludeAgent;
	}

	public int getRequiredYear() {
		return requiredYear;
	}

	public void setRequiredYear(int requiredYear) {
		this.requiredYear = requiredYear;
	}

	public int getRequiredMonth() {
		return requiredMonth;
	}

	public void setRequiredMonth(int requiredMonth) {
		this.requiredMonth = requiredMonth;
	}

	public int getPeriodOfYears() {
		return periodOfYears;
	}

	public void setPeriodOfYears(int periodOfYears) {
		this.periodOfYears = periodOfYears;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public BankBranch getSaleBank() {
		return saleBank;
	}

	public void setSaleBank(BankBranch saleBank) {
		this.saleBank = saleBank;
	}

}
