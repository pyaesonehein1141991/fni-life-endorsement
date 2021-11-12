package org.ace.insurance.travel.personTravel.proposal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProposalStatus;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.SetUpIDConfig;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.interfaces.IInsuredItem;
import org.ace.insurance.common.interfaces.IPolicy;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.ProductGroup;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.salesPoints.SalesPoints;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PERSONTRAVEL_PROPOSAL)
@TableGenerator(name = "PERSON_TRAVELPROPOSAL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PERSON_TRAVELPROPOSAL_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PersonTravelProposal.findAll", query = "SELECT p FROM PersonTravelProposal p"),
		@NamedQuery(name = "PersonTravelProposal.updateCompleteStatus", query = "UPDATE PersonTravelProposal p SET p.complete = :complete WHERE p.id = :id") })
@EntityListeners(IDInterceptor.class)
public class PersonTravelProposal implements Serializable, IPolicy {

	private static final long serialVersionUID = -649078564286041008L;
	private boolean complete;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PERSON_TRAVELPROPOSAL_GEN")
	private String id;
	private String proposalNo;
	private double specialDiscount;

	@Temporal(TemporalType.DATE)
	private Date submittedDate;

	@Enumerated(EnumType.STRING)
	private SaleChannelType saleChannelType;

	@Enumerated(EnumType.STRING)
	private ProposalStatus proposalStatus;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGENTID", referencedColumnName = "ID")
	private Agent agent;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID")
	private Customer customer;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORGANIZATIONID", referencedColumnName = "ID")
	private Organization organization;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CURRENCYID", referencedColumnName = "ID")
	private Currency currency;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PERSONTRAVELINFOID", referencedColumnName = "ID")
	private PersonTravelProposalInfo personTravelInfo;

	@Embedded
	private UserRecorder recorder;

	@Version
	private int version;

	public PersonTravelProposal() {
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
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

	public Date getSubmittedDate() {
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
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

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public ProposalStatus getProposalStatus() {
		return proposalStatus;
	}

	public void setProposalStatus(ProposalStatus proposalStatus) {
		this.proposalStatus = proposalStatus;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getCustomerName() {
		if (customer != null) {
			return customer.getFullName();
		}
		if (organization != null) {
			return organization.getName();
		}
		return null;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

	public String getPhoneNo() {
		if (customer != null) {
			return customer.getContentInfo().getPhone();
		}
		if (organization != null) {
			return organization.getContentInfo().getPhone();
		}
		return null;
	}

	public String getCustomerId() {
		if (customer != null) {
			return customer.getId();
		}
		if (organization != null) {
			return organization.getId();
		}
		return null;
	}

	public String getCustomerAddress() {
		if (customer != null) {
			return customer.getFullAddress();
		}
		if (organization != null) {
			return organization.getFullAddress();
		}
		return null;
	}

	public String getSalePersonName() {
		if (agent != null) {
			return agent.getFullName();
		}
		return null;
	}

	public String getAgentName() {
		if (agent != null)
			return agent.getFullName();
		else
			return "N/A";
	}

	public String getAgentLiscenseNo() {
		if (agent != null)
			return " [" + agent.getLiscenseNo() + "]";
		else
			return "[0]";
	}

	public String getAgentInfo() {
		if (agent != null)
			return agent.getFullName() + " [" + agent.getLiscenseNo() + "]";
		else
			return "N/A [0]";
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public PersonTravelProposalInfo getPersonTravelInfo() {
		return personTravelInfo;
	}

	public void setPersonTravelInfo(PersonTravelProposalInfo personTravelInfo) {
		this.personTravelInfo = personTravelInfo;
	}

	public UserRecorder getRecorder() {
		return recorder;
	}

	public void setRecorder(UserRecorder recorder) {
		this.recorder = recorder;
	}

	public double getSpecialDiscount() {
		return specialDiscount;
	}

	public void setSpecialDiscount(double specialDiscount) {
		this.specialDiscount = specialDiscount;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<PersonTravelProposalInfo> getTravelInfoList() {
		List<PersonTravelProposalInfo> infoList = new ArrayList<>();
		infoList.add(this.getPersonTravelInfo());
		return infoList;
	}

	public boolean isUnderHundred() {
		if (SetUpIDConfig.isUnder100MileTravelInsurance(this.getProduct())) {
			return true;
		}
		return false;
	}

	public double getTotalDiscountAmount() {
		double specialDiscountAmount = Utils.getPercentOf(specialDiscount, personTravelInfo.getTotalPremium());
		return Utils.getTwoDecimalPoint(specialDiscountAmount);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + (complete ? 1231 : 1237);
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((personTravelInfo == null) ? 0 : personTravelInfo.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((proposalStatus == null) ? 0 : proposalStatus.hashCode());
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		result = prime * result + ((saleChannelType == null) ? 0 : saleChannelType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(specialDiscount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
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
		PersonTravelProposal other = (PersonTravelProposal) obj;
		if (agent == null) {
			if (other.agent != null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		if (branch == null) {
			if (other.branch != null)
				return false;
		} else if (!branch.equals(other.branch))
			return false;
		if (complete != other.complete)
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
		if (personTravelInfo == null) {
			if (other.personTravelInfo != null)
				return false;
		} else if (!personTravelInfo.equals(other.personTravelInfo))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (proposalNo == null) {
			if (other.proposalNo != null)
				return false;
		} else if (!proposalNo.equals(other.proposalNo))
			return false;
		if (proposalStatus != other.proposalStatus)
			return false;
		if (recorder == null) {
			if (other.recorder != null)
				return false;
		} else if (!recorder.equals(other.recorder))
			return false;
		if (saleChannelType != other.saleChannelType)
			return false;
		if (Double.doubleToLongBits(specialDiscount) != Double.doubleToLongBits(other.specialDiscount))
			return false;
		if (submittedDate == null) {
			if (other.submittedDate != null)
				return false;
		} else if (!submittedDate.equals(other.submittedDate))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	@Override
	public String getPolicyNo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getPremium() {
		if (personTravelInfo != null)
			return personTravelInfo.getBasicTermPremium();
		return 0;
	}

	@Override
	public double getAddOnPremium() {
		return 0;
	}

	@Override
	public double getTotalPremium() {
		if (personTravelInfo != null)
			return personTravelInfo.getTotalPremium();
		return 0;
	}

	@Override
	public double getTotalTermPremium() {
		if (personTravelInfo != null)
			return personTravelInfo.getTotalBasicTermPremium();
		return 0;
	}

	@Override
	public double getTotalSumInsured() {
		if (personTravelInfo != null)
			return personTravelInfo.getSumInsured();
		return 0;
	}

	@Override
	public int getTotalUnit() {
		if (personTravelInfo != null)
			return (int) personTravelInfo.getTotalUnit();
		return 0;
	}

	@Override
	public boolean isCoinsuranceApplied() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Date getCommenmanceDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getAgentCommission() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRenewalAgentCommission() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ProductGroup getProductGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IInsuredItem> getInsuredItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InsuranceType getInsuranceType() {
		// TODO Auto-generated method stub
		return InsuranceType.PERSON_TRAVEL;
	}

	@Override
	public ProposalType getProposalType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getActivedPolicyStartDate() {
		if (personTravelInfo != null)
			return personTravelInfo.getDepartureDate();
		return null;
	}

	@Override
	public Date getActivedPolicyEndDate() {
		if (personTravelInfo != null)
			return personTravelInfo.getArrivalDate();
		return null;
	}

	@Override
	public SalesPoints getSalesPoints() {
		// TODO Auto-generated method stub
		return null;
	}
}
