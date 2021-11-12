package org.ace.insurance.report.claim;

import java.util.Date;

import org.ace.insurance.common.HospitalCase;
import org.ace.insurance.life.policy.PolicyInsuredPerson;

public class LifeClaimMedicalFeeDTO {

	private String policyNo;

	private String id;

	private Date claimDate;

	private Date medicalFeesStartDate;

	private String insuredPersonName;

	private Date paidDate;

	private String claimNo;

	private String hospital;

	private double medicalFee;

	private String sanctionNo;

	private Date sanctionDate;

	private PolicyInsuredPerson policyInsuredPerson;

	private String branchId;

	private String medicalCase;

	private Date medicalreportDate;

	private Date informDate;

	private String hospitalInformDate;

	private Date medicalDate;

	public LifeClaimMedicalFeeDTO(String id, String policyNo, Date claimDate, PolicyInsuredPerson policyInsuredPerson, String claimNo, double medicalFee, String hospital,
			String branchId, HospitalCase medicalCase, Date informDate, Date medicalDate) {

		this.id = id;
		this.policyNo = policyNo;
		this.medicalFeesStartDate = claimDate;
		this.insuredPersonName = policyInsuredPerson.getFullName();
		this.claimNo = claimNo;
		this.medicalFee = medicalFee;
		this.hospital = hospital;
		this.branchId = branchId;
		if (HospitalCase.GP.equals(medicalCase)) {
			this.medicalCase = "GP";
		} else {
			this.medicalCase = "SP";
		}
		this.informDate = informDate;
		this.medicalDate = medicalDate;
	}

	public LifeClaimMedicalFeeDTO(String sanctionNo, double medicalFees, String hospital, String branchId, Date santionDate) {
		super();
		this.hospital = hospital;
		this.branchId = branchId;
		this.medicalFee = medicalFees;
		this.hospital = hospital;
		this.sanctionDate = santionDate;
		this.sanctionNo = sanctionNo;
	}

	public LifeClaimMedicalFeeDTO(String id, String policyNo, Date claimDate, PolicyInsuredPerson policyInsuredPerson, String claimNo, double medicalFee, String hospital,
			String branchId, HospitalCase medicalCase, Date medicalreportDate) {
		super();
		this.id = id;
		this.policyNo = policyNo;
		this.medicalFeesStartDate = claimDate;
		this.insuredPersonName = policyInsuredPerson.getFullName();
		this.claimNo = claimNo;
		this.medicalFee = medicalFee;
		this.hospital = hospital;
		this.branchId = branchId;
		if (HospitalCase.GP.equals(medicalCase)) {
			this.medicalCase = "GP";
		} else {
			this.medicalCase = "SP";
		}
		this.medicalreportDate = medicalreportDate;
	}

	public double getMedicalFee() {
		return medicalFee;
	}

	public void setMedicalFee(double medicalFee) {
		this.medicalFee = medicalFee;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public LifeClaimMedicalFeeDTO() {
		super();
	}

	public Date getClaimDate() {
		return claimDate;
	}

	public void setClaimDate(Date claimDate) {
		this.claimDate = claimDate;
	}

	public String getInsuredPersonName() {
		return insuredPersonName;
	}

	public void setInsuredPersonName(String insuredPersonName) {
		this.insuredPersonName = insuredPersonName;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}

	public String getClaimNo() {
		return claimNo;
	}

	public void setClaimNo(String claimNo) {
		this.claimNo = claimNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public PolicyInsuredPerson getPolicyInsuredPerson() {
		return policyInsuredPerson;
	}

	public void setPolicyInsuredPerson(PolicyInsuredPerson policyInsuredPerson) {
		this.policyInsuredPerson = policyInsuredPerson;
	}

	public String getHospital() {
		return hospital;
	}

	public void setHospital(String hospital) {
		this.hospital = hospital;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}

	public String getMedicalCase() {
		return medicalCase;
	}

	public void setMedicalCase(String medicalCase) {
		this.medicalCase = medicalCase;
	}

	public Date getMedicalFeesStartDate() {
		return medicalFeesStartDate;
	}

	public void setMedicalFeesStartDate(Date medicalFeesStartDate) {
		this.medicalFeesStartDate = medicalFeesStartDate;
	}

	public Date getMedicalreportDate() {
		return medicalreportDate;
	}

	public void setMedicalreportDate(Date medicalreportDate) {
		this.medicalreportDate = medicalreportDate;
	}

	public Date getInformDate() {
		return informDate;
	}

	public void setInformDate(Date informDate) {
		this.informDate = informDate;
	}

	public String toString() {
		return medicalDate.toString();
	}

	public String getHospitalInformDate() {
		return hospitalInformDate;
	}

	public void setHospitalInformDate(String hospitalInformDate) {
		this.hospitalInformDate = hospitalInformDate;
	}

}