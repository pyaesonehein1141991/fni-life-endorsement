package org.ace.insurance.travel.expressTravel;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ProposalStatus;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.interfaces.IInsuredItem;
import org.ace.insurance.common.interfaces.IPolicy;
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
@Table(name = TableName.TRAVELPROPOSAL)
@TableGenerator(name = "TRAVELPROPOSAL_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "TRAVELPROPOSAL_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "TravelProposal.findAll", query = "SELECT t FROM TravelProposal t "),
		@NamedQuery(name = "TravelProposal.findByDate", query = "SELECT t FROM TravelProposal t WHERE t.submittedDate BETWEEN :startDate AND :endDate") })
@EntityListeners(IDInterceptor.class)
public class TravelProposal implements Serializable, IPolicy {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TRAVELPROPOSAL_GEN")
	private String id;
	private String proposalNo;
	private String policyNo;
	private double specialDiscount;

	@Temporal(TemporalType.DATE)
	private Date submittedDate;

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
	@JoinColumn(name = "CURRENCYID", referencedColumnName = "ID")
	private Currency currency;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SALESPOINTSID", referencedColumnName = "ID")
	private SalesPoints salesPoints;

	
	/** OneToOne in FNI */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "travelProposal", orphanRemoval = true)
	private List<TravelExpress> expressList;

	@Temporal(TemporalType.DATE)
	private Date fromDate;

	@Temporal(TemporalType.DATE)
	private Date toDate;

	@Enumerated(EnumType.STRING)
	private SaleChannelType saleChannelType;

	@Embedded
	private UserRecorder recorder;

	@Enumerated(EnumType.STRING)
	private ProposalStatus proposalStatus;

	@Version
	private int version;

	public TravelProposal() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserRecorder getRecorder() {
		return recorder;
	}

	public void setRecorder(UserRecorder recorder) {
		this.recorder = recorder;
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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public List<TravelExpress> getExpressList() {
		if (expressList == null) {
			expressList = new ArrayList<TravelExpress>();
		}
		return expressList;
	}

	public void setExpressList(List<TravelExpress> expressList) {
		for (TravelExpress express : expressList) {
			express.setTravelProposal(this);
		}
		this.expressList = expressList;
	}

	public void addExpress(TravelExpress express) {
		express.setTravelProposal(this);
		getExpressList().add(express);
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public ProposalStatus getProposalStatus() {
		return proposalStatus;
	}

	public void setProposalStatus(ProposalStatus proposalStatus) {
		this.proposalStatus = proposalStatus;
	}

	public double getSpecialDiscount() {
		return specialDiscount;
	}

	public void setSpecialDiscount(double specialDiscount) {
		this.specialDiscount = specialDiscount;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Agent getAgent() {
		return agent;
	}

	/*************** Generated Method ****/
	public int getTotalUnit() {
		int totalUnit = 0;
		for (TravelExpress travelExpress : getExpressList()) {
			totalUnit += travelExpress.getNoOfUnit();
		}
		return totalUnit;
	}

	public double getTotalSumInsured() {
		int totalUnit = 0;
		for (TravelExpress travelExpress : getExpressList()) {
			totalUnit += travelExpress.getSumInsured();
		}
		return totalUnit;
	}

	public double getTotalNetPremium() {
		double totalPremium = 0.0;
		for (TravelExpress travelExpress : getExpressList()) {
			totalPremium += travelExpress.getPremium();
		}
		return totalPremium;
	}

	public double getTotalCommission() {
		double totalCommission = 0.0;
		for (TravelExpress travelExpress : getExpressList()) {
			totalCommission += travelExpress.getCommission();
		}
		return totalCommission;
	}

	public int getTotalPassenger() {
		int totalPassenger = 0;
		for (TravelExpress travelExpress : getExpressList()) {
			totalPassenger += travelExpress.getNoOfPassenger();
		}
		return totalPassenger;
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

	public String getSalePerson() {
		String result = "";
		if (SaleChannelType.AGENT.equals(saleChannelType) || SaleChannelType.DIRECTMARKETING.equals(saleChannelType)) {
			if (agent != null) {
				result = agent.getFullName();
			}
		}
		return result;
	}

	@Override
	public double getTotalDiscountAmount() {
		double specialDiscountAmount = Utils.getPercentOf(specialDiscount, getPremium());
		return Utils.getTwoDecimalPoint(specialDiscountAmount);
	}

	@Override
	public double getPremium() {
		double premium = 0.0;
		for (TravelExpress te : getExpressList()) {
			premium += te.getPremium();
		}
		return premium;
	}

	@Override
	public double getAddOnPremium() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTotalPremium() {
		return Utils.getTwoDecimalPoint(getPremium() + getAddOnPremium());
	}

	@Override
	public double getTotalTermPremium() {
		return getTotalPremium();
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
	public Organization getOrganization() {
		// TODO Auto-generated method stub
		return null;
	}

	public Customer getCustomer() {
		// TODO Auto-generated method stub
		return null;
	}

	private TravelExpress getTravelExpress() {
		if (getExpressList().size() > 0)
			return getExpressList().get(0);
		return null;
	}

	public String getCustomerName() {
		if (getTravelExpress() != null && getTravelExpress().getExpress() != null) {
			return getTravelExpress().getExpress().getFullName();
		}
		return "";
	}

	public String getTourNameList() {
		if (getTravelExpress() != null) {
			return getTravelExpress().getTourNameList();
		}
		return "";
	}

	public String getRegistrationNoList() {
		if (getTravelExpress() != null) {
			return getTravelExpress().getRegistrationNoList();
		}
		return "";
	}

	@Override
	public String getCustomerId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCustomerNameWithBank() {
		return getCustomerName();
	}

	public String getCustomerAddress() {
		String customerAddress = expressList.get(0).getExpress().getFullAddress();
		return customerAddress;
	}

	public String getPhoneNo() {
		String phonNo = expressList.get(0).getExpress().getContentInfo().getPhoneOrMoblieNo();
		return phonNo;
	}

	public String getCustomerNrc() {
		String customerNrc = expressList.get(0).getExpress().getOwnerName();
		return customerNrc;
	}

	public String getTourName() {
		String tourName = expressList.get(0).getTourNameList();
		return tourName;
	}

	public String getExpressName() {
		String expressName = expressList.get(0).getExpress().getName();
		return expressName;
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
		return null;
	}

	@Override
	public ProposalType getProposalType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getActivedPolicyStartDate() {
		if (fromDate != null)
			return fromDate;
		return fromDate;
	}

	@Override
	public Date getActivedPolicyEndDate() {
		if (toDate != null)
			return toDate;
		return toDate;
	}

	public double getNetPremium() {
		return getTotalPremium() - getTotalDiscountAmount();
	}
	
	

	public SalesPoints getSalesPoints() {
		return salesPoints;
	}

	public void setSalesPoints(SalesPoints salesPoints) {
		this.salesPoints = salesPoints;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((paymentType == null) ? 0 : paymentType.hashCode());
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		result = prime * result + ((proposalNo == null) ? 0 : proposalNo.hashCode());
		result = prime * result + ((proposalStatus == null) ? 0 : proposalStatus.hashCode());
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		result = prime * result + ((saleChannelType == null) ? 0 : saleChannelType.hashCode());
		long temp;
		temp = Double.doubleToLongBits(specialDiscount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
		result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
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
		TravelProposal other = (TravelProposal) obj;
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
		if (fromDate == null) {
			if (other.fromDate != null)
				return false;
		} else if (!fromDate.equals(other.fromDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (paymentType == null) {
			if (other.paymentType != null)
				return false;
		} else if (!paymentType.equals(other.paymentType))
			return false;
		if (policyNo == null) {
			if (other.policyNo != null)
				return false;
		} else if (!policyNo.equals(other.policyNo))
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
		if (toDate == null) {
			if (other.toDate != null)
				return false;
		} else if (!toDate.equals(other.toDate))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
