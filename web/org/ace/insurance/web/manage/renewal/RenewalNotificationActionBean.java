package org.ace.insurance.web.manage.renewal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.Gender;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.life.claim.LifePolicySearch;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.MedicalKeyFactorValue;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAddOn;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.renewal.RenewalNotification;
import org.ace.insurance.renewal.RenewalNotificationCriteria;
import org.ace.insurance.renewal.service.interfaces.IRenewalNotificationService;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.salesPoints.SalesPoints;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.document.DocumentBuilder;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuAddOnDTO;
import org.ace.insurance.web.manage.medical.proposal.MedProInsuDTO;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "RenewalNotificationActionBean")
public class RenewalNotificationActionBean extends BaseBean {

	@ManagedProperty(value = "#{RenewalNotificationService}")
	private IRenewalNotificationService notificationService;

	public void setNotificationService(IRenewalNotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@ManagedProperty(value = "#{MedicalProposalService}")
	private IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
	}
	
	private List<RenewalNotification> notificationList;
	private List<RenewalNotification> selectedList;
	private List<PolicyReferenceType> policyReferenceTypes;
	private RenewalNotificationCriteria criteria;
	private MedicalProposal medicalProposal;
	private MedProInsuDTO insuredPersonDTO;

	// for renewral
	private String reportName = null;
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private String fileName = null;

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	@PostConstruct
	public void init() {
		createNewCriteria();
		loadPolicyReferenceType();
	}

	private void createNewCriteria() {
		criteria = new RenewalNotificationCriteria();
	}

	public void search() {
		notificationList = notificationService.findPoliciesByCriteria(criteria);
	}

	public void reset() {
		notificationList = null;
		selectedList = null;
		createNewCriteria();
	}

	public void returnSalesPoints(SelectEvent event) {
		SalesPoints salesPoints = (SalesPoints) event.getObject();
		criteria.setSalesPoints(salesPoints);
	}
	
	public void returnLifePolicyNo(SelectEvent event) {
		LifePolicySearch lifePolicySearch = (LifePolicySearch) event.getObject();
		criteria.setPolicyNo(lifePolicySearch.getPolicyNo());
	}

	public List<RenewalNotification> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<RenewalNotification> selectedList) {
		this.selectedList = selectedList;
	}

	public List<RenewalNotification> getNotificationList() {
		return notificationList;
	}

	public RenewalNotificationCriteria getCriteria() {
		return criteria;
	}

	public List<PolicyReferenceType> getPolicyReferenceTypes() {
		return policyReferenceTypes;
	}

	private void loadPolicyReferenceType() {
		policyReferenceTypes = Arrays.asList(PolicyReferenceType.PA_POLICY, PolicyReferenceType.FARMER_POLICY, PolicyReferenceType.SNAKE_BITE_POLICY,
				PolicyReferenceType.GROUP_LIFE_POLICY, PolicyReferenceType.SPORT_MAN_POLICY, PolicyReferenceType.ENDOWNMENT_LIFE_POLICY,
				PolicyReferenceType.SHORT_ENDOWMENT_LIFE_POLICY, PolicyReferenceType.HEALTH_POLICY, PolicyReferenceType.MICRO_HEALTH_POLICY,
				PolicyReferenceType.CRITICAL_ILLNESS_POLICY);
	}
	
	/* set keyfactorValue of product and addOn */
//	private void loadKeyFactorValueList(MedProInsuDTO insuredPersonDTO) {
//
//		Date startDate;
//		Date dateOfBirth = insuredPersonDTO.getCustomer().getDateOfBirth();
//		Date oldEndDate = medicalProposal.getEndDate();
//		int age = Utils.getAgeForNextYear(dateOfBirth, oldEndDate);
//		Date currentDate = new Date();
//		if (oldEndDate.after(currentDate)) {
//			startDate = oldEndDate;
//		} else {
//			startDate = currentDate;
//		}
//		insuredPersonDTO.setAge(age);
//		Gender gender = insuredPersonDTO.getCustomer().getGender();
//		PaymentType paymentType = medicalProposal.getPaymentType();
//		for (MedicalKeyFactorValue kfv : insuredPersonDTO.getKeyFactorValueList()) {
//			if (KeyFactorChecker.isGender(kfv.getKeyFactor())) {
//				kfv.setValue(gender + "");
//			} else if (KeyFactorChecker.isMedicalAge(kfv.getKeyFactor())) {
//				kfv.setValue(age + "");
//			} else if (KeyFactorChecker.isPaymentType(kfv.getKeyFactor())) {
//				kfv.setValue(paymentType.getId());
//			}
//		}
//		for (MedProInsuAddOnDTO mAddon : insuredPersonDTO.getInsuredPersonAddOnList()) {
//			for (MedicalKeyFactorValue akv : mAddon.getKeyFactorValueList()) {
//				if (KeyFactorChecker.isGender(akv.getKeyFactor())) {
//					akv.setValue(gender + "");
//				} else if (KeyFactorChecker.isMedicalAge(akv.getKeyFactor())) {
//					akv.setValue(age + "");
//				} else if (KeyFactorChecker.isPaymentType(akv.getKeyFactor())) {
//					akv.setValue(paymentType.getId());
//				}
//			}
//		}
//	}

	public void generateReport() {
		if (selectedList.size() == 0) {
			addErrorMessage("paymentNotificationForm:lifePolicyInfoTable", MessageId.ATLEAST_ONE_CHECK_REQUIRED);
			return;
		} else if (selectedList.size() > 0) {
			if (PolicyReferenceType.CRITICAL_ILLNESS_POLICY.equals(criteria.getPolicyReferenceType()) || PolicyReferenceType.HEALTH_POLICY.equals(criteria.getPolicyReferenceType())
					|| PolicyReferenceType.MICRO_HEALTH_POLICY.equals(criteria.getPolicyReferenceType())) {
				reportName = "MedicalRenewalLetter";
				fileName = reportName + ".pdf";
				List<MedicalPolicy> policies = new ArrayList<MedicalPolicy>();
				for (RenewalNotification dto : selectedList) {
					MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByPolicyNo(dto.getPolicyNo());
					policies.add(medicalPolicy);
					//update code for renewal premium calculation
					String medicalProposalId = medicalPolicy.getMedicalProposal().getId();
					medicalProposal = medicalProposalService.findMedicalProposalById(medicalProposalId);
				}

				//update code for renewal premium calculation
				Date dateOfBirth = medicalProposal.getCustomer().getDateOfBirth();
				//Date currentDate = medicalProposal.getEndDate();
				Date current_date = new Date();  
				int age = Utils.getAgeForNextYear(dateOfBirth, current_date) + 1;
				Gender gender = medicalProposal.getCustomer().getGender();
				PaymentType paymentType = medicalProposal.getPaymentType();
				//LoadKeyFactorValues
				for (MedicalKeyFactorValue kfv : medicalProposal.getMedicalProposalInsuredPersonList().get(0).getKeyFactorValueList()) {
					if (KeyFactorChecker.isGender(kfv.getKeyFactor())) {
						kfv.setValue(gender + "");
					} else if (KeyFactorChecker.isMedicalAge(kfv.getKeyFactor())) {
						kfv.setValue(age + "");
					} else if (KeyFactorChecker.isPaymentType(kfv.getKeyFactor())) {
						kfv.setValue(paymentType.getId());
					}
				}
				for (MedicalProposalInsuredPersonAddOn mAddon : medicalProposal.getMedicalProposalInsuredPersonList().get(0).getInsuredPersonAddOnList()) {
					for (MedicalKeyFactorValue akv : mAddon.getKeyFactorValueList()) {
						if (KeyFactorChecker.isGender(akv.getKeyFactor())) {
							akv.setValue(gender + "");
						} else if (KeyFactorChecker.isMedicalAge(akv.getKeyFactor())) {
							akv.setValue(age + "");
						} else if (KeyFactorChecker.isPaymentType(akv.getKeyFactor())) {
							akv.setValue(paymentType.getId());
						}
					}
				}
				medicalProposal = medicalProposalService.calculatePremium(medicalProposal);
				DocumentBuilder.generateMedicalPolicyRenewalLetters(policies, medicalProposal, dirPath, fileName);
			} else {
				reportName = "LifePolicyRenewalLetter";
				fileName = reportName + ".pdf";
				if (selectedList.size() > 0) {
					List<LifePolicy> policies = new ArrayList<LifePolicy>();
					for (RenewalNotification dto : selectedList) {
						LifePolicy lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(dto.getPolicyNo());
						policies.add(lifePolicy);
					}
					DocumentBuilder.generateLifePolicyRenewalLetters(policies, dirPath, fileName);
				}
			}
		}

	}

	public MedProInsuDTO getInsuredPersonDTO() {
		return insuredPersonDTO;
	}

	public void setInsuredPersonDTO(MedProInsuDTO insuredPersonDTO) {
		this.insuredPersonDTO = insuredPersonDTO;
	}

	
}
