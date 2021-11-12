package org.ace.insurance.medical.policy;

import org.ace.insurance.common.ISorter;
import org.ace.insurance.common.Name;

public class MPL002 implements ISorter {
	private static final long serialVersionUID = 1L;
	private String id;
	private String policyNo;
	private String customerName;
	private String branch;

	public MPL002() {
	}

	public MPL002(String id, String policyNo, String initialId, Name name, String organization, String branch) {
		this.id = id;
		this.policyNo = policyNo;
		this.customerName = initialId != null ? initialId + " " + name.getFullName() : organization;
		this.branch = branch;
	}

	public String getId() {
		return id;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBranch() {
		return branch;
	}

	@Override
	public String getRegistrationNo() {
		return policyNo;
	}

}
