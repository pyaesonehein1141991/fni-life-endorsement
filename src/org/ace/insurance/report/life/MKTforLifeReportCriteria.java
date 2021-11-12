package org.ace.insurance.report.life;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MKTforLifeReportCriteria {
	private Date startDate;
	private Date endDate;
	private List<String> productIdList;
	private String policyId;
	private String policyNo;

	public MKTforLifeReportCriteria() {
	}

	public MKTforLifeReportCriteria(Date startDate, Date endDate, List<String> productIdList, String policyId, String policyNo) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.productIdList = productIdList;
		this.policyId = policyId;
		this.policyNo = policyNo;
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

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

}
