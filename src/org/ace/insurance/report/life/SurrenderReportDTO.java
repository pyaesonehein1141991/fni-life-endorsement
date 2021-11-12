package org.ace.insurance.report.life;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class SurrenderReportDTO {

	private String insruedPersonName;
	private String policyNo;
	private String fromDateToDate;
	private String paymentType;
	private double sumInsured;
	private double basictermPremium;
	private double surrenderAmount;
	private String agentName;
	private int policyTerm;
	private int age;
	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedDate;
	private String salepoint;
	private int dueNo;
	private String receiptNo;

	public SurrenderReportDTO() {
	}

	public SurrenderReportDTO(String insruedPersonName, String policyNo, String fromDateToDate, String paymentType, double sumInsured, double basictermPremium,
			double surrenderAmount, String agentName, int policyTerm, int age, Date submittedDate, String salepoint, int dueNo, String receiptNo) {
		this.insruedPersonName = insruedPersonName;
		this.policyNo = policyNo;
		this.fromDateToDate = fromDateToDate;
		this.paymentType = paymentType;
		this.sumInsured = sumInsured;
		this.basictermPremium = basictermPremium;
		this.surrenderAmount = surrenderAmount;
		this.agentName = agentName;
		this.policyTerm = policyTerm;
		this.age = age;
		this.submittedDate = submittedDate;
		this.salepoint = salepoint;
		this.dueNo = dueNo;

	}

	public String getInsruedPersonName() {
		return insruedPersonName;
	}

	public void setInsruedPersonName(String insruedPersonName) {
		this.insruedPersonName = insruedPersonName;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getFromDateToDate() {
		return fromDateToDate;
	}

	public void setFromDateToDate(String fromDateToDate) {
		this.fromDateToDate = fromDateToDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public double getBasictermPremium() {
		return basictermPremium;
	}

	public void setBasictermPremium(double basictermPremium) {
		this.basictermPremium = basictermPremium;
	}

	public double getSurrenderAmount() {
		return surrenderAmount;
	}

	public void setSurrenderAmount(double surrenderAmount) {
		this.surrenderAmount = surrenderAmount;
	}

	public int getDueNo() {
		return dueNo;
	}

	public void setDueNo(int dueNo) {
		this.dueNo = dueNo;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public int getPolicyTerm() {
		return policyTerm;
	}

	public void setPolicyTerm(int policyTerm) {
		this.policyTerm = policyTerm;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	public String getSalepoint() {
		return salepoint;
	}

	public void setSalepoint(String salepoint) {
		this.salepoint = salepoint;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

}
