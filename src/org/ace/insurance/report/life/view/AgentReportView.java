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
@Table(name = TableName.VWT_AGENT)
@ReadOnly
public class AgentReportView {
	@Id
	private String id;
	
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
		
	

}
