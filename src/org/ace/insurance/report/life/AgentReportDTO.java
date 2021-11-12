package org.ace.insurance.report.life;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class AgentReportDTO {
	
	private String agentName;
	private String liscenseno;
	private String phoneno;
	private String residentaddress;
	private String policyno;
	private String payablereceiptno;
	@Temporal(TemporalType.TIMESTAMP)
	private Date outstandingdate;
	private String sanctionNo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date sanctionDate;
	private String invoiceNo;
	@Temporal(TemporalType.TIMESTAMP)
	private Date invoiceDate;
	private String voucherno;
	@Temporal(TemporalType.TIMESTAMP)
	private Date paymentDate;
	private double commission;
	private String insuranceType;
	private String remark;
	
	public AgentReportDTO(String agentName,String liscenseno,String phoneno,String residentaddress,String policyno,String payablereceiptno,Date outstandingdate,String sanctionNo,
			Date sanctionDate,String invoiceNo,Date invoiceDate,String voucherno,Date paymentDate,double commission,String insuranceType,String remark)
	{
		this.agentName=agentName;
		this.liscenseno=liscenseno;
		this.phoneno=phoneno;
		this.residentaddress=residentaddress;
		this.policyno=policyno;
		this.payablereceiptno=payablereceiptno;
		this.outstandingdate=outstandingdate;
		this.sanctionNo=sanctionNo;
		this.sanctionDate=sanctionDate;
		this.invoiceNo=invoiceNo;
		this.invoiceDate=invoiceDate;
		this.voucherno=voucherno;
		this.paymentDate=paymentDate;
		this.commission=commission;
		this.insuranceType=insuranceType;
		this.remark=remark;
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
	public String getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}
	public String getResidentaddress() {
		return residentaddress;
	}
	public void setResidentaddress(String residentaddress) {
		this.residentaddress = residentaddress;
	}
	public String getPolicyno() {
		return policyno;
	}
	public void setPolicyno(String policyno) {
		this.policyno = policyno;
	}
	public String getPayablereceiptno() {
		return payablereceiptno;
	}
	public void setPayablereceiptno(String payablereceiptno) {
		this.payablereceiptno = payablereceiptno;
	}
	public Date getOutstandingdate() {
		return outstandingdate;
	}
	public void setOutstandingdate(Date outstandingdate) {
		this.outstandingdate = outstandingdate;
	}
	public String getSanctionNo() {
		return sanctionNo;
	}
	public void setSanctionNo(String sanctionNo) {
		this.sanctionNo = sanctionNo;
	}
	public Date getSanctionDate() {
		return sanctionDate;
	}
	public void setSanctionDate(Date sanctionDate) {
		this.sanctionDate = sanctionDate;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getVoucherno() {
		return voucherno;
	}
	public void setVoucherno(String voucherno) {
		this.voucherno = voucherno;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public double getCommission() {
		return commission;
	}
	public void setCommission(double commission) {
		this.commission = commission;
	}
	public String getInsuranceType() {
		return insuranceType;
	}
	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
	
	
}
