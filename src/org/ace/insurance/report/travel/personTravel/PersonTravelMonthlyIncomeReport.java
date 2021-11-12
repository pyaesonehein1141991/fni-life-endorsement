package org.ace.insurance.report.travel.personTravel;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.PERSONTRAVELMONTHLYINCOMEREPORT)
@ReadOnly
public class PersonTravelMonthlyIncomeReport implements Serializable {

	private static final long serialVersionUID = -6470147111818154559L;
	@Id
	private String id;
	private String productId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	private String proposalNo;
	private String policyNo;
	private String expressName;
	private String agentId;
	private String agentName;
	private String insurers;
	private String travelPath;
	private String vehicleNo;
	private int travelDays;
	private int totalUnit;
	private double sumInsured;
	private double premium;
	private double commission;
	private double netPremium;
	private String branchId;
	private String branchName;

	public String getId() {
		return id;
	}

	public String getProductId() {
		return productId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getExpressName() {
		return expressName;
	}

	public String getAgentId() {
		return agentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getInsurers() {
		return insurers;
	}

	public String getTravelPath() {
		return travelPath;
	}

	public String getVehicleNo() {
		return vehicleNo;
	}

	public int getTravelDays() {
		return travelDays;
	}

	public int getTotalUnit() {
		return totalUnit;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public double getPremium() {
		return premium;
	}

	public double getCommission() {
		return commission;
	}

	public double getNetPremium() {
		return netPremium;
	}

	public String getBranchId() {
		return branchId;
	}

	public String getBranchName() {
		return branchName;
	}

}
