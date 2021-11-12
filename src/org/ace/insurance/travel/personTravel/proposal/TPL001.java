package org.ace.insurance.travel.personTravel.proposal;

import java.util.Date;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.web.common.SaleChannelType;

public class TPL001 implements ISorter {
	private static final long serialVersionUID = 1L;
	private String id;
	private String proposalNo;
	private String policyNo;
	private String customerName;
	private SaleChannelType saleChannel;
	private String agent;
	private String Branch;
	private double premium;
	private double totalUnit;
	private double sumInsured;
	private int noOfPassenger;
	private Date depatureDate;
	private Date arrivalDate;

	public TPL001() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TPL001(String id, String proposalNo, String policyNo, String customerName, SaleChannelType saleChannel, String agent, String branch, double premium, double totalUnit,
			double sumInsured, int noOfPassenger, Date depatureDate, Date arrivalDate) {
		super();
		this.id = id;
		this.proposalNo = proposalNo;
		this.policyNo = policyNo;
		this.customerName = customerName;
		this.saleChannel = saleChannel;
		this.agent = agent;
		this.Branch = branch;
		this.premium = premium;
		this.totalUnit = totalUnit;
		this.sumInsured = sumInsured;
		this.noOfPassenger = noOfPassenger;
		this.depatureDate = depatureDate;
		this.arrivalDate = arrivalDate;
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

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public SaleChannelType getSaleChannel() {
		return saleChannel;
	}

	public void setSaleChannel(SaleChannelType saleChannel) {
		this.saleChannel = saleChannel;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public String getBranch() {
		return Branch;
	}

	public void setBranch(String branch) {
		Branch = branch;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getTotalUnit() {
		return totalUnit;
	}

	public void setTotalUnit(double totalUnit) {
		this.totalUnit = totalUnit;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	public void setNoOfPassenger(int noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}

	public Date getDepatureDate() {
		return depatureDate;
	}

	public void setDepatureDate(Date depatureDate) {
		this.depatureDate = depatureDate;
	}

	public Date getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	@Override
	public String getRegistrationNo() {
		// TODO Auto-generated method stub
		return null;
	}

}
