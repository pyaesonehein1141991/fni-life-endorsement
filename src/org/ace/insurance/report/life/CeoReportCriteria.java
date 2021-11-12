package org.ace.insurance.report.life;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.ace.insurance.web.common.SaleChannelType;

public class CeoReportCriteria {
	// private int year;
	// private int month;
	private List<String> productIdList;
	private int periodYear;

	@Enumerated(EnumType.STRING)
	private SaleChannelType saleChannelType;
	private String salePointId;
	private String salePointName;

	public CeoReportCriteria() {
	}

	public CeoReportCriteria(int month, int year, String branch, List<String> productIdList) {
		// this.month=month;
		// this.year=year;
		this.productIdList = productIdList;
	}

	/*
	 * public int getYear() { return year; }
	 * 
	 * public void setYear(int year) { this.year = year; }
	 * 
	 * 
	 * public int getMonth() { return month; }
	 * 
	 * public void setMonth(int month) { this.month = month; }
	 */

	public List<String> getProductIdList() {
		if (productIdList == null) {
			return productIdList = new ArrayList<String>();
		} else {
			return productIdList;
		}

	}

	public void setProductIdList(List<String> productIdList) {
		this.productIdList = productIdList;
	}

	public int getPeriodYear() {
		return periodYear;
	}

	public void setPeriodYear(int periodYear) {
		this.periodYear = periodYear;
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

}
