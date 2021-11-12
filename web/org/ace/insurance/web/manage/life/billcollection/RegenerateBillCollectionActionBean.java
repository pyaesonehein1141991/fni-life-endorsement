package org.ace.insurance.web.manage.life.billcollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;

import org.ace.insurance.common.DateSorter;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.life.endorsement.LifeEndorseChange;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.LifeEndorseInsuredPerson;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.policyExtraAmount.PolicyExtraAmount;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentDelegateService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.web.common.document.DocumentBuilder;
import org.ace.java.web.common.BaseBean;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;

@ViewScoped
@ManagedBean(name = "RegenerateBillCollectionActionBean")
public class RegenerateBillCollectionActionBean extends BaseBean {

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{PaymentDelegateService}")
	private IPaymentDelegateService paymentDelegateService;

	public void setPaymentDelegateService(IPaymentDelegateService paymentDelegateService) {
		this.paymentDelegateService = paymentDelegateService;
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

	@ManagedProperty(value = "#{LifePolicyHistoryService}")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	public void setLifePolicyHistoryService(ILifePolicyHistoryService lifePolicyHistoryService) {
		this.lifePolicyHistoryService = lifePolicyHistoryService;
	}

	
	@ManagedProperty(value = "#{LifeEndorsementService}")
	private ILifeEndorsementService lifeEndorsementService;

	public void setLifeEndorsementService(ILifeEndorsementService lifeEndorsementService) {
		this.lifeEndorsementService = lifeEndorsementService;
	}
	private List<BC0001> billCollectionList;
	private PolicyCriteria policyCriteria;
	private List<BillCollectionCashReceiptDTO> cashReceiptDTOList;
	private Map<String, BC0001> paymentMap;
	private List<LifeEndorseInsuredPerson> lifeEndorseInsuredPersonlist;
	private List<LifeEndorseChange> lifeEndorseChangelist;
	private LifeEndorseInfo lifeEndorseInfo;
	private PolicyExtraAmount policyExtraAmount;
	private LifeEndorseChange lifeEndorseChange;

	@PostConstruct
	public void init() {
		policyCriteria = new PolicyCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		policyCriteria.setReferenceType(ReferenceType.ENDOWMENT_LIFE);
		cashReceiptDTOList = new ArrayList<BillCollectionCashReceiptDTO>();
	}

	public void search() {
		billCollectionList = paymentService.findBCPaymentByPolicyNo(policyCriteria);
		paymentMap = new HashMap<String, BC0001>();
		DateSorter<BC0001> sorter = new DateSorter<BC0001>(billCollectionList);
		for (BC0001 dto : billCollectionList) {
			paymentMap.put(dto.getPolicyNo(), dto);
		}
		for (Map.Entry<String, BC0001> entry : paymentMap.entrySet()) {
			BC0001 BC0001 = entry.getValue();
			BC0001.setLastbillcollection(true);
		}
	}

	public List<BC0001> getBillCollectionList() {
		return billCollectionList;
	}

	private final String reportName = "LifeBillCollection";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateCashReceipt(BC0001 dto) {
		if (dto.getReceiptNo() == null) {
			PrimeFaces.current().executeScript("PF('informationDialog').show();");
		} else {
			if (PolicyReferenceType.LIFE_BILL_COLLECTION.equals(dto.getReferenceType()) || PolicyReferenceType.STUDENT_LIFE_POLICY_BILL_COLLECTION.equals(dto.getReferenceType())
					|| PolicyReferenceType.SHORT_ENDOWMENT_LIFE_BILL_COLLECTION.equals(dto.getReferenceType())) {
				LifePolicy lifePolicy;
				lifePolicy = lifePolicyService.findLifePolicyById(dto.getReferenceNo());
				Payment payment = paymentService.findByInvoiceNo(dto.getInvoiceNo());
				lifeEndorseInfo = lifeEndorsementService.findLastLifeEndorseInfoByPolicyNO(lifePolicy.getPolicyNo());
				if(lifeEndorseInfo!=null) {
				lifeEndorseInsuredPersonlist = lifeEndorsementService.findInsuredPerson(lifeEndorseInfo.getId());
				policyExtraAmount=lifeEndorsementService.findpolicyExtaAmount(lifePolicy.getLifeProposal().getProposalNo());
				List<String> policyidList = new ArrayList<>();
				for (LifeEndorseInsuredPerson li : lifeEndorseInsuredPersonlist) {
					policyidList.add(li.getId());
				}
				lifeEndorseChangelist = lifeEndorsementService.findEndorseChangbyInsuredPersonId(policyidList);
				}
				DocumentBuilder.generateLifeBillCollectionCashReceipt(lifePolicy, payment,lifeEndorseChangelist,policyExtraAmount,dirPath, fileName);
			} else if (PolicyReferenceType.HEALTH_POLICY_BILL_COLLECTION.equals(dto.getReferenceType())
					|| PolicyReferenceType.CRITICAL_ILLNESS_POLICY_BILL_COLLECTION.equals(dto.getReferenceType())) {
				MedicalPolicy medicalPolicy;
				medicalPolicy = medicalPolicyService.findMedicalPolicyById(dto.getReferenceNo());
				Payment payment = paymentService.findByInvoiceNo(dto.getInvoiceNo());
				
				DocumentBuilder.generateMedicalBillCollectionCashReceipt(medicalPolicy, payment, dirPath, fileName);
			} else {
				addErrorMessage("billCollectionForm:receiptTable", UIInput.REQUIRED_MESSAGE_ID);
			}
		}
	}

	public void generateReport(BC0001 dto) {
		cashReceiptDTOList.clear();
		BillCollectionCashReceiptDTO cashdto = new BillCollectionCashReceiptDTO();
		Payment payment = paymentService.findByInvoiceNo(dto.getInvoiceNo());
		cashdto.setPayment(payment);
		cashdto.setReferenceType(policyCriteria.getReferenceType());
		if (ReferenceType.HEALTH.equals(cashdto.getReferenceType()) || ReferenceType.CRITICAL_ILLNESS.equals(cashdto.getReferenceType())) {
			MedicalPolicy policy = medicalPolicyService.findMedicalPolicyById(dto.getPolicyId());
			cashdto.setMedicalPolicy(policy);
		} else {
			LifePolicy policy = lifePolicyService.findLifePolicyById(dto.getPolicyId());
			cashdto.setLifePolicy(policy);
			lifeEndorseInfo = lifeEndorsementService.findLastLifeEndorseInfoByPolicyId(cashdto.getLifePolicy().getId());
			if(lifeEndorseInfo!=null) {
			lifeEndorseInsuredPersonlist = lifeEndorsementService.findInsuredPerson(lifeEndorseInfo.getId());
			lifeEndorseChange = lifeEndorsementService.findEndorseChangOnebyInsuredPersonId(lifeEndorseInsuredPersonlist.get(0).getId());
			policyExtraAmount=lifeEndorsementService.findpolicyExtaAmount(cashdto.getLifePolicy().getProposalNo());
		}
		}
		cashReceiptDTOList.add(cashdto);
		
		
		DocumentBuilder.generateBillCollectionPaymentInvoice(cashReceiptDTOList,lifeEndorseChange,policyExtraAmount, dirPath, fileName);
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PolicyCriteria getPolicyCriteria() {
		return policyCriteria;
	}

	public void setPolicyCriteria(PolicyCriteria policyCriteria) {
		this.policyCriteria = policyCriteria;
	}

	public ReferenceType[] getReferenceTypeSelectedItemList() {
		return new ReferenceType[] { ReferenceType.ENDOWMENT_LIFE, ReferenceType.STUDENT_LIFE, ReferenceType.SHORT_ENDOWMENT_LIFE, ReferenceType.HEALTH,
				ReferenceType.CRITICAL_ILLNESS };
	}

}
