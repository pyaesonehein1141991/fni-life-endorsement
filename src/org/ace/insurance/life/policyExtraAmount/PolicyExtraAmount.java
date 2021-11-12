package org.ace.insurance.life.policyExtraAmount;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.life.policy.LifePolicyAttachment;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.bankBranch.BankBranch;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.salesPoints.SalesPoints;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.POLICYEXTRAAMOUNT)
@TableGenerator(name = "POLICYEXTRAAMOUNT_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "POLICYEXTRAAMOUNT_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class PolicyExtraAmount implements Serializable {
	private static final long serialVersionUID = -7883787646075278917L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "POLICYEXTRAAMOUNT_GEN")
	private String id;

	private boolean isPaid;
	private int lastPaymentTerm;
	@Column(name = "PERIODOFMONTH")
	private int periodMonth;
	private String policyNo;
	private String endorsementNo;
	private String lifeProposalNo;
	private double extraAmount;

	/* Underwriting payment date */
	@Temporal(TemporalType.TIMESTAMP)
	private Date commenmanceDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVEDPOLICYSTARTDATE")
	private Date activedPolicyStartDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTIVEDPOLICYENDDATE")
	private Date activedPolicyEndDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "COVERAGEDATE")
	private Date coverageDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ISSUEDATE")
	private Date issueDate;

	@Column(name = "ENDORSEMENTCONFIRMDATE")
	@Temporal(TemporalType.DATE)
	private Date endorsementConfirmDate;

	@Enumerated(EnumType.STRING)
	private PolicyStatus policyStatus;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID")
	private Customer customer;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORGANIZATIONID", referencedColumnName = "ID")
	private Organization organization;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;


	@Embedded
	private UserRecorder recorder;
	@Version
	private int version;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isPaid() {
		return isPaid;
	}
	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
	public int getLastPaymentTerm() {
		return lastPaymentTerm;
	}
	public void setLastPaymentTerm(int lastPaymentTerm) {
		this.lastPaymentTerm = lastPaymentTerm;
	}
	public int getPeriodMonth() {
		return periodMonth;
	}
	public void setPeriodMonth(int periodMonth) {
		this.periodMonth = periodMonth;
	}
	public String getPolicyNo() {
		return policyNo;
	}
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}
	public String getEndorsementNo() {
		return endorsementNo;
	}
	public void setEndorsementNo(String endorsementNo) {
		this.endorsementNo = endorsementNo;
	}
	public String getLifeProposalNo() {
		return lifeProposalNo;
	}
	public void setLifeProposalNo(String lifeProposalNo) {
		this.lifeProposalNo = lifeProposalNo;
	}
	public double getExtraAmount() {
		return extraAmount;
	}
	public void setExtraAmount(double extraAmount) {
		this.extraAmount = extraAmount;
	}
	public Date getCommenmanceDate() {
		return commenmanceDate;
	}
	public void setCommenmanceDate(Date commenmanceDate) {
		this.commenmanceDate = commenmanceDate;
	}
	public Date getActivedPolicyStartDate() {
		return activedPolicyStartDate;
	}
	public void setActivedPolicyStartDate(Date activedPolicyStartDate) {
		this.activedPolicyStartDate = activedPolicyStartDate;
	}
	public Date getActivedPolicyEndDate() {
		return activedPolicyEndDate;
	}
	public void setActivedPolicyEndDate(Date activedPolicyEndDate) {
		this.activedPolicyEndDate = activedPolicyEndDate;
	}
	public Date getCoverageDate() {
		return coverageDate;
	}
	public void setCoverageDate(Date coverageDate) {
		this.coverageDate = coverageDate;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public Date getEndorsementConfirmDate() {
		return endorsementConfirmDate;
	}
	public void setEndorsementConfirmDate(Date endorsementConfirmDate) {
		this.endorsementConfirmDate = endorsementConfirmDate;
	}
	public PolicyStatus getPolicyStatus() {
		return policyStatus;
	}
	public void setPolicyStatus(PolicyStatus policyStatus) {
		this.policyStatus = policyStatus;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public Branch getBranch() {
		return branch;
	}
	public void setBranch(Branch branch) {
		this.branch = branch;
	}
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public UserRecorder getRecorder() {
		return recorder;
	}
	public void setRecorder(UserRecorder recorder) {
		this.recorder = recorder;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activedPolicyEndDate == null) ? 0 : activedPolicyEndDate.hashCode());
		result = prime * result + ((activedPolicyStartDate == null) ? 0 : activedPolicyStartDate.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((commenmanceDate == null) ? 0 : commenmanceDate.hashCode());
		result = prime * result + ((coverageDate == null) ? 0 : coverageDate.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((endorsementConfirmDate == null) ? 0 : endorsementConfirmDate.hashCode());
		result = prime * result + ((endorsementNo == null) ? 0 : endorsementNo.hashCode());
		long temp;
		temp = Double.doubleToLongBits(extraAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isPaid ? 1231 : 1237);
		result = prime * result + ((issueDate == null) ? 0 : issueDate.hashCode());
		result = prime * result + lastPaymentTerm;
		result = prime * result + ((lifeProposalNo == null) ? 0 : lifeProposalNo.hashCode());
		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + periodMonth;
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		result = prime * result + ((policyStatus == null) ? 0 : policyStatus.hashCode());
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		result = prime * result + version;
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
		PolicyExtraAmount other = (PolicyExtraAmount) obj;
		if (activedPolicyEndDate == null) {
			if (other.activedPolicyEndDate != null)
				return false;
		} else if (!activedPolicyEndDate.equals(other.activedPolicyEndDate))
			return false;
		if (activedPolicyStartDate == null) {
			if (other.activedPolicyStartDate != null)
				return false;
		} else if (!activedPolicyStartDate.equals(other.activedPolicyStartDate))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (commenmanceDate == null) {
			if (other.commenmanceDate != null)
				return false;
		} else if (!commenmanceDate.equals(other.commenmanceDate))
			return false;
		if (coverageDate == null) {
			if (other.coverageDate != null)
				return false;
		} else if (!coverageDate.equals(other.coverageDate))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (endorsementConfirmDate == null) {
			if (other.endorsementConfirmDate != null)
				return false;
		} else if (!endorsementConfirmDate.equals(other.endorsementConfirmDate))
			return false;
		if (endorsementNo == null) {
			if (other.endorsementNo != null)
				return false;
		} else if (!endorsementNo.equals(other.endorsementNo))
			return false;
		if (Double.doubleToLongBits(extraAmount) != Double.doubleToLongBits(other.extraAmount))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isPaid != other.isPaid)
			return false;
		if (issueDate == null) {
			if (other.issueDate != null)
				return false;
		} else if (!issueDate.equals(other.issueDate))
			return false;
		if (lastPaymentTerm != other.lastPaymentTerm)
			return false;
		if (lifeProposalNo == null) {
			if (other.lifeProposalNo != null)
				return false;
		} else if (!lifeProposalNo.equals(other.lifeProposalNo))
			return false;
		if (organization == null) {
			if (other.organization != null)
				return false;
		} else if (!organization.equals(other.organization))
			return false;
		if (paymentType == null) {
			if (other.paymentType != null)
				return false;
		} else if (!paymentType.equals(other.paymentType))
			return false;
		if (periodMonth != other.periodMonth)
			return false;
		if (policyNo == null) {
			if (other.policyNo != null)
				return false;
		} else if (!policyNo.equals(other.policyNo))
			return false;
		if (policyStatus != other.policyStatus)
			return false;
		if (recorder == null) {
			if (other.recorder != null)
				return false;
		} else if (!recorder.equals(other.recorder))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
	
}
