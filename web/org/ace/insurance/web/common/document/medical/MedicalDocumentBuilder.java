package org.ace.insurance.web.common.document.medical;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.HealthType;
import org.ace.insurance.common.KeyFactorIDConfig;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonAddOn;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPersonBeneficiaries;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAddOn;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonBeneficiaries;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.document.JasperFactory;
import org.ace.insurance.web.common.document.JasperTemplate;
import org.ace.insurance.web.common.myanmarLanguae.MyanmarLanguae;
import org.ace.insurance.web.common.ntw.mym.AbstractMynNumConvertor;
import org.ace.insurance.web.common.ntw.mym.DefaultConvertor;
import org.ace.insurance.web.manage.life.billcollection.BillCollectionCashReceiptDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonDTO;
import org.ace.insurance.web.manage.medical.claim.approval.MedicalClaimApprovalActionBean;
import org.ace.insurance.web.manage.medical.claim.factory.MedicalPolicyInsuredPersonFactory;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.web.ApplicationSetting;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

public class MedicalDocumentBuilder extends JasperFactory {

	/** to generate Health,CriticalIllness and MicroHealth Acceptance Letter */
	public static List<JasperPrint> generateMedicalAcceptanceLetter(MedicalProposal medicalProposal, AcceptedInfo acceptedInfo) {
		Map<String, Object> paramMap = new HashMap<>();
		Customer customer = medicalProposal.getCustomer();
		Organization organization = medicalProposal.getOrganization();
		Product product = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getProduct();
		String customerName = "";
		String customerAddress = "";
		String customerPhone = "";
		if (customer != null) {
			customerName = customer.getFullName();
			customerAddress = customer.getFullAddress();
			customerPhone = customer.getContentInfo().getPhone();
		} else if (organization != null) {
			customerName = organization.getName();
			customerAddress = organization.getFullAddress();
			customerPhone = organization.getContentInfo().getPhone();
		}
		JasperPrint jprint = new JasperPrint();
		String basicPlus = "";
		int basicUnit = 0;
		int additional1 = 0;
		String additional1Name = "";
		int additional2 = 0;
		String additional2Name = "";
		Double termPremium = 0.0;
		double oneYearPremium = 0.0;
		double totalPremium = 0.0;
		String agentInfo = medicalProposal.getAgentNLiscenseNo();
		for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
			termPremium += person.getBasicTermPremium();
			oneYearPremium += person.getPremium();
			totalPremium += person.getTotalPremium();
			basicUnit += person.getUnit();
			for (MedicalProposalInsuredPersonAddOn addOn : person.getInsuredPersonAddOnList()) {
				if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getOperationAndMis())) {
					additional1Name = addOn.getAddOn().getProductContent().getName();
					additional1 += addOn.getUnit();
				} else if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getClinical())) {
					additional2Name = addOn.getAddOn().getProductContent().getName();
					additional2 += addOn.getUnit();
				}
			}
		}
		paramMap.put("customerName", customerName);
		paramMap.put("basicPlus", basicPlus);
		paramMap.put("additional1", additional1Name + " - " + additional1 + " Units, " + additional2Name + " - " + additional2 + " Units");
		paramMap.put("basicUnit", basicUnit + (basicUnit == 1 ? " Unit" : " Units"));
		paramMap.put("paymentType", medicalProposal.getPaymentType().getDescription());
		paramMap.put("oneYearPremium", oneYearPremium);
		paramMap.put("termPremium", termPremium);
		paramMap.put("startDate", medicalProposal.getStartDate());
		paramMap.put("endDate", medicalProposal.getEndDate());
		paramMap.put("phoneNo", customerPhone);
		paramMap.put("proposalNo", medicalProposal.getProposalNo());
		paramMap.put("periodYears", medicalProposal.getPeriodYears());
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("nbcTotalPremium", 0.0);
		paramMap.put("customerAddress", customerAddress);
		paramMap.put("currentDate", Utils.formattedDate(new Date()));
		paramMap.put("ownerLogo", ApplicationSetting.getMilogo());
		paramMap.put("servicesCharges", acceptedInfo.getServicesCharges());
		paramMap.put("discountAmount", acceptedInfo.getDiscountAmount());
		paramMap.put("stampFees", acceptedInfo.getStampFeesAmount());
		paramMap.put("netTermAmount", totalPremium + acceptedInfo.getServicesCharges() + acceptedInfo.getStampFeesAmount() - acceptedInfo.getDiscountAmount());
		paramMap.put("agent", agentInfo);

		if (KeyFactorChecker.isIndividualHealth(product.getId()) || KeyFactorChecker.isGroupHealth(product.getId()))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_HEALTH_INFORM_ACCEPT, new JREmptyDataSource());
		else if (KeyFactorChecker.isMicroHealth(product.getId()))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_INFORM_ACCEPT, new JREmptyDataSource());
		else if (KeyFactorChecker.isCriticalIllness(product.getId()) || KeyFactorChecker.isGroupCriticalIllness(product.getId()))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.CRITICAL_ILLNESS_INFORM_ACCEPT, new JREmptyDataSource());

		return Arrays.asList(jprint);
	}

	/** to generate Health,CriticalIllness and MicroHealth Reject Letter */
	public static List<JasperPrint> generateMedicalRejectLetter(MedicalProposal medicalProposal) {

		Map<String, Object> paramMap = new HashMap<>();

		Customer customer = medicalProposal.getCustomer();
		Organization organization = medicalProposal.getOrganization();
		String customerName = "";
		String customerAddress = "";
		String customerNrc = "";
		if (customer != null) {
			customerName = customer.getFullName();
			customerAddress = customer.getFullAddress();
			customerNrc = customer.getFullIdNo();
		} else if (organization != null) {
			customerName = organization.getName();
			customerAddress = organization.getFullAddress();
			customerNrc = organization.getRegNo();
		}
		MedicalProposalInsuredPerson medicalProposalInsuredPerson = medicalProposal.getMedicalProposalInsuredPersonList().get(0);
		paramMap.put("customerName", customerName);
		paramMap.put("customerAddress", customerAddress);
		paramMap.put("agent", medicalProposal.getAgent() != null ? medicalProposal.getAgent().getFullName() : "");
		paramMap.put("nrc", customerNrc);
		paramMap.put("proposalNo", medicalProposal.getProposalNo());
		paramMap.put("todayDate", Utils.formattedDate(new Date()));
		paramMap.put("ownerLogo", ApplicationSetting.getMilogo());

		JasperPrint jprint = new JasperPrint();
		if (KeyFactorChecker.isIndividualHealth(medicalProposalInsuredPerson.getProduct().getId())
				|| KeyFactorChecker.isGroupHealth(medicalProposalInsuredPerson.getProduct().getId()))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_INFORM_REJECT, new JREmptyDataSource());
		else if (KeyFactorChecker.isMicroHealth(medicalProposalInsuredPerson.getProduct().getId()))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_INFORM_REJECT, new JREmptyDataSource());
		else if (KeyFactorChecker.isCriticalIllness(medicalProposalInsuredPerson.getProduct().getId())
				|| KeyFactorChecker.isGroupCriticalIllness(medicalProposalInsuredPerson.getProduct().getId()))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.CRITICAL_ILLNESS_INFORM_REJECT, new JREmptyDataSource());
		return Arrays.asList(jprint);

	}

	/** to generate Health,CriticalIllness and MicroHealth Invoice Letter */
	public static List<JasperPrint> generateHealthPaymentInvoice(MedicalProposal medicalProposal, PaymentDTO payment) {
		String insuredPersonName = "";
		String invoiceDate = Utils.formattedDate(payment.getConfirmDate());
		String invoice = payment.getInvoiceNo();
		String proposalNo = medicalProposal.getProposalNo();
		String paymentType = medicalProposal.getPaymentType().getName();
		String customerName = medicalProposal.getCustomerName();
		MedicalProposalInsuredPerson insuredPerson = medicalProposal.getMedicalProposalInsuredPersonList().get(0);
		String address = medicalProposal.getCustomerAddress();
		String agentRegNo = medicalProposal.getAgentLiscenseNo();
		String agentName = medicalProposal.getAgentName();
		double termPremium = medicalProposal.getPaymentType().getMonth() == 0 ? 0.00 : medicalProposal.getTotalTermPremium();
		double discount = payment.getTotalDiscountAmount();
		double ncbPremium = payment.getNcbPremium();
		double stampFee = payment.getStampFees();
		double annualPremium = medicalProposal.getTotalPremium();
		double netPremium = payment.getTotalAmount();
		int basicUnit = 0;
		double basicPremium = 0.0;
		String typeOfInsurance = "";
		int additionalUnit1 = 0;
		int additionalUnit2 = 0;
		double additionalPremium1 = 0.0;
		double additionalPremium2 = 0.0;
		int personCount = 0;
		if (medicalProposal.getOrganization() == null) {
			int numberOfInsuredPerson = medicalProposal.getMedicalProposalInsuredPersonList().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredPersonName = medicalProposal.getOrganization().getName();
		}
		for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {
			personCount++;
			basicUnit += person.getUnit();
			basicPremium += person.getPremium();
			for (MedicalProposalInsuredPersonAddOn addOn : person.getInsuredPersonAddOnList()) {
				if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getOperationAndMis())) {
					additionalUnit1 += addOn.getUnit();
					additionalPremium1 += addOn.getPremium();
				} else if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getClinical())) {
					additionalUnit2 += addOn.getUnit();
					additionalPremium2 += addOn.getPremium();
				}
			}
		}
		if (medicalProposal.getHealthType() == HealthType.HEALTH) {
			typeOfInsurance = "Health Insurance";
		} else if (medicalProposal.getHealthType() == HealthType.MICROHEALTH) {
			typeOfInsurance = "Micro Health Insurance";
		} else if (medicalProposal.getHealthType() == HealthType.CRITICALILLNESS) {
			typeOfInsurance = "Critical Illness Insurance ";
		}

		Calendar cal = Calendar.getInstance();
		/*
		 * if (medicalProposal.getStartDate() == null) { cal.setTime(new
		 * Date()); startDate = Utils.formattedDate(new Date()); } else {
		 * cal.setTime(medicalProposal.getStartDate()); startDate =
		 * Utils.formattedDate(medicalProposal.getStartDate()); }
		 */

		String startDate = Utils.formattedDate(medicalProposal.getStartDate());
		cal.setTime(medicalProposal.getStartDate());
		int period = medicalProposal.getPeriodYears();
		cal.add(Calendar.YEAR, period);
		String endDate = Utils.formattedDate(cal.getTime());

		cal.setTime(medicalProposal.getStartDate());
		int duedate = medicalProposal.getPaymentType().getMonth();
		cal.add(Calendar.MONTH, duedate);
		String due = Utils.formattedDate(cal.getTime());

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("additionalUnit1", additionalUnit1 + "");
		paramMap.put("additionalUnit2", additionalUnit2 + "");
		paramMap.put("additionalPremium1", additionalPremium1);
		paramMap.put("additionalPremium2", additionalPremium2);
		paramMap.put("termPremium", termPremium);
		paramMap.put("typeOfInsurance", typeOfInsurance);
		paramMap.put("invoiceDate", Utils.formatDateTimeWithAMOrPM(payment.getConfirmDate()));
		paramMap.put("invoiceNo", invoice);
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("duenum", "1 ");
		paramMap.put("paymentType", paymentType);
		paramMap.put("fromDate", startDate);
		paramMap.put("toDate", endDate);
		paramMap.put("insurdName", insuredPersonName);
		paramMap.put("address", address);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("annualPremium", annualPremium);
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", ncbPremium);
		paramMap.put("discount", discount);
		paramMap.put("stampFee", stampFee);
		paramMap.put("Duedate", startDate);
		paramMap.put("totalPremium", netPremium);
		paramMap.put("basicUnit", basicUnit + (basicUnit == 1 ? " Unit" : " Units"));
		paramMap.put("basicSI", basicPremium);
		paramMap.put("period", period + "");
		paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());

		JasperPrint jprint = new JasperPrint();
		if (medicalProposal.getHealthType() == HealthType.HEALTH)
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_HEALTH_PAYMENT_INVOICE, new JREmptyDataSource());
		else if (medicalProposal.getHealthType() == HealthType.MICROHEALTH)
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_PAYMENT_INVOICE, new JREmptyDataSource());
		else if (medicalProposal.getHealthType() == HealthType.CRITICALILLNESS)
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.CRITICALILLNESS_HEALTH_PAYMENT_INVOICE, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** to generate Health,CriticalIllness and MicroHealth Receipt Letter */
	public static <T> List<JasperPrint> generateMedicalReceipt(MedicalProposal medicalProposal, Payment payment) {
		String insuredPersonName = "";
		String proposalNo = medicalProposal.getProposalNo();
		String receiptNo = payment.getReceiptNo();
		double adjustAmount = payment.getNcbPremium() != 0 ? -payment.getNcbPremium() : 0.0;
		MedicalProposalInsuredPerson medicalProposalInsuredPerson = medicalProposal.getMedicalProposalInsuredPersonList().get(0);
		String customerName = medicalProposal.getCustomerName();
		String customerAddress = medicalProposal.getCustomerAddress();
		String paymentType = medicalProposal.getPaymentType().getName();
		String productid = medicalProposalInsuredPerson.getProduct().getId();
		String agentInfo = medicalProposal.getAgentNLiscenseNo();
		MedicalProposalInsuredPerson insuredPerson = medicalProposal.getMedicalProposalInsuredPersonList().get(0);
		int insuredPersonCount = medicalProposal.getMedicalProposalInsuredPersonList().size() - 1;
		int numberOfInsuredPerson = medicalProposal.getMedicalProposalInsuredPersonList().size();
		Calendar cal = Calendar.getInstance();
		cal.setTime(medicalProposal.getStartDate());
		int duedate = medicalProposal.getPaymentType().getMonth();
		cal.add(Calendar.MONTH, duedate);
		String due = Utils.formattedDate(cal.getTime());

		if (medicalProposal.getOrganization() == null) {
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = medicalProposal.getMedicalProposalInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredPersonName = medicalProposal.getOrganization().getName();
		}
		boolean isHealth = false;
		if (KeyFactorChecker.isHealth(productid))
			isHealth = true;
		int additionalUnit1 = 0;
		int additionalUnit2 = 0;

		for (MedicalProposalInsuredPerson person : medicalProposal.getMedicalProposalInsuredPersonList()) {

			for (MedicalProposalInsuredPersonAddOn addOn : person.getInsuredPersonAddOnList()) {
				if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getOperationAndMis())) {
					additionalUnit1 += addOn.getUnit();
				} else if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getClinical())) {
					additionalUnit2 += addOn.getUnit();
				}
			}
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("receiptBG", ApplicationSetting.getReceipt4Fly());
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("policyPeriod", medicalProposal.getPeriodYears() + " Year");
		paramMap.put("receiptDate", (payment.getPaymentDate() == null) ? "-" : Utils.formatDateTimeWithAMOrPM(payment.getPaymentDate()));
		paramMap.put("receiptNo", receiptNo);
		paramMap.put("isHealth", isHealth);
		paramMap.put("startDate", Utils.formattedDate(medicalProposal.getStartDate()));
		paramMap.put("endDate", Utils.formattedDate(medicalProposal.getEndDate()));
		paramMap.put("customerName", insuredPersonName);
		paramMap.put("address", customerAddress);
		paramMap.put("stampFee", payment.getStampFees());
		paramMap.put("unit", medicalProposal.getTotalUnit());
		paramMap.put("basicUnit", medicalProposal.getTotalBasicUnit());
		paramMap.put("addOnOneUnit", additionalUnit1);
		paramMap.put("addOnTwoUnit", additionalUnit2);
		paramMap.put("premium", payment.getTotalPremium());
		paramMap.put("agent", agentInfo);
		paramMap.put("no", "1");
		paramMap.put("discount", payment.getSpecialDiscount());
		paramMap.put("totalPremium", payment.getTotalAmount());
		paramMap.put("adjustAmount", adjustAmount);
		paramMap.put("paymentType", paymentType);
		paramMap.put("dueDate", Utils.formattedDate(medicalProposal.getStartDate()));
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		paramMap.put("premiumInWord", convertor.getNameWithDecimal(payment.getTotalAmount()));
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		
		//"  (B-"+$P{basicUnit}+",C1-"+$P{addOnOneUnit}+",C2-"+$P{addOnTwoUnit}+")"         update ZNM(Request To Change FNI 03092021)
		String totalUnit = " (B-"+ String.valueOf(medicalProposal.getTotalBasicUnit()) +",C1-"+String.valueOf(additionalUnit1) +",C2-"+ String.valueOf(additionalUnit2)+")";
		paramMap.put("totalUnit", medicalProposal.getHealthType() == HealthType.CRITICALILLNESS ? "" : totalUnit);

		for (int i = 0; i <= 1; i++) {
			paramMap.put("isCopy", i == 1 ? true : false);
			JasperPrint jprint = new JasperPrint();
			if (medicalProposal.getHealthType() == HealthType.HEALTH || medicalProposal.getHealthType() == HealthType.CRITICALILLNESS)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.CRITICALILLNESS_HEALTH_CASH_RECEPT, new JREmptyDataSource());
			else if (medicalProposal.getHealthType() == HealthType.MICROHEALTH)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_CASH_RECEIPT, new JREmptyDataSource());
			jprintList.add(jprint);
		}
		return jprintList;
	}

	/** to generate Health,CriticalIllness and MicroHealth Policy Letter */
	public static List<JasperPrint> generateMedicalHealthPolicyIssue(MedicalPolicy medicalPolicy, Payment payment) {
		String policyNo = medicalPolicy.getPolicyNo();
		String policyStartDate = Utils.formattedDate(medicalPolicy.getActivedPolicyStartDate());
		String paymentDate = (payment.getPaymentDate() == null) ? "-" : Utils.formattedDate(payment.getPaymentDate());
		String paymentType = medicalPolicy.getPaymentType().getName();
		int termMonth;
		termMonth = medicalPolicy.getPaymentType().getMonth();
		termMonth = termMonth == 0 ? medicalPolicy.getPeriodMonth() : termMonth;
		String agentName = medicalPolicy.getAgentName();
		String agentRegNo = medicalPolicy.getAgentLiscenseNo();
		String insuredPersonName = "";

		MedicalPolicyInsuredPerson medicalPolicyInsuredPerson = medicalPolicy.getPolicyInsuredPersonList().get(0);
		String insuName = medicalPolicy.getCustomerName();
		String insuAddress = medicalPolicyInsuredPerson.getFullAddress();
		String insufatherName = medicalPolicyInsuredPerson.getFatherName();
		int insuAge = medicalPolicyInsuredPerson.getAge();
		String insuIdNo = medicalPolicyInsuredPerson.getFullIdNo();
		String insurOccup = medicalPolicyInsuredPerson.getOccupationName();
		String insuPhNo = medicalPolicyInsuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(medicalPolicyInsuredPerson.getDateOfBirth());

		int insuBirthDay = calendar.get(Calendar.DAY_OF_MONTH);
		int insuBirthMonth = calendar.get(Calendar.MONTH) + 1;
		int insuBirthYear = calendar.get(Calendar.YEAR);

		MedicalPolicyInsuredPersonBeneficiaries medicalPolicyInsuredPersonBeneficiarie = medicalPolicyInsuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
		int benefSize = medicalPolicyInsuredPerson.getPolicyInsuredPersonBeneficiariesList().size();
		String beneName = medicalPolicyInsuredPersonBeneficiarie.getFullName() + (benefSize > 1 ? " + " + (benefSize - 1) : "");
		String beneIdNo = medicalPolicyInsuredPersonBeneficiarie.getFullIdNoForView();
		
		
		int beneAge = ageForNextYearBF(medicalPolicy.getActivedPolicyStartDate(),medicalPolicyInsuredPersonBeneficiarie.getDateOfBirth());
		String relationship = medicalPolicyInsuredPersonBeneficiarie.getRelationship().getName();
		String beneAddress = medicalPolicyInsuredPersonBeneficiarie.getResidentAddress().getFullResidentAddress();
		String benePhNo = medicalPolicyInsuredPersonBeneficiarie.getContentInfo().getPhoneOrMoblieNo();
		String beneFatherName = medicalPolicyInsuredPersonBeneficiarie.getFatherName();
		double termPremium = 0.0;
		double oneYearPremium = 0.0;
		int basicUnit = 0;
		double basicPremium = 0.0;
		int additionalUnit1 = 0;
		int additionalUnit2 = 0;
		double totalPremium = 0.0;
		double additionalPremium1 = 0.0;
		double additionalPremium2 = 0.0;
		String customerName = medicalPolicy.getCustomerName();
		if (medicalPolicy.getOrganization() == null) {
			int numberOfInsuredPerson = medicalPolicy.getPolicyInsuredPersonList().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = medicalPolicy.getPolicyInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredPersonName = medicalPolicy.getOrganization().getName();
		}
		// insuredPersonName = medicalPolicyInsuredPerson.getFullName();
		for (MedicalPolicyInsuredPerson person : medicalPolicy.getPolicyInsuredPersonList()) {
			basicUnit += person.getUnit();
			basicPremium += person.getPremium();
			termPremium += person.getTotalTermPremium();
			oneYearPremium += person.getTotalPremium();
			for (MedicalPolicyInsuredPersonAddOn addOn : person.getPolicyInsuredPersonAddOnList()) {
				if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getOperationAndMis())) {
					additionalUnit1 += addOn.getUnit();
					additionalPremium1 += addOn.getPremium();
				} else if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getClinical())) {
					additionalUnit2 += addOn.getUnit();
					additionalPremium2 += addOn.getPremium();
				}
			}
		}
		totalPremium = additionalPremium1 + additionalPremium2 + basicPremium;
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String termPremiumInMMText = convertor.getName(termPremium);
		// String termPremiumInMM =
		// NumberToNumberConvertor.getMyanmarNumber(termPremium, true);
		String oneYearPremiumInMMText = convertor.getName(oneYearPremium);
		// String oneYeatPremiumInMM =
		// NumberToNumberConvertor.getMyanmarNumber(oneYearPremium, true);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("policyNo", policyNo);
		paramMap.put("policyStartDate", policyStartDate);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("paymentType", paymentType);
		paramMap.put("agentName", agentName);
		paramMap.put("insuName", insuredPersonName);
		paramMap.put("insuAddress", insuAddress);
		paramMap.put("insufatherName", insufatherName);
		paramMap.put("insuAge", insuAge + "");
		paramMap.put("insuIdNo", insuIdNo);
		paramMap.put("insurOccup", insurOccup);
		paramMap.put("insuPhNo", insuPhNo);
		paramMap.put("insuBirthDay", insuBirthDay);
		paramMap.put("insuBirthMonth", insuBirthMonth);
		paramMap.put("insuBirthYear", insuBirthYear);
		paramMap.put("beneName", beneName);
		paramMap.put("beneIdNo", beneIdNo);
		paramMap.put("beneAge", beneAge);
		paramMap.put("relationship", relationship);
		paramMap.put("beneAddress", beneAddress);
		paramMap.put("benePhNo", benePhNo);
		paramMap.put("fromDate", Utils.formattedDate(medicalPolicy.getActivedPolicyStartDate()));
		paramMap.put("toDate", Utils.formattedDate(medicalPolicy.getActivedPolicyEndDate()));
		paramMap.put("date", paymentDate);
		paramMap.put("basicUnit", basicUnit);
		paramMap.put("basicPremium", Utils.getCurrencyFormatString(basicPremium));
		paramMap.put("termPremiumInMM", Utils.getCurrencyFormatString(termPremium));
		paramMap.put("termPremiumText", termPremiumInMMText);
		paramMap.put("oneYearPremiumText", oneYearPremiumInMMText);
		paramMap.put("oneYearPremium", Utils.getCurrencyFormatString(oneYearPremium));
		paramMap.put("period", medicalPolicy.getPeriodYears() + " Year");
		paramMap.put("beneFatherName", beneFatherName);
		paramMap.put("additionalUnit1", additionalUnit1);
		paramMap.put("additionalUnit2", additionalUnit2);
		paramMap.put("additionalPremium1", Utils.getCurrencyFormatString(additionalPremium1));
		paramMap.put("additionalPremium2", Utils.getCurrencyFormatString(additionalPremium2));
		paramMap.put("totalPremium", Utils.getCurrencyFormatString(totalPremium));
		paramMap.put("termMonth", termMonth);
		paramMap.put("termPremium", Utils.getCurrencyFormatString(termPremium));
		paramMap.put("paymentDate", paymentDate);
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		JasperPrint jprint = new JasperPrint();
		JasperPrint termAndCPrint = new JasperPrint();
		JasperPrint termAndCPrint1 = new JasperPrint();
		JasperPrint jprintInsuredPerson = new JasperPrint();
		String productId = medicalPolicyInsuredPerson.getProduct().getId();
		Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
		insuredPesronMap_2.put("startDate", Utils.formattedDate(medicalPolicy.getActivedPolicyStartDate()));
		insuredPesronMap_2.put("endDate", Utils.formattedDate(medicalPolicy.getActivedPolicyEndDate()));
		insuredPesronMap_2.put("productName", MyanmarLanguae.getProductNameMyanmarLanguage(medicalPolicyInsuredPerson.getProduct()));
		List<MedicalPolicyInsuredPersonDTO> perDTOList = new ArrayList<MedicalPolicyInsuredPersonDTO>();
		for (MedicalPolicyInsuredPerson person : medicalPolicy.getPolicyInsuredPersonList()) {
			perDTOList.add(MedicalPolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
		}
		insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
		if (KeyFactorChecker.isIndividualHealth(productId) || KeyFactorChecker.isGroupHealth(productId)) {
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_HEALTH_POLICY_ISSUE, new JREmptyDataSource());
			jprintList.add(jprint);
			Utils.removeBlankPages(jprint);
			if (KeyFactorChecker.isGroupHealth(productId)) {
				jprintInsuredPerson = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.GPMEDICALPERSONATT, new JREmptyDataSource());
				Utils.removeBlankPages(jprintInsuredPerson);
				jprintList.add(jprintInsuredPerson);
			}
			termAndCPrint = JasperFactory.generateJasperPrint(null, JasperTemplate.HL_TERM_CONDITION, new JREmptyDataSource());
			termAndCPrint1 = JasperFactory.generateJasperPrint(null, JasperTemplate.HL_TERM_CONDITION1, new JREmptyDataSource());
		} else if (KeyFactorChecker.isMicroHealth(productId)) {
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_POLICY_ISSUE, new JREmptyDataSource());
			termAndCPrint = JasperFactory.generateJasperPrint(null, JasperTemplate.MH_TERM_CONDITION, new JREmptyDataSource());
			termAndCPrint1 = JasperFactory.generateJasperPrint(null, JasperTemplate.MH_TERM_CONDITION1, new JREmptyDataSource());
			jprintList.add(jprint);
			Utils.removeBlankPages(jprint);
		} else if (KeyFactorChecker.isCriticalIllness(productId) || KeyFactorChecker.isGroupCriticalIllness(productId)) {
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.CRITICAL_ILLNESS_POLICY_ISSUE, new JREmptyDataSource());
			jprintList.add(jprint);
			Utils.removeBlankPages(jprint);
			if (KeyFactorChecker.isGroupCriticalIllness(productId)) {
				jprintInsuredPerson = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.MED_HEALTH_INUREDPERSON_ATTACH, new JREmptyDataSource());
				Utils.removeBlankPages(jprintInsuredPerson);
				jprintList.add(jprintInsuredPerson);
			}
			termAndCPrint = JasperFactory.generateJasperPrint(null, JasperTemplate.CI_TERM_CONDITION, new JREmptyDataSource());
			termAndCPrint1 = JasperFactory.generateJasperPrint(null, JasperTemplate.CI_TERM_CONDITION1, new JREmptyDataSource());
		}

		Utils.removeBlankPages(termAndCPrint);
		Utils.removeBlankPages(termAndCPrint1);
		jprintList.add(termAndCPrint);
		jprintList.add(termAndCPrint1);
		return jprintList;
	}
	
	public static int ageForNextYearBF(Date startDate, Date dob) {
		Calendar cal_1 = Calendar.getInstance();
		cal_1.setTime(startDate);
		int currentYear = cal_1.get(Calendar.YEAR);
		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(dob);
		cal_2.set(Calendar.YEAR, currentYear);
		if (startDate.after(cal_2.getTime())) {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(dob);
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR) + 1;
			return year_2 - year_1;
		} else {
			Calendar cal_3 = Calendar.getInstance();
			cal_3.setTime(dob);
			int year_1 = cal_3.get(Calendar.YEAR);
			int year_2 = cal_1.get(Calendar.YEAR);
			return year_2 - year_1;
		}
	}

	/** to generate Health and CriticalIllness Bill Collection Noti Letter */
	public static List<JasperPrint> generateMedicalPolicyNotification(List<MedicalPolicy> policies, String dirPath, String fileName, ReferenceType referenceType) {
		List<JasperPrint> jasperList = new ArrayList<JasperPrint>();
		try {
			for (MedicalPolicy medicalPolicy : policies) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				int policyPeriodYears = 0;
				int basicUnit = 0;
				policyPeriodYears = medicalPolicy.getPeriodYears();
				Date activePolicyStartDate = medicalPolicy.getActivedPolicyStartDate();
				Date activePolicyEndDate = medicalPolicy.getActivedPolicyEndDate();
				int month = medicalPolicy.getPaymentType().getMonth();
				int remainingTerm = 0;
				int currentTerm = 0;
				int additional1 = 0, additional2 = 0;
				if (medicalPolicy.getPaymentTerm() != medicalPolicy.getLastPaymentTerm()) {
					remainingTerm = medicalPolicy.getPaymentTerm() - medicalPolicy.getLastPaymentTerm();
					currentTerm = medicalPolicy.getLastPaymentTerm() + 1;
				}
				for (MedicalPolicyInsuredPerson person : medicalPolicy.getPolicyInsuredPersonList()) {
					basicUnit += person.getUnit();
					// basicPremium += person.getPremium();
					// termPremium += person.getTotalTermPremium();
					// oneYearPremium += person.getTotalPremium();
					for (MedicalPolicyInsuredPersonAddOn addOn : person.getPolicyInsuredPersonAddOnList()) {
						if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getOperationAndMis())) {
							additional1 += addOn.getUnit();
							// additionalPremium1 += addOn.getPremium();
						} else if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getClinical())) {
							additional2 += addOn.getUnit();
							// additionalPremium2 += addOn.getPremium();
						}
					}
				}

				paramMap.put("insuredPersonName", medicalPolicy.getInsuredPersonName());
				paramMap.put("insuredPersonAddress", medicalPolicy.getCustomerAddress());
				paramMap.put("salePersonName", medicalPolicy.getAgentName());
				paramMap.put("phoneNo", medicalPolicy.getCustomerPhoneNo());
				paramMap.put("policyNo", medicalPolicy.getPolicyNo());
				paramMap.put("basicTermPremium", Utils.formattedCurrency(medicalPolicy.getTotalTermPremium()));
				paramMap.put("activePolicyStartDate", Utils.formattedDate(activePolicyStartDate));
				paramMap.put("activePolicyEndDate", Utils.formattedDate(activePolicyEndDate));
				paramMap.put("policyLifeTime", policyPeriodYears);
				paramMap.put("month", month);
				paramMap.put("currentTerm", currentTerm);
				paramMap.put("remainingTerm", remainingTerm);
				paramMap.put("basicUnit", basicUnit);
				paramMap.put("additional1", additional1);
				paramMap.put("additional2", additional2);
				paramMap.put("dueDay", DateUtils.getDayFromDate(medicalPolicy.getCoverageDate()));
				paramMap.put("dueMonth", (DateUtils.getMonthFromDate(medicalPolicy.getCoverageDate())) + 1);
				paramMap.put("dueYear", DateUtils.getYearFromDate(medicalPolicy.getCoverageDate()));
				paramMap.put("dayNoti", DateUtils.getDayWithMyanmarLanguage(new Date()));
				paramMap.put("monthNoti", DateUtils.getMonthWithMyanmarLanguage(new Date()));
				paramMap.put("yearNoti", DateUtils.getYearWithMyanmarLanguage(new Date()));
				paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
				if (ReferenceType.CRITICAL_ILLNESS.equals(referenceType)) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.CRITICALILLNESS_POLICY_NOTIFICATION_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jasperList.add(print);
				} else {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.HEALTH_POLICY_NOTIFICATION_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jasperList.add(print);
				}
			}
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate HealthPolicyNotification", e);
		}
		return jasperList;
	}

	/** to generate Health and CriticalIllness Bill Collection Invoice Letter */
	public static List<JasperPrint> generateBillCollectionPaymentInvoice(List<BillCollectionCashReceiptDTO> cashReceiptDTOs) {
		List<JasperPrint> jasperList = new ArrayList<JasperPrint>();
		for (BillCollectionCashReceiptDTO cashReceiptDTO : cashReceiptDTOs) {
			Payment payment = cashReceiptDTO.getPayment();
			MedicalPolicy policy = cashReceiptDTO.getMedicalPolicy();
			String invoiceDate = Utils.formatDateTimeWithAMOrPM(payment.getConfirmDate());
			String invoice = payment.getInvoiceNo();
			String proposalNo = policy.getPolicyNo();
			String paymentType = policy.getPaymentType().getName();
			String customerName = policy.getCustomerName();
			String address = policy.getCustomerAddress();
			String agentRegNo = policy.getAgentLiscenseNo();
			String agentName = policy.getAgentName();
			double annualPremium = policy.getTotalPremium();
			double termPremium = policy.getTotalTermPremium();
			double discount = payment.getTotalDiscount();
			double stampFee = payment.getStampFees();
			double totalPremium = payment.getTotalAmount();
			String typeOfInsurance = "";
			int basicUnit = 0;
			double basicPremium = 0.0;
			int additionalUnit1 = 0;
			int additionalUnit2 = 0;
			double additionalPremium1 = 0.0;
			double additionalPremium2 = 0.0;
			Agent agent;
			for (MedicalPolicyInsuredPerson person : policy.getPolicyInsuredPersonList()) {
				basicUnit += person.getUnit();
				basicPremium += person.getPremium();
				for (MedicalPolicyInsuredPersonAddOn addOn : person.getPolicyInsuredPersonAddOnList()) {
					if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getOperationAndMis())) {
						additionalUnit1 += addOn.getUnit();
						additionalPremium1 += addOn.getPremium();
					}
					if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getClinical())) {
						additionalUnit2 += addOn.getUnit();
						additionalPremium2 += addOn.getPremium();
					}
				}
			}
			if (policy.getHealthType() == HealthType.HEALTH) {
				typeOfInsurance = "Health Insurance";
			} else if (policy.getHealthType() == HealthType.MICROHEALTH) {
				typeOfInsurance = "Micro Health Insurance";
			} else if (policy.getHealthType() == HealthType.CRITICALILLNESS) {
				typeOfInsurance = "Critical Illness Insurance ";
			}
			String insuredPersonName = "";
			if (policy.getOrganization() == null) {
				int numberOfInsuredPerson = policy.getPolicyInsuredPersonList().size();
				if (numberOfInsuredPerson > 1) {
					insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
				} else {
					insuredPersonName = policy.getPolicyInsuredPersonList().get(0).getFullName();
				}
			} else {
				insuredPersonName = policy.getOrganization().getName();
			}
			agent = policy.getAgent();
			if (agent == null) {
				if (SaleChannelType.WALKIN.getLabel() == policy.getSaleChannelType().getLabel()) {
					agentName = "WALKIN";
				} else {
					agentName = "DIRECTMARKETING";
				}
			} else {
				agentName = agent.getFullName();
			}
			int term = payment.getFromTerm();
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(policy.getActivedPolicyStartDate());
			calendar1.add(Calendar.MONTH, payment.getPaymentType().getMonth() * (term - 1));
			Date dueDate = calendar1.getTime();
			int paymentTimes = payment.getToTerm() - payment.getFromTerm() + 1;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dueDate);
			calendar.add(Calendar.MONTH, payment.getPaymentType().getMonth() * paymentTimes);
			Date toDate = calendar.getTime();

			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("additionalUnit1", additionalUnit1 + "");
			paramMap.put("additionalUnit2", additionalUnit2 + "");
			paramMap.put("additionalPremium1", additionalPremium1);
			paramMap.put("additionalPremium2", additionalPremium2);
			paramMap.put("termPremium", termPremium);
			paramMap.put("typeOfInsurance", typeOfInsurance);
			paramMap.put("invoiceDate", invoiceDate);
			paramMap.put("invoiceNo", invoice);
			paramMap.put("proposalNo", proposalNo);
			paramMap.put("duenum", payment.getPaymentTermStrings());
			paramMap.put("paymentType", paymentType);
			paramMap.put("fromDate", Utils.formattedDate(policy.getActivedPolicyStartDate()));
			paramMap.put("toDate", Utils.formattedDate(policy.getActivedPolicyEndDate()));
			paramMap.put("insurdName", insuredPersonName);
			paramMap.put("address", address);
			paramMap.put("agentRegNo", agentRegNo);
			paramMap.put("agentName", agentName);
			paramMap.put("annualPremium", annualPremium);
			paramMap.put("adjPlus", 0.00);
			paramMap.put("adjMinus", 0.00);
			paramMap.put("discount", discount);
			paramMap.put("stampFee", stampFee);
			paramMap.put("totalPremium", totalPremium);
			paramMap.put("basicUnit", basicUnit + (basicUnit == 1 ? " Unit" : " Units"));
			paramMap.put("basicSI", basicPremium);
			paramMap.put("Duedate", Utils.formattedDate(dueDate));
			paramMap.put("period", policy.getPeriodYears() + "");
			paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			JasperPrint jprint = new JasperPrint();
			if (policy.getHealthType() == HealthType.HEALTH)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MED_HEALTH_PAYMENT_INVOICE, new JREmptyDataSource());
			else if (policy.getHealthType() == HealthType.MICROHEALTH)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_PAYMENT_INVOICE, new JREmptyDataSource());
			else if (policy.getHealthType() == HealthType.CRITICALILLNESS)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.CRITICALILLNESS_HEALTH_PAYMENT_INVOICE, new JREmptyDataSource());
			jasperList.add(jprint);
		}
		return jasperList;
	}

	/** to generate Health and CriticalIllness Bill Collection Receipt Letter */
	public static <T> List<JasperPrint> generateMedicalBillcollectionCashReceipt(MedicalPolicy medicalPolicy, Payment payment) {
		String proposalNo = medicalPolicy.getPolicyNo();
		String receiptNo = payment.getReceiptNo();
		MedicalPolicyInsuredPerson medicalProposalInsuredPerson = medicalPolicy.getPolicyInsuredPersonList().get(0);
		String customerName = medicalPolicy.getCustomerName();
		String customerAddress = medicalPolicy.getCustomerAddress();
		String paymentType = medicalPolicy.getPaymentType().getName();
		String productid = medicalProposalInsuredPerson.getProduct().getId();
		String agentInfo = medicalPolicy.getAgentNameNLiscenseNo();
		int term = payment.getFromTerm();
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(medicalPolicy.getActivedPolicyStartDate());
		calendar1.add(Calendar.MONTH, payment.getPaymentType().getMonth() * (term - 1));
		Date dueDate = calendar1.getTime();
		int paymentTimes = payment.getToTerm() - payment.getFromTerm() + 1;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dueDate);
		calendar.add(Calendar.MONTH, payment.getPaymentType().getMonth() * paymentTimes);
		Date toDate = calendar.getTime();

		boolean isHealth = false;
		if (KeyFactorChecker.isIndividualHealth(productid) || KeyFactorChecker.isGroupHealth(productid))
			isHealth = true;

		int basicUnit = 0;
		double basicPremium = 0.0;
		int additionalUnit1 = 0;
		int additionalUnit2 = 0;
		for (MedicalPolicyInsuredPerson person : medicalPolicy.getPolicyInsuredPersonList()) {
			basicUnit += person.getUnit();

			for (MedicalPolicyInsuredPersonAddOn addOn : person.getPolicyInsuredPersonAddOnList()) {
				if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getOperationAndMis())) {
					additionalUnit1 += addOn.getUnit();

				} else if (addOn.getAddOn().getProductContent().getId().equals(KeyFactorIDConfig.getClinical())) {
					additionalUnit2 += addOn.getUnit();

				}
			}
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("receiptDate", Utils.formatDateTimeWithAMOrPM(payment.getPaymentDate()));
		paramMap.put("receiptNo", receiptNo);
		paramMap.put("isHealth", isHealth);
		paramMap.put("startDate", Utils.formattedDate(medicalPolicy.getActivedPolicyStartDate()));
		paramMap.put("endDate", Utils.formattedDate(medicalPolicy.getActivedPolicyEndDate()));
		int numberOfInsuredPerson = medicalPolicy.getPolicyInsuredPersonList().size();
		if (numberOfInsuredPerson > 1) {
			customerName = customerName.concat(" +").concat(String.valueOf(numberOfInsuredPerson - 1));
		}
		paramMap.put("customerName", customerName);
		paramMap.put("address", customerAddress);
		paramMap.put("stampFee", payment.getStampFees());
		paramMap.put("unit", medicalPolicy.getTotalUnit());
		paramMap.put("basicUnit", medicalPolicy.getTotalBasicUnit());
		paramMap.put("addOnOneUnit", additionalUnit1);
		paramMap.put("addOnTwoUnit", additionalUnit2);
		paramMap.put("premium", payment.getTotalPremium());
		paramMap.put("agent", agentInfo);
		paramMap.put("no", payment.getPaymentTermStrings());
		paramMap.put("discount", payment.getSpecialDiscount());
		paramMap.put("totalPremium", payment.getTotalAmount());
		paramMap.put("adjustAmount", 0.00);
		paramMap.put("paymentType", paymentType);
		paramMap.put("policyPeriod", medicalPolicy.getPeriodYears() + " Year");
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		paramMap.put("dueDate", Utils.formattedDate(dueDate));
		paramMap.put("premiumInWord", convertor.getNameWithDecimal(payment.getTotalAmount()));
		paramMap.put("receiptBG", ApplicationSetting.getReceipt4Fly());
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		for (int i = 0; i <= 1; i++) {
			paramMap.put("isCopy", i == 1 ? true : false);
			JasperPrint jprint = new JasperPrint();
			if (KeyFactorChecker.isIndividualHealth(productid) || KeyFactorChecker.isCriticalIllness(productid) || KeyFactorChecker.isGroupCriticalIllness(productid)
					|| KeyFactorChecker.isGroupHealth(productid))
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.CRITICALILLNESS_HEALTH_CASH_RECEPT, new JREmptyDataSource());
			else if (KeyFactorChecker.isMicroHealth(productid))
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.MICRO_HEALTH_CASH_RECEIPT, new JREmptyDataSource());
			jprintList.add(jprint);
		}
		return jprintList;
	}

}
