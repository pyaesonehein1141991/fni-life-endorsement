package org.ace.insurance.report.life.view;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.ace.insurance.common.TableName;
import org.ace.insurance.web.common.SaleChannelType;
import org.eclipse.persistence.annotations.ReadOnly;

@Entity
@Table(name = TableName.VWT_CEO)
@ReadOnly
public class CeoReportView {

	@Id
	private String id;
	private String policyNo;
	private int term;
	private String paymentType;
	private String productId;
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	private double sumInsured;
	private int paymentYear;
	private String productName;
	@Enumerated(EnumType.STRING)
	private SaleChannelType saleChannelType;
	private String salePointId;
	private String salePointName;
	private String insuredpersonName;
	private double january;
	private double february;
	private double march;
	private double april;
	private double may;
	private double june;
	private double july;
	private double august;
	private double september;
	private double october;
	private double november;
	private double december;

	public CeoReportView() {
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

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public double getJanuary() {
		return january;
	}

	public void setJanuary(double january) {
		this.january = january;
	}

	public double getFebruary() {
		return february;
	}

	public void setFebruary(double february) {
		this.february = february;
	}

	public double getMarch() {
		return march;
	}

	public void setMarch(double march) {
		this.march = march;
	}

	public double getApril() {
		return april;
	}

	public void setApril(double april) {
		this.april = april;
	}

	public double getMay() {
		return may;
	}

	public void setMay(double may) {
		this.may = may;
	}

	public double getJune() {
		return june;
	}

	public void setJune(double june) {
		this.june = june;
	}

	public double getJuly() {
		return july;
	}

	public void setJuly(double july) {
		this.july = july;
	}

	public double getAugust() {
		return august;
	}

	public void setAugust(double august) {
		this.august = august;
	}

	public double getSeptember() {
		return september;
	}

	public void setSeptember(double september) {
		this.september = september;
	}

	public double getOctober() {
		return october;
	}

	public void setOctober(double october) {
		this.october = october;
	}

	public double getNovember() {
		return november;
	}

	public void setNovember(double november) {
		this.november = november;
	}

	public double getDecember() {
		return december;
	}

	public void setDecember(double december) {
		this.december = december;
	}

	public SaleChannelType getSaleChannelType() {
		return saleChannelType;
	}

	public void setSaleChannelType(SaleChannelType saleChannelType) {
		this.saleChannelType = saleChannelType;
	}

	public String getSalePointId() {
		return salePointId;
	}

	public void setSalePointId(String salePointId) {
		this.salePointId = salePointId;
	}

	public String getSalePointName() {
		return salePointName;
	}

	public void setSalePointName(String salePointName) {
		this.salePointName = salePointName;
	}

	public String getInsuredpersonName() {
		return insuredpersonName;
	}

	public void setInsuredpersonName(String insuredpersonName) {
		this.insuredpersonName = insuredpersonName;
	}

}
