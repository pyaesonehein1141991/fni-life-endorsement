package org.ace.insurance.travel;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_FNI_SPECIALTRAVEL_DAILY_RECEIPT_REPORT)
@ReadOnly
public class SpecialTravelDailyReceiptReport {
	@Id
	private String id;
	private Date receiptDate;
	private String customerName;
	private String agent;
	private String branchId;
	private String branchname;
	private String paymentBranchId;
	private String paymentBranchName;
	private String proposalNo;
	private String receiptNo;
	private double suminsured;
	private double premium;
	private boolean isMMK;

	public SpecialTravelDailyReceiptReport() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public double getSuminsured() {
		return suminsured;
	}

	public void setSuminsured(double suminsured) {
		this.suminsured = suminsured;
	}

	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public String getPaymentBranchId() {
		return paymentBranchId;
	}

	public void setPaymentBranchId(String paymentBranchId) {
		this.paymentBranchId = paymentBranchId;
	}

	public String getPaymentBranchName() {
		return paymentBranchName;
	}

	public void setPaymentBranchName(String paymentBranchName) {
		this.paymentBranchName = paymentBranchName;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public boolean isMMK() {
		return isMMK;
	}

	public void setMMK(boolean isMMK) {
		this.isMMK = isMMK;
	}

}
