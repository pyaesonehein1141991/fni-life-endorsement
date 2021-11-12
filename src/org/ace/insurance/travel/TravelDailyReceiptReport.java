package org.ace.insurance.travel;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_FNI_TRAVEL_DAILY_RECEIPT_REPORT)
@ReadOnly
public class TravelDailyReceiptReport {
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
	private double stampFees;
	private boolean isMMK;

	public TravelDailyReceiptReport() {

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

	public double getStampFees() {
		return stampFees;
	}

	public void setStampFees(double stampFees) {
		this.stampFees = stampFees;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((branchId == null) ? 0 : branchId.hashCode());
		result = prime * result + ((branchname == null) ? 0 : branchname.hashCode());
		result = prime * result + ((customerName == null) ? 0 : customerName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isMMK ? 1231 : 1237);
		result = prime * result + ((paymentBranchId == null) ? 0 : paymentBranchId.hashCode());
		result = prime * result + ((paymentBranchName == null) ? 0 : paymentBranchName.hashCode());
		long temp;
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((receiptDate == null) ? 0 : receiptDate.hashCode());
		result = prime * result + ((receiptNo == null) ? 0 : receiptNo.hashCode());
		temp = Double.doubleToLongBits(stampFees);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(suminsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TravelDailyReceiptReport other = (TravelDailyReceiptReport) obj;
		if (agent == null) {
			if (other.agent != null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		if (branchId == null) {
			if (other.branchId != null)
				return false;
		} else if (!branchId.equals(other.branchId))
			return false;
		if (branchname == null) {
			if (other.branchname != null)
				return false;
		} else if (!branchname.equals(other.branchname))
			return false;
		if (customerName == null) {
			if (other.customerName != null)
				return false;
		} else if (!customerName.equals(other.customerName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isMMK != other.isMMK)
			return false;
		if (paymentBranchId == null) {
			if (other.paymentBranchId != null)
				return false;
		} else if (!paymentBranchId.equals(other.paymentBranchId))
			return false;
		if (paymentBranchName == null) {
			if (other.paymentBranchName != null)
				return false;
		} else if (!paymentBranchName.equals(other.paymentBranchName))
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
			return false;
		if (receiptDate == null) {
			if (other.receiptDate != null)
				return false;
		} else if (!receiptDate.equals(other.receiptDate))
			return false;
		if (receiptNo == null) {
			if (other.receiptNo != null)
				return false;
		} else if (!receiptNo.equals(other.receiptNo))
			return false;
		if (Double.doubleToLongBits(stampFees) != Double.doubleToLongBits(other.stampFees))
			return false;
		if (Double.doubleToLongBits(suminsured) != Double.doubleToLongBits(other.suminsured))
			return false;
		return true;
	}

}
