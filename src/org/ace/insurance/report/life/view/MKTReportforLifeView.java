package org.ace.insurance.report.life.view;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_MKTFORLIFE)
@ReadOnly
public class MKTReportforLifeView {
	
	
	@Id
	private String id;
	private String policyNo;
	private String productId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	private String productName;
	@Temporal(TemporalType.TIMESTAMP)
	private Date receipt;
	private double amount;
	private double ape;
	private int due;
	private int news;
	private String paymentType;
	private int period;
	private String salepoint;
    private double sumInsured; 
    private String saleChannel;
    private String customerName; 
    private String agentName;
	private String liscenseno;
	private String remark;

	
	public MKTReportforLifeView() {
		
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

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public double getSumInsured() {
		return sumInsured;
	}
	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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
	
	public void setReceiptDate(Date receipt) {
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

	public void setReceipt(Date receipt) {
		this.receipt = receipt;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
