package org.ace.insurance.travel.personTravel.policy;

import java.io.Serializable;
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
import javax.persistence.Version;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProposalType;
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
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PERSONTRAVEL_POLICY)
@TableGenerator(name = "PERSONTRAVEL_POLICY_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PERSONTRAVEL_POLICY_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PersonTravelPolicy.findAll", query = "SELECT p FROM PersonTravelPolicy p"),
		@NamedQuery(name = "PersonTravelPolicy.findByPolicyNo", query = "SELECT m FROM PersonTravelPolicy m WHERE m.policyNo IS NOT NULL AND m.policyNo = :policyNo"), })
@EntityListeners(IDInterceptor.class)
public class PersonTravelPolicy implements Serializable, IPolicy {

	private static final long serialVersionUID = -6240320890425254185L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PERSONTRAVEL_POLICY_GEN")
	private String id;

	private String policyNo;

	private double specialDiscount;

	@Enumerated(EnumType.STRING)
	private SaleChannelType saleChannelType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID")
	private Customer customer;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORGANIZATIONID", referencedColumnName = "ID")
	private Organization organization;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGENTID", referencedColumnName = "ID")
	private Agent agent;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAYMENTTYPEID", referencedColumnName = "ID")
	private PaymentType paymentType;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CURRENCYID", referencedColumnName = "ID")
	private Currency currency;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BRANCHID", referencedColumnName = "ID")
	private Branch branch;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PERSONTRAVELPOLICYINFOID", referencedColumnName = "ID")
	private PersonTravelPolicyInfo personTravelPolicyInfo;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROPOSALID", referencedColumnName = "ID")
	private PersonTravelProposal personTravelProposal;

	@Embedded
	private UserRecorder recorder;

	@Version
	private int version;
	
	public PersonTravelPolicy() {
	}

	public PersonTravelPolicy(PersonTravelProposal personTravelProposal) {
		this.customer = personTravelProposal.getCustomer();
		this.organization = personTravelProposal.getOrganization();
		this.agent = personTravelProposal.getAgent();
		this.product = personTravelProposal.getProduct();
		this.paymentType = personTravelProposal.getPaymentType();
		this.currency = personTravelProposal.getCurrency();
		this.branch = personTravelProposal.getBranch();
		this.personTravelPolicyInfo = new PersonTravelPolicyInfo(personTravelProposal.getPersonTravelInfo());
		this.personTravelProposal = personTravelProposal;
		this.saleChannelType = personTravelProposal.getSaleChannelType();
		this.specialDiscount = personTravelProposal.getSpecialDiscount();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the policyNo
	 */
	public String getPolicyNo() {
		return policyNo;
	}

	/**
	 * @param policyNo
	 *            the policyNo to set
	 */
	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer
	 *            the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return the organization
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * @param organization
	 *            the organization to set
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	/**
	 * @return the agent
	 */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * @param agent
	 *            the agent to set
	 */
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * @return the paymentType
	 */
	public PaymentType getPaymentType() {
		return paymentType;
	}

	/**
	 * @param paymentType
	 *            the paymentType to set
	 */
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the branch
	 */
	public Branch getBranch() {
		return branch;
	}

	/**
	 * @param branch
	 *            the branch to set
	 */
	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	/**
	 * @return the personTravelPolicyInfo
	 */
	public PersonTravelPolicyInfo getPersonTravelPolicyInfo() {
		return personTravelPolicyInfo;
	}

	/**
	 * @param personTravelPolicyInfo
	 *            the personTravelPolicyInfo to set
	 */
	public void setPersonTravelPolicyInfo(PersonTravelPolicyInfo personTravelPolicyInfo) {
		this.personTravelPolicyInfo = personTravelPolicyInfo;
	}

	public PersonTravelProposal getPersonTravelProposal() {
		return personTravelProposal;
	}

	public void setPersonTravelProposal(PersonTravelProposal personTravelProposal) {
		this.personTravelProposal = personTravelProposal;
	}

	/**
	 * @return the recorder
	 */
	public UserRecorder getRecorder() {
		return recorder;
	}

	/**
	 * @param recorder
	 *            the recorder to set
	 */
	public void setRecorder(UserRecorder recorder) {
		this.recorder = recorder;
	}

	public double getSpecialDiscount() {
		return specialDiscount;
	}

	public void setSpecialDiscount(double specialDiscount) {
		this.specialDiscount = specialDiscount;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
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

	public String getCustomerPhno() {
		if (customer != null) {
			return customer.getContentInfo().getPhoneOrMoblieNo();
		}
		if (organization != null) {
			return organization.getContentInfo().getPhoneOrMoblieNo();
		}
		return null;
	}

	public double getAgentCommission() {
		double totalCommission = 0.0;
		if (agent != null) {
			double commissionPercent = product.getFirstCommission();
			if (commissionPercent > 0) {
				double totalPremium = personTravelPolicyInfo.getBasicTermPremium();
				double commission = Utils.getPercentOf(commissionPercent, totalPremium);
				totalCommission = totalCommission + commission;
			}
		}
		return Utils.getTwoDecimalPoint(totalCommission);
	}

	public double getTotalBasicTermPremium() {
		double result = 0.0;
		result += personTravelPolicyInfo.getTotalBasicTermPremium();
		return result;
	}

	public String getAgentName() {
		if (agent != null)
			return agent.getFullName();
		else
			return "N/A";
	}

	public String getAgentLiscneseNo() {
		if (agent != null)
			return agent.getLiscenseNo();
		else
			return "[0]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((personTravelPolicyInfo == null) ? 0 : personTravelPolicyInfo.hashCode());
		result = prime * result + ((personTravelProposal == null) ? 0 : personTravelProposal.hashCode());
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		result = prime * result + ((saleChannelType == null) ? 0 : saleChannelType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(specialDiscount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		PersonTravelPolicy other = (PersonTravelPolicy) obj;
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
		if (personTravelPolicyInfo == null) {
			if (other.personTravelPolicyInfo != null)
				return false;
		} else if (!personTravelPolicyInfo.equals(other.personTravelPolicyInfo))
			return false;
		if (personTravelProposal == null) {
			if (other.personTravelProposal != null)
				return false;
		} else if (!personTravelProposal.equals(other.personTravelProposal))
			return false;
		if (policyNo == null) {
			if (other.policyNo != null)
				return false;
		} else if (!policyNo.equals(other.policyNo))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
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
		if (version != other.version)
			return false;
		return true;
	}

	@Override
	public double getTotalDiscountAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPremium() {
		// TODO Auto-generated method stub
		return personTravelPolicyInfo.getPremium();
	}

	@Override
	public double getAddOnPremium() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalTermPremium() {
		return personTravelPolicyInfo.getBasicTermPremium();
	}

	@Override
	public boolean isCoinsuranceApplied() {
		return false;
	}

	@Override
	public Date getCommenmanceDate() {
		return null;
	}

	@Override
	public double getRenewalAgentCommission() {
		return 0;
	}

	public String getCustomerNameWithBank() {
		return getCustomerName();
	}

	@Override
	public String getCustomerAddress() {
		if (customer != null) {
			return customer.getFullAddress();
		}
		if (organization != null) {
			return organization.getFullAddress();
		}
		return null;
	}

	@Override
	public ProductGroup getProductGroup() {
		return null;
	}

	@Override
	public List<IInsuredItem> getInsuredItems() {
		return null;
	}

	@Override
	public InsuranceType getInsuranceType() {
		return InsuranceType.PERSON_TRAVEL;
	}

	@Override
	public double getTotalPremium() {
		double totalPremium = getPremium() + getAddOnPremium();
		return Utils.getTwoDecimalPoint(totalPremium);
	}

	@Override
	public double getTotalSumInsured() {
		if (personTravelPolicyInfo != null)
			return personTravelPolicyInfo.getSumInsured();
		return 0;
	}

	@Override
	public ProposalType getProposalType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getActivedPolicyStartDate() {
		if (personTravelPolicyInfo != null)
			return personTravelPolicyInfo.getDepartureDate();
		return null;
	}

	@Override
	public Date getActivedPolicyEndDate() {
		if (personTravelPolicyInfo != null)
			return personTravelPolicyInfo.getArrivalDate();
		return null;
	}

	@Override
	public int getTotalUnit() {
		if (personTravelPolicyInfo != null)
			return (int) personTravelPolicyInfo.getTotalUnit();
		return 0;
	}

	@Override
	public String getCustomerId() {
		if (customer != null) {
			return customer.getId();
		}
		if (organization != null) {
			return organization.getId();
		}
		return null;
	}

	public String getCustomerFullIdNo() {
		if (customer != null) {
			return customer.getFullIdNo();
		}
		if (organization != null) {
			return organization.getRegNo();
		}
		return null;
	}

	public String getCustomerFatherName() {
		if (customer != null) {
			return customer.getFatherName();
		}
		if (organization != null) {
			return organization.getOwnerName();
		}
		return null;
	}

	@Override
	public SalesPoints getSalesPoints() {
		// TODO Auto-generated method stub
		return null;
	}
}

