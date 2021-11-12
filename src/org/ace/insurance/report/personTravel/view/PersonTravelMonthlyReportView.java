package org.ace.insurance.report.personTravel.view;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.Utils;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_FNI_PERSONTRAVEL_MONTHLY_INCOME)
@ReadOnly
public class PersonTravelMonthlyReportView {
	@Id
	private String id;
	private String receiptNo;
	private String customerName;
	private String travelPath;
	private String agentName;
	private String registrationNo;
	private double sumInsured;
	private double premium;
	private int unit;
	private int noOfPassenger;
	private double commission;
	@Temporal(TemporalType.DATE)
	private Date arrivalDate;
	@Temporal(TemporalType.DATE)
	private Date departureDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	private String branchId;
	private String productId;

	public PersonTravelMonthlyReportView() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getRegistrationNo() {
		return registrationNo;
	}

	public void setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public String getTravelPath() {
		return travelPath;
	}

	public void setTravelPath(String travelPath) {
		this.travelPath = travelPath;
	}

	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	public void setNoOfPassenger(int noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public Date getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
	}
	
	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getReceiptNoAndDate() {
		return getReceiptNo() + " (" + Utils.getDateFormatString(getPaymentDate()) + ")";
	}

	public String getTerm() {
		return "From" + " (" + Utils.getDateFormatString(getDepartureDate()) + ") " + "To" + " (" + Utils.getDateFormatString(getArrivalDate()) + ")";
	}

	public double getTotalPremium() {
		return getPremium() - getCommission();
	}
}
