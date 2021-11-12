package org.ace.insurance.report.life;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class MKTforHealthReportDTO {

	private String id;
	private String policyNo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date receipt;
	private String productId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	private double amount;
	private double ape;
	private int due;
	private int news;
	private String paymentType;
	private int period;
	private String salepoint;
	private double sumInsured;
	private int paymentYear;
	private String productName;
	private String saleChannel;
	private String customerName;
	private String agentName;
	private String liscenseno;
	

	public MKTforHealthReportDTO() {
	}

	public MKTforHealthReportDTO(String id, Date receipt, String policyNo, Date startDate, Date endDate, double sumInsured, String productName, double amount, double ape, int due,
			int news, String paymentType, int period, String salepoint, String saleChannel, String customerName,String agentName,String liscenseno ) {

		this.id = id;
		this.receipt = receipt;
		this.policyNo = policyNo;
		// this.productId = productId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.sumInsured = sumInsured;
		this.productName = productName;
		this.amount = amount;
		this.ape = ape;
		this.due = due;
		this.news = news;
		this.paymentType = paymentType;
		this.period = period;
		this.salepoint = salepoint;
		this.saleChannel = saleChannel;
		this.customerName = customerName;
		this.agentName=agentName;
		this.liscenseno=liscenseno;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public int getPaymentYear() {
		return paymentYear;
	}

	public void setPaymentYear(int paymentYear) {
		this.paymentYear = paymentYear;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Date getReceipt() {
		return receipt;
	}

	public void setReceipt(Date receipt) {
		this.receipt = receipt;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getApe() {
		return ape;
	}

	public void setApe(double ape) {
		this.ape = ape;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public int getDue() {
		return due;
	}

	public void setDue(int due) {
		this.due = due;
	}

	public int getNews() {
		return news;
	}

	public void setNews(int news) {
		this.news = news;
	}

	public String getSalepoint() {
		return salepoint;
	}

	public void setSalepoint(String salepoint) {
		this.salepoint = salepoint;
	}

	public String getSaleChannel() {
		return saleChannel;
	}

	public void setSaleChannel(String saleChannel) {
		this.saleChannel = saleChannel;
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

	public String getLiscenseno() {
		return liscenseno;
	}

	public void setLiscenseno(String liscenseno) {
		this.liscenseno = liscenseno;
	}		
}
