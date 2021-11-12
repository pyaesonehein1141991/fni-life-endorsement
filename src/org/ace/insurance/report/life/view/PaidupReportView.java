package org.ace.insurance.report.life.view;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_PAIDUPREPORT)
@ReadOnly
public class PaidupReportView {

	@Id
	private String id;
	private String insruedPersonName;
	private String policyNo;
	private String fromDateToDate;
	private String paymentType;
	private double sumInsured;
	private double basictermPremium;
	private double realPaidupAmount;
	private String agentName;
	private int policyTerm;
	private int age;
	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedDate;
	private String salepoint;

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

	public double getRealPaidupAmount() {
		return realPaidupAmount;
	}

	public void setRealPaidupAmount(double realPaidupAmount) {
		this.realPaidupAmount = realPaidupAmount;
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

}
