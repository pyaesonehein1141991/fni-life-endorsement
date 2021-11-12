package org.ace.insurance.travel.personTravel.policy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.common.Utils;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalInfo;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalKeyfactorValue;
import org.ace.insurance.travel.personTravel.proposal.ProposalPersonTravelVehicle;
import org.ace.insurance.travel.personTravel.proposal.ProposalTraveller;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PERSONTRAVEL_POLICY_INFO)
@TableGenerator(name = "PERSONTRAVEL_POLICY_INFO_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PERSONTRAVEL_POLICY_INFO_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PersonTravelPolicyInfo.findAll", query = "SELECT p FROM PersonTravelPolicyInfo p") })
@EntityListeners(IDInterceptor.class)
public class PersonTravelPolicyInfo implements Serializable {

	private static final long serialVersionUID = 1035242311912746968L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PERSONTRAVEL_POLICY_INFO_GEN")
	private String id;
	private int noOfPassenger;
	private double totalUnit;
	private int paymentTerm;
	private double premiumRate;
	private double sumInsured;
	private double premium;
	private double basicTermPremium;

	@Temporal(TemporalType.DATE)
	private Date departureDate;

	@Temporal(TemporalType.DATE)
	private Date arrivalDate;

	@Column(name = "TRAVELPATH")
	private String travelPath;

	private boolean isRoundTrip;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXPRESSID", referencedColumnName = "ID")
	private Express express;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PERSONTRAVELPOLICYINFOID", referencedColumnName = "ID")
	private List<PolicyPersonTravelVehicle> policyPersonTravelVehicleList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PERSONTRAVELPOLICYINFOID", referencedColumnName = "ID")
	private List<PersonTravelPolicyKeyfactorValue> travelPolicyKeyfactorValueList;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "PERSONTRAVELPOLICYINFOID", referencedColumnName = "ID")
	private List<PolicyTraveller> policyTravellerList;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "HOLDERID", referencedColumnName = "ID")
	private List<Attachment> attachmentList;

	@Embedded
	private UserRecorder recorder;

	@Version
	private int version;

	public PersonTravelPolicyInfo() {
	}

	public PersonTravelPolicyInfo(PersonTravelProposalInfo personTravelInfo) {
		this.noOfPassenger = personTravelInfo.getNoOfPassenger();
		this.totalUnit = personTravelInfo.getTotalUnit();
		this.paymentTerm = personTravelInfo.getPaymentTerm();
		this.premiumRate = personTravelInfo.getPremiumRate();
		this.sumInsured = personTravelInfo.getSumInsured();
		this.premium = personTravelInfo.getPremium();
		this.basicTermPremium = personTravelInfo.getBasicTermPremium();
		this.departureDate = personTravelInfo.getDepartureDate();
		this.arrivalDate = personTravelInfo.getArrivalDate();
		this.travelPath = personTravelInfo.getTravelPath();
		this.isRoundTrip = personTravelInfo.isRoundTrip();
		this.express = personTravelInfo.getExpress();
		PolicyPersonTravelVehicle policyVehicle;
		for (ProposalPersonTravelVehicle vehicle : personTravelInfo.getProposalPersonTravelVehicleList()) {
			policyVehicle = new PolicyPersonTravelVehicle(vehicle);
			if (policyPersonTravelVehicleList == null) {
				policyPersonTravelVehicleList = new ArrayList<>();
			}
			policyPersonTravelVehicleList.add(policyVehicle);
		}
		for (PersonTravelProposalKeyfactorValue keyFactorValue : personTravelInfo.getTravelProposalKeyfactorValueList()) {
			if (travelPolicyKeyfactorValueList == null) {
				travelPolicyKeyfactorValueList = new ArrayList<>();
			}
			travelPolicyKeyfactorValueList.add(new PersonTravelPolicyKeyfactorValue(keyFactorValue));
		}
		for (ProposalTraveller traveller : personTravelInfo.getProposalTravellerList()) {
			if (policyTravellerList == null) {
				policyTravellerList = new ArrayList<>();
			}
			policyTravellerList.add(new PolicyTraveller(traveller));
		}
		for (Attachment attachment : personTravelInfo.getAttachmentList()) {
			if (attachmentList == null) {
				attachmentList = new ArrayList<>();
			}
			attachmentList.add(new Attachment(attachment));
		}
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
	 * @return the noOfPassenger
	 */
	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	/**
	 * @param noOfPassenger
	 *            the noOfPassenger to set
	 */
	public void setNoOfPassenger(int noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}

	/**
	 * @return the totalUnit
	 */
	public double getTotalUnit() {
		return totalUnit;
	}

	/**
	 * @param totalUnit
	 *            the totalUnit to set
	 */
	public void setTotalUnit(int totalUnit) {
		this.totalUnit = totalUnit;
	}

	/**
	 * @return the paymentTerm
	 */
	public int getPaymentTerm() {
		return paymentTerm;
	}

	/**
	 * @param paymentTerm
	 *            the paymentTerm to set
	 */
	public void setPaymentTerm(int paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	/**
	 * @return the premiumRate
	 */
	public double getPremiumRate() {
		return premiumRate;
	}

	/**
	 * @param premiumRate
	 *            the premiumRate to set
	 */
	public void setPremiumRate(double premiumRate) {
		this.premiumRate = premiumRate;
	}

	/**
	 * @return the premium
	 */
	public double getPremium() {
		return premium;
	}

	/**
	 * @param premium
	 *            the premium to set
	 */
	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	/**
	 * @return the basicTermPremium
	 */
	public double getBasicTermPremium() {
		return basicTermPremium;
	}

	/**
	 * @param basicTermPremium
	 *            the basicTermPremium to set
	 */
	public void setBasicTermPremium(double basicTermPremium) {
		this.basicTermPremium = basicTermPremium;
	}

	// public double getTotalTermPremium() {
	// double result = 0;
	// if (policyTravellerList.size() > 1) {
	// for (PolicyTraveller insuredPerson : this.getPolicyTravellerList()) {
	// result += insuredPerson.getBasicTermPremium();
	// }
	// } else {
	// result = this.getBasicTermPremium();
	// }
	// return result;
	// }

	/**
	 * @return the startDate
	 */
	public Date getDepartureDate() {
		return departureDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getArrivalDate() {
		return arrivalDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	/**
	 * @return the travelPath
	 */
	public String getTravelPath() {
		return travelPath;
	}

	/**
	 * @param travelPath
	 *            the travelPath to set
	 */

	public void setTravelPath(String travelPath) {
		this.travelPath = travelPath;
	}

	/**
	 * @return the isRoundTrip
	 */
	public boolean isRoundTrip() {
		return isRoundTrip;
	}

	/**
	 * @param isRoundTrip
	 *            the isRoundTrip to set
	 */
	public void setRoundTrip(boolean isRoundTrip) {
		this.isRoundTrip = isRoundTrip;
	}

	/**
	 * @return the policyPersonTravelVehicleList
	 */
	public List<PolicyPersonTravelVehicle> getPolicyPersonTravelVehicleList() {
		return policyPersonTravelVehicleList;
	}

	/**
	 * @param policyPersonTravelVehicleList
	 *            the policyPersonTravelVehicleList to set
	 */
	public void setPolicyPersonTravelVehicleList(List<PolicyPersonTravelVehicle> policyPersonTravelVehicleList) {
		this.policyPersonTravelVehicleList = policyPersonTravelVehicleList;
	}

	/**
	 * @return the express
	 */
	public Express getExpress() {
		return express;
	}

	/**
	 * @param express
	 *            the express to set
	 */
	public void setExpress(Express express) {
		this.express = express;
	}

	/**
	 * @return the travelPolicyKeyfactorValueList
	 */
	public List<PersonTravelPolicyKeyfactorValue> getTravelPolicyKeyfactorValueList() {
		return travelPolicyKeyfactorValueList;
	}

	/**
	 * @param travelPolicyKeyfactorValueList
	 *            the travelPolicyKeyfactorValueList to set
	 */
	public void setTravelPolicyKeyfactorValueList(List<PersonTravelPolicyKeyfactorValue> travelPolicyKeyfactorValueList) {
		this.travelPolicyKeyfactorValueList = travelPolicyKeyfactorValueList;
	}

	/**
	 * @return the policyTravellerList
	 */
	public List<PolicyTraveller> getPolicyTravellerList() {
		return policyTravellerList;
	}

	/**
	 * @param policyTravellerList
	 *            the policyTravellerList to set
	 */
	public void setPolicyTravellerList(List<PolicyTraveller> policyTravellerList) {
		this.policyTravellerList = policyTravellerList;
	}

	/**
	 * @return the attachmentList
	 */
	public List<Attachment> getAttachmentList() {
		return attachmentList;
	}

	/**
	 * @param attachmentList
	 *            the attachmentList to set
	 */
	public void setAttachmentList(List<Attachment> attachmentList) {
		this.attachmentList = attachmentList;
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

	public double getTotalBasicTermPremium() {
		double termPermium = 0.0;
		termPermium = Utils.getTwoDecimalPoint(getBasicTermPremium());
		return termPermium;
	}

	public String getVehicleInfo() {
		StringBuffer result = new StringBuffer();
		Iterator<PolicyPersonTravelVehicle> iterator = policyPersonTravelVehicleList.iterator();
		while (iterator.hasNext()) {
			PolicyPersonTravelVehicle info = iterator.next();
			result.append(info.getUsageOfVehicle().getLabel() + " (" + info.getRegistrationNo() + ")");
			if (iterator.hasNext()) {
				result.append(", ");
			}
		}
		return result.toString();
	}

	public int getPeriodOfInsurance() {
		return Utils.daysBetween(departureDate, arrivalDate, false, true);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arrivalDate == null) ? 0 : arrivalDate.hashCode());
		long temp;
		temp = Double.doubleToLongBits(basicTermPremium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((departureDate == null) ? 0 : departureDate.hashCode());
		result = prime * result + ((express == null) ? 0 : express.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isRoundTrip ? 1231 : 1237);
		result = prime * result + noOfPassenger;
		result = prime * result + paymentTerm;
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(premiumRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		temp = Double.doubleToLongBits(sumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(totalUnit);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((travelPath == null) ? 0 : travelPath.hashCode());
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
		PersonTravelPolicyInfo other = (PersonTravelPolicyInfo) obj;
		if (arrivalDate == null) {
			if (other.arrivalDate != null)
				return false;
		} else if (!arrivalDate.equals(other.arrivalDate))
			return false;
		if (Double.doubleToLongBits(basicTermPremium) != Double.doubleToLongBits(other.basicTermPremium))
			return false;
		if (departureDate == null) {
			if (other.departureDate != null)
				return false;
		} else if (!departureDate.equals(other.departureDate))
			return false;
		if (express == null) {
			if (other.express != null)
				return false;
		} else if (!express.equals(other.express))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isRoundTrip != other.isRoundTrip)
			return false;
		if (noOfPassenger != other.noOfPassenger)
			return false;
		if (paymentTerm != other.paymentTerm)
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (Double.doubleToLongBits(premiumRate) != Double.doubleToLongBits(other.premiumRate))
			return false;
		if (recorder == null) {
			if (other.recorder != null)
				return false;
		} else if (!recorder.equals(other.recorder))
			return false;
		if (Double.doubleToLongBits(sumInsured) != Double.doubleToLongBits(other.sumInsured))
			return false;
		if (Double.doubleToLongBits(totalUnit) != Double.doubleToLongBits(other.totalUnit))
			return false;
		if (travelPath == null) {
			if (other.travelPath != null)
				return false;
		} else if (!travelPath.equals(other.travelPath))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	public double getTotalPremium() {
		double result = 0;
		if (policyTravellerList.size() > 1) {
			for (PolicyTraveller insuredPerson : this.getPolicyTravellerList()) {
				result += insuredPerson.getPremium();
			}
		} else {
			result = Utils.getTwoDecimalPoint(getPremium());
		}
		return result;
	}

}
