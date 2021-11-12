package org.ace.insurance.travel.claim;

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

import org.ace.insurance.claim.Attachment;
import org.ace.insurance.common.ClaimStatus;
import org.ace.insurance.common.PolicyReferenceType;
//import org.ace.insurance.common.RejectReason;
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.organization.Organization;
//import org.ace.insurance.web.common.PolicyType;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
//@Table(name = TableName.TRAVELCLAIM)
@TableGenerator(name = "TRAVELCLAIM_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "TRAVELCLAIM_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "TravelClaim.findAll", query = "SELECT t FROM TravelClaim t "),
		@NamedQuery(name = "TravelClaim.findById", query = "SELECT c FROM TravelClaim c WHERE c.id = :id"),
		@NamedQuery(name = "TravelClaim.findByClaimNo", query = "SELECT c FROM TravelClaim c WHERE c.claimNo = :claimNo") })
@EntityListeners(IDInterceptor.class)
public class TravelClaim implements Serializable {
//	private static final long serialVersionUID = 1L;
//
//	@Version
//	private int version;
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TRAVELCLAIM_GEN")
//	private String id;
//	private String claimNo;
//	private String policyNo;
//	private String accidencePlace;
//	private String causeOfAccidence;
//	private String rejectDescription;
//
//	@Temporal(TemporalType.DATE)
//	private Date policyStartDate;
//
//	@Temporal(TemporalType.DATE)
//	private Date policyEndDate;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date accidenceDate;
//
//	@Temporal(TemporalType.DATE)
//	private Date submittedDate;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date informedDate;
//
//	@Temporal(TemporalType.DATE)
//	private Date registeredDate;
//
//	@Enumerated(EnumType.STRING)
//	private PolicyType policyType;
//
//	@Enumerated(EnumType.STRING)
//	private ClaimStatus calimStatus;
//
//	@Enumerated(EnumType.STRING)
//	private RejectReason rejectReason;
//
//	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
//	@JoinColumn(name = "TRAVELCLAIMID", referencedColumnName = "ID")
//	private List<TravelClaimPerson> claimPersonList;
//
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "EXPRESSID", referencedColumnName = "ID")
//	private Express express;
//
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "CLAIMBRANCHID", referencedColumnName = "ID")
//	private Branch claimBranch;
//
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "POLICYBRANCHID", referencedColumnName = "ID")
//	private Branch policyBranch;
//
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "CUSTOMERID", referencedColumnName = "ID")
//	private Customer customer;
//
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ORGANIZATIONID", referencedColumnName = "ID")
//	private Organization organization;
//
//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "CURRENCYID", referencedColumnName = "ID")
//	private Currency currency;
//
//	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//	@JoinColumn(name = "HOLDERID", referencedColumnName = "ID")
//	private List<Attachment> attachmentList;
//	@Embedded
//	private UserRecorder recorder;
//
//	private double sumInsured;
//	private double totalUnit;
//	private int noOfPerson;
//	private String travelPath;
//	private String vehicleInfo;
//	private String productId;
//	private double premium;
//
//	public TravelClaim() {
//
//	}
//
//	public TravelClaim(TP001 registration) {
//		this.customer = registration.getCustomer();
//		this.organization = registration.getOrganization();
//		this.express = registration.getExpress();
//		this.vehicleInfo = registration.getRegistrationNo();
//		this.policyNo = registration.getPolicyNo();
//		this.policyType = registration.getPolicyType();
//		this.policyBranch = registration.getPolicyBranch();
//		this.policyStartDate = registration.getDepatureDate();
//		this.policyEndDate = registration.getArrivalDate();
//		this.sumInsured = registration.getSumInsured();
//		this.totalUnit = registration.getTotalUnit();
//		this.premium = registration.getPremium();
//		this.noOfPerson = registration.getNoOfPerson();
//		this.travelPath = registration.getTravelPath();
//		this.productId = registration.getProductId();
//		this.vehicleInfo = registration.getRegistrationNo();
//		this.currency = registration.getCurrency();
//
//	}
//
//	public UserRecorder getRecorder() {
//		return recorder;
//	}
//
//	public void setRecorder(UserRecorder recorder) {
//		this.recorder = recorder;
//	}
//
//	public PolicyReferenceType getPolicyReferenceType() {
//		PolicyReferenceType referenceType;
//		if (policyType.equals(PolicyType.SPECIALTRAVEL)) {
//			referenceType = PolicyReferenceType.SPECIAL_TRAVEL_CLAIM;
//		} else {
//			referenceType = PolicyReferenceType.TRAVEL_CLAIM;
//
//		}
//		return referenceType;
//	}
//
//	public int getVersion() {
//		return version;
//	}
//
//	public void setVersion(int version) {
//		this.version = version;
//	}
//
//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public String getClaimNo() {
//		return claimNo;
//	}
//
//	public void setClaimNo(String claimNo) {
//		this.claimNo = claimNo;
//	}
//
//	public String getPolicyNo() {
//		return policyNo;
//	}
//
//	public void setPolicyNo(String policyNo) {
//		this.policyNo = policyNo;
//	}
//
//	public String getAccidencePlace() {
//		return accidencePlace;
//	}
//
//	public void setAccidencePlace(String accidencePlace) {
//		this.accidencePlace = accidencePlace;
//	}
//
//	public String getCauseOfAccidence() {
//		return causeOfAccidence;
//	}
//
//	public void setCauseOfAccidence(String causeOfAccidence) {
//		this.causeOfAccidence = causeOfAccidence;
//	}
//
//	public String getRejectDescription() {
//		return rejectDescription;
//	}
//
//	public void setRejectDescription(String rejectDescription) {
//		this.rejectDescription = rejectDescription;
//	}
//
//	public Date getPolicyStartDate() {
//		return policyStartDate;
//	}
//
//	public void setPolicyStartDate(Date policyStartDate) {
//		this.policyStartDate = policyStartDate;
//	}
//
//	public Date getPolicyEndDate() {
//		return policyEndDate;
//	}
//
//	public void setPolicyEndDate(Date policyEndDate) {
//		this.policyEndDate = policyEndDate;
//	}
//
//	public Date getAccidenceDate() {
//		return accidenceDate;
//	}
//
//	public void setAccidenceDate(Date accidenceDate) {
//		this.accidenceDate = accidenceDate;
//	}
//
//	public Date getSubmittedDate() {
//		return submittedDate;
//	}
//
//	public void setSubmittedDate(Date submittedDate) {
//		this.submittedDate = submittedDate;
//	}
//
//	public Date getInformedDate() {
//		return informedDate;
//	}
//
//	public void setInformedDate(Date informedDate) {
//		this.informedDate = informedDate;
//	}
//
//	public Date getRegisteredDate() {
//		return registeredDate;
//	}
//
//	public void setRegisteredDate(Date registeredDate) {
//		this.registeredDate = registeredDate;
//	}
//
//	public PolicyType getPolicyType() {
//		return policyType;
//	}
//
//	public void setPolicyType(PolicyType policyType) {
//		this.policyType = policyType;
//	}
//
//	public ClaimStatus getCalimStatus() {
//		return calimStatus;
//	}
//
//	public void setCalimStatus(ClaimStatus calimStatus) {
//		this.calimStatus = calimStatus;
//	}
//
//	public RejectReason getRejectReason() {
//		return rejectReason;
//	}
//
//	public void setRejectReason(RejectReason rejectReason) {
//		this.rejectReason = rejectReason;
//	}
//
//	public Express getExpress() {
//		return express;
//	}
//
//	public void setExpress(Express express) {
//		this.express = express;
//	}
//
//	public Branch getClaimBranch() {
//		return claimBranch;
//	}
//
//	public void setClaimBranch(Branch claimBranch) {
//		this.claimBranch = claimBranch;
//	}
//
//	public Branch getPolicyBranch() {
//		return policyBranch;
//	}
//
//	public List<TravelClaimPerson> getClaimPersonList() {
//		return claimPersonList;
//	}
//
//	public void addTravelClaimPerson(TravelClaimPerson person) {
//		if (claimPersonList == null) {
//			claimPersonList = new ArrayList<TravelClaimPerson>();
//		}
//		claimPersonList.add(person);
//	}
//
//	public void setClaimPersonList(List<TravelClaimPerson> claimPersonList) {
//		this.claimPersonList = claimPersonList;
//	}
//
//	public void setPolicyBranch(Branch policyBranch) {
//		this.policyBranch = policyBranch;
//	}
//
//	public Customer getCustomer() {
//		return customer;
//	}
//
//	public void setCustomer(Customer customer) {
//		this.customer = customer;
//	}
//
//	public List<Attachment> getAttachmentList() {
//		return attachmentList;
//	}
//
//	public void setAttachmentList(List<Attachment> attachmentList) {
//		this.attachmentList = attachmentList;
//	}
//
//	public void addAttachment(Attachment attachment) {
//		if (attachmentList == null) {
//			attachmentList = new ArrayList<Attachment>();
//		}
//		attachmentList.add(attachment);
//	}
//
//	public Organization getOrganization() {
//		return organization;
//	}
//
//	public void setOrganization(Organization organization) {
//		this.organization = organization;
//	}
//
//	public double getSumInsured() {
//		return sumInsured;
//	}
//
//	public void setSumInsured(double sumInsured) {
//		this.sumInsured = sumInsured;
//	}
//
//	public double getTotalUnit() {
//		return totalUnit;
//	}
//
//	public void setTotalUnit(double totalUnit) {
//		this.totalUnit = totalUnit;
//	}
//
//	public int getNoOfPerson() {
//		return noOfPerson;
//	}
//
//	public void setNoOfPerson(int noOfPerson) {
//		this.noOfPerson = noOfPerson;
//	}
//
//	public String getProductId() {
//		return productId;
//	}
//
//	public void setProductId(String productId) {
//		this.productId = productId;
//	}
//
//	public String getTravelPath() {
//		return travelPath;
//	}
//
//	public void setTravelPath(String travelPath) {
//		this.travelPath = travelPath;
//	}
//
//	public String getVehicleInfo() {
//		return vehicleInfo;
//	}
//
//	public void setVehicleInfo(String vehicleInfo) {
//		this.vehicleInfo = vehicleInfo;
//	}
//
//	public double getPremium() {
//		return premium;
//	}
//
//	public void setPremium(double premium) {
//		this.premium = premium;
//	}
//
//	public String getCustomerName() {
//		return customer != null ? customer.getFullName() : organization != null ? organization.getName() : express != null ? express.getName() : "";
//	}
//
//	public String getCustomerId() {
//		return customer != null ? customer.getId() : organization != null ? organization.getId() : express != null ? express.getId() : "";
//	}
//
//	public Currency getCurrency() {
//		return currency;
//	}
//
//	public void setCurrency(Currency currency) {
//		this.currency = currency;
//	}
//
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((accidenceDate == null) ? 0 : accidenceDate.hashCode());
//		result = prime * result + ((accidencePlace == null) ? 0 : accidencePlace.hashCode());
//		result = prime * result + ((attachmentList == null) ? 0 : attachmentList.hashCode());
//		result = prime * result + ((calimStatus == null) ? 0 : calimStatus.hashCode());
//		result = prime * result + ((causeOfAccidence == null) ? 0 : causeOfAccidence.hashCode());
//		result = prime * result + ((claimBranch == null) ? 0 : claimBranch.hashCode());
//		result = prime * result + ((claimNo == null) ? 0 : claimNo.hashCode());
//		result = prime * result + ((claimPersonList == null) ? 0 : claimPersonList.hashCode());
//		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
//		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
//		result = prime * result + ((express == null) ? 0 : express.hashCode());
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		result = prime * result + ((informedDate == null) ? 0 : informedDate.hashCode());
//		result = prime * result + noOfPerson;
//		result = prime * result + ((organization == null) ? 0 : organization.hashCode());
//		result = prime * result + ((policyBranch == null) ? 0 : policyBranch.hashCode());
//		result = prime * result + ((policyEndDate == null) ? 0 : policyEndDate.hashCode());
//		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
//		result = prime * result + ((policyStartDate == null) ? 0 : policyStartDate.hashCode());
//		result = prime * result + ((policyType == null) ? 0 : policyType.hashCode());
//		long temp;
//		temp = Double.doubleToLongBits(premium);
//		result = prime * result + (int) (temp ^ (temp >>> 32));
//		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
//		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
//		result = prime * result + ((registeredDate == null) ? 0 : registeredDate.hashCode());
//		result = prime * result + ((rejectDescription == null) ? 0 : rejectDescription.hashCode());
//		result = prime * result + ((rejectReason == null) ? 0 : rejectReason.hashCode());
//		result = prime * result + ((submittedDate == null) ? 0 : submittedDate.hashCode());
//		temp = Double.doubleToLongBits(sumInsured);
//		result = prime * result + (int) (temp ^ (temp >>> 32));
//		temp = Double.doubleToLongBits(totalUnit);
//		result = prime * result + (int) (temp ^ (temp >>> 32));
//		result = prime * result + ((travelPath == null) ? 0 : travelPath.hashCode());
//		result = prime * result + ((vehicleInfo == null) ? 0 : vehicleInfo.hashCode());
//		result = prime * result + version;
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		TravelClaim other = (TravelClaim) obj;
//		if (accidenceDate == null) {
//			if (other.accidenceDate != null)
//				return false;
//		} else if (!accidenceDate.equals(other.accidenceDate))
//			return false;
//		if (accidencePlace == null) {
//			if (other.accidencePlace != null)
//				return false;
//		} else if (!accidencePlace.equals(other.accidencePlace))
//			return false;
//		if (attachmentList == null) {
//			if (other.attachmentList != null)
//				return false;
//		} else if (!attachmentList.equals(other.attachmentList))
//			return false;
//		if (calimStatus != other.calimStatus)
//			return false;
//		if (causeOfAccidence == null) {
//			if (other.causeOfAccidence != null)
//				return false;
//		} else if (!causeOfAccidence.equals(other.causeOfAccidence))
//			return false;
//		if (claimBranch == null) {
//			if (other.claimBranch != null)
//				return false;
//		} else if (!claimBranch.equals(other.claimBranch))
//			return false;
//		if (claimNo == null) {
//			if (other.claimNo != null)
//				return false;
//		} else if (!claimNo.equals(other.claimNo))
//			return false;
//		if (claimPersonList == null) {
//			if (other.claimPersonList != null)
//				return false;
//		} else if (!claimPersonList.equals(other.claimPersonList))
//			return false;
//		if (currency == null) {
//			if (other.currency != null)
//				return false;
//		} else if (!currency.equals(other.currency))
//			return false;
//		if (customer == null) {
//			if (other.customer != null)
//				return false;
//		} else if (!customer.equals(other.customer))
//			return false;
//		if (express == null) {
//			if (other.express != null)
//				return false;
//		} else if (!express.equals(other.express))
//			return false;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		if (informedDate == null) {
//			if (other.informedDate != null)
//				return false;
//		} else if (!informedDate.equals(other.informedDate))
//			return false;
//		if (noOfPerson != other.noOfPerson)
//			return false;
//		if (organization == null) {
//			if (other.organization != null)
//				return false;
//		} else if (!organization.equals(other.organization))
//			return false;
//		if (policyBranch == null) {
//			if (other.policyBranch != null)
//				return false;
//		} else if (!policyBranch.equals(other.policyBranch))
//			return false;
//		if (policyEndDate == null) {
//			if (other.policyEndDate != null)
//				return false;
//		} else if (!policyEndDate.equals(other.policyEndDate))
//			return false;
//		if (policyNo == null) {
//			if (other.policyNo != null)
//				return false;
//		} else if (!policyNo.equals(other.policyNo))
//			return false;
//		if (policyStartDate == null) {
//			if (other.policyStartDate != null)
//				return false;
//		} else if (!policyStartDate.equals(other.policyStartDate))
//			return false;
//		if (policyType != other.policyType)
//			return false;
//		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
//			return false;
//		if (productId == null) {
//			if (other.productId != null)
//				return false;
//		} else if (!productId.equals(other.productId))
//			return false;
//		if (recorder == null) {
//			if (other.recorder != null)
//				return false;
//		} else if (!recorder.equals(other.recorder))
//			return false;
//		if (registeredDate == null) {
//			if (other.registeredDate != null)
//				return false;
//		} else if (!registeredDate.equals(other.registeredDate))
//			return false;
//		if (rejectDescription == null) {
//			if (other.rejectDescription != null)
//				return false;
//		} else if (!rejectDescription.equals(other.rejectDescription))
//			return false;
//		if (rejectReason != other.rejectReason)
//			return false;
//		if (submittedDate == null) {
//			if (other.submittedDate != null)
//				return false;
//		} else if (!submittedDate.equals(other.submittedDate))
//			return false;
//		if (Double.doubleToLongBits(sumInsured) != Double.doubleToLongBits(other.sumInsured))
//			return false;
//		if (Double.doubleToLongBits(totalUnit) != Double.doubleToLongBits(other.totalUnit))
//			return false;
//		if (travelPath == null) {
//			if (other.travelPath != null)
//				return false;
//		} else if (!travelPath.equals(other.travelPath))
//			return false;
//		if (vehicleInfo == null) {
//			if (other.vehicleInfo != null)
//				return false;
//		} else if (!vehicleInfo.equals(other.vehicleInfo))
//			return false;
//		if (version != other.version)
//			return false;
//		return true;
//	}

}
