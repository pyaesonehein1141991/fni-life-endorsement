package org.ace.insurance.web.manage.medical;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonBeneficiaries;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.java.web.common.BaseBean;

@RequestScoped
@ManagedBean(name = "MedicalProposalTemplateBean")
public class MedicalProposalTemplateBean extends BaseBean {

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	private MedicalProposal medicalProposal;
	private MedicalPolicy medicalPolicy;
	private List<WorkFlowHistory> workFlowList;
	private boolean isShowPolicy;
	int age;
	Date startDate;
	Date dateOfBirth;

	@SuppressWarnings("unchecked")
	private void initialization() {
		medicalProposal = (MedicalProposal) getParam("medicalProposal");
		workFlowList = (List<WorkFlowHistory>) getParam("workFlowList");
	}

	@PostConstruct
	public void init() {
		initialization();
		startDate=medicalProposal.getStartDate();
		//dateOfBirth=medicalProposal.getMedicalProposalInsuredPersonList().get(0).getInsuredPersonBeneficiariesList().get(0).getDateOfBirth();
		 //age=AgeForNextYearBF(startDate,dateOfBirth);
	}

	public MedicalPolicy getMedicalPolicy() {
		return medicalPolicy;
	}

	public MedicalProposal getMedicalProposal() {
		return medicalProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowList;
	}

	public boolean isShowPolicy() {
		return isShowPolicy;
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public int ageForNextYearBF(MedicalProposalInsuredPersonBeneficiaries beneficial) {
		Calendar cal_1 = Calendar.getInstance();
		cal_1.setTime(startDate);
		int currentYear = cal_1.get(Calendar.YEAR);
		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(beneficial.getDateOfBirth());
		cal_2.set(Calendar.YEAR, currentYear);
		if (startDate.after(cal_2.getTime())) {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(beneficial.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR) + 1;
			return year_2 - year_1;
		} else {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(beneficial.getDateOfBirth());
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR);
			return year_2 - year_1;
		}
	}
	
	
}
