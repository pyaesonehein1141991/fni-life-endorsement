package org.ace.insurance.web.common.document.life;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedProperty;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.common.EndorsementStatus;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Gender;
import org.ace.insurance.common.KeyFactorIDConfig;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.DisabilityLifeClaimPartLink;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeDeathClaim;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.life.endorsement.LifeEndorseBeneficiary;
import org.ace.insurance.life.endorsement.LifeEndorseChange;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.LifeEndorseInsuredPerson;
import org.ace.insurance.life.endorsement.service.interfaces.ILifeEndorsementService;
import org.ace.insurance.life.factory.PolicyInsuredPersonFactory;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.PolicyInsuredPerson;
import org.ace.insurance.life.policy.PolicyInsuredPersonBeneficiaries;
import org.ace.insurance.life.policy.PolicyInsuredPersonDTO;
import org.ace.insurance.life.policy.SportManTravelAbroad;
import org.ace.insurance.life.policyExtraAmount.PolicyExtraAmount;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.MedicalPolicyInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposal;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPerson;
import org.ace.insurance.medical.proposal.MedicalProposalInsuredPersonAddOn;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.proxy.MEDFEES001;
import org.ace.insurance.report.claim.LifeClaimMedicalFeeDTO;
import org.ace.insurance.report.life.PaymentTypeChangeReportDTO;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.hospital.Hospital;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.common.document.JasperFactory;
import org.ace.insurance.web.common.document.JasperTemplate;
import org.ace.insurance.web.common.myanmarLanguae.MyanmarLanguae;
import org.ace.insurance.web.common.ntw.mym.AbstractMynNumConvertor;
import org.ace.insurance.web.common.ntw.mym.DefaultConvertor;
import org.ace.insurance.web.manage.life.billcollection.BillCollectionCashReceiptDTO;
import org.ace.insurance.web.manage.life.proposal.BeneficiariesInfoDTO;
import org.ace.insurance.web.manage.medical.claim.MedicalPolicyInsuredPersonDTO;
import org.ace.insurance.web.manage.medical.claim.factory.MedicalPolicyInsuredPersonFactory;
import org.ace.java.component.SystemException;
import org.ace.java.web.ApplicationSetting;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class LifeDocumentBuilder extends JasperFactory {
	
	@ManagedProperty(value = "#{MedicalProposalService}")
	private static IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
	}
	
	/** Public,ShortTerm,GroupLife Acceptance Letter */
	public static List<JasperPrint> generateLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		int numberOfEmployee = 0;
		Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		boolean isPublicLife;
		boolean isShortermEndownment;
		boolean isStudentLife;
		boolean isPublicTermLife;
		isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
		isPublicLife = KeyFactorChecker.isPublicLife(product);
		isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
		isPublicTermLife = KeyFactorChecker.isPublicTermLife(product.getId());
		for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			if (proposalInsuredPerson.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				numberOfEmployee += 1;
			}
		}
		String stampFee = Utils.formattedCurrency(acceptedInfo.getStampFeesAmount());
		Calendar cal = Calendar.getInstance();
		String period = "";
		int periodMonths = lifeProposal.getPeriodMonth();
		cal.add(Calendar.YEAR, lifeProposal.getPeriodMonth());
		period = periodMonths / 12 + "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("reportLogo", ApplicationSetting.getFNIPublicLogo());
		paramMap.put("customerName", lifeProposal.getCustomerName());
		paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
		paramMap.put("phoneNo", lifeProposal.getPhoneNo());
		paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
		paramMap.put("proposalNo", lifeProposal.getProposalNo());
		paramMap.put("periodYears", period);
		paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getApprovedSumInsured()));
		paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
		paramMap.put("netPremium", Utils.formattedCurrency(acceptedInfo.getNetPremium()));
		paramMap.put("netTermPremium", Utils.formattedCurrency(acceptedInfo.getNetTermPremium() - acceptedInfo.getStampFeesAmount()));
		paramMap.put("netTermAmount", Utils.formattedCurrency(acceptedInfo.getNetTermPremium()));
		paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
		paramMap.put("totalEmployee", numberOfEmployee);
		paramMap.put("stempFee", stampFee);

		if (isStudentLife) {
			paramMap.put("childName", lifeProposal.getProposalInsuredPersonList().get(0).getFullName());
			paramMap.put("periodYear", lifeProposal.getPeriodOfYears());
			paramMap.put("premiumTerm", lifeProposal.getPremiumTerm());
			paramMap.put("sumInsured", Utils.formattedCurrency(lifeProposal.getSumInsured()));
		}

		if (lifeProposal.getAgent() != null) {
			paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
		} else {
			paramMap.put("agent", "( - )");
		}
		JasperPrint jprint = new JasperPrint();
		String productName = "";
		String productDescription = "";
		if (isPublicLife) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_LIFE_LABEL");
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else if (isShortermEndownment) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("SHORT_TERM_LIFE_LABEL");
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else if (isStudentLife) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("STUDENT_LIFE_LABEL");
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else if (isPublicTermLife) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_TERM_LIFE_LABEL");
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else {
			productName = MyanmarLanguae.getMyanmarLanguaeString("GROUP_LIFE_LABEL");
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("GROUP_ACCEPTED_LABEL");
		}
		paramMap.put("productName", productName);
		paramMap.put("productDescription", productDescription);
		jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ACCEPTANCE_LETTER, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** SportMan Acceptance Letter */
	public static List<JasperPrint> generateSportManAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("reportLogo", ApplicationSetting.getFNIPublicLogo());
		paramMap.put("customerName", lifeProposal.getCustomerName());
		paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
		paramMap.put("phoneNo", lifeProposal.getPhoneNo());
		paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
		paramMap.put("proposalNo", lifeProposal.getProposalNo());
		paramMap.put("periodYears", lifeProposal.getPeriodOfYears());
		paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getSuminsuredPerUnit()));
		paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
		paramMap.put("netPremium", Utils.formattedCurrency(acceptedInfo.getNetPremium()));
		paramMap.put("netTermPremium", Utils.formattedCurrency(acceptedInfo.getNetTermPremium()));
		paramMap.put("netTermAmount", Utils.formattedCurrency(acceptedInfo.getNetTermAmount()));
		paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
		if (lifeProposal.getAgent() != null) {
			paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
		} else {
			paramMap.put("agent", "( - )");
		}
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SPORTMAN_ACCEPTANCE_LETTER, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** Farmer Acceptance Letter */
	public static List<JasperPrint> generateFarmerAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("reportLogo", ApplicationSetting.getFNIPublicLogo());
		paramMap.put("customerName", lifeProposal.getCustomerName());
		paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
		paramMap.put("phoneNo", lifeProposal.getPhoneNo());
		paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
		paramMap.put("proposalNo", lifeProposal.getProposalNo());
		paramMap.put("periodYears", lifeProposal.getPeriodOfYears());
		paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured()));
		paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
		if (lifeProposal.getAgent() != null) {
			paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
		} else {
			paramMap.put("agent", "( - )");
		}
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.FARMER_ACCEPTANCE_LETTER, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** SnakeBike Acceptance Letter */
	public static List<JasperPrint> generateSnakeBikeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("reportLogo", ApplicationSetting.getFNIPublicLogo());
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getPeriodOfYears());
			paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getProposalInsuredPersonList().get(0).getProposedSumInsured()));
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SNAKEBIKE_ACCEPTANCE_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	/** PA Acceptance Letter */
	public static List<JasperPrint> generatePersonalAccidentAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		int numberOfEmployee = 0;
		for (ProposalInsuredPerson proposalInsuredPerson : lifeProposal.getProposalInsuredPersonList()) {
			if (proposalInsuredPerson.getEndorsementStatus() != EndorsementStatus.TERMINATE) {
				numberOfEmployee += 1;
			}
		}
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYears", lifeProposal.getPeriodMonth());
			paramMap.put("approvedSumInsured", Utils.formattedCurrency(lifeProposal.getApprovedSumInsured()));
			paramMap.put("totalPremium", Utils.formattedCurrency(acceptedInfo.getTotalPremium()));
			paramMap.put("netPremium", Utils.formattedCurrency(acceptedInfo.getNetPremium()));
			paramMap.put("netTermPremium", Utils.formattedCurrency(acceptedInfo.getNetTermPremium()));
			paramMap.put("netTermAmount", Utils.formattedCurrency(acceptedInfo.getNetTermAmount()));
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			paramMap.put("totalEmployee", numberOfEmployee);
			Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
			paramMap.put("isUSD", KeyFactorChecker.isPersonalAccidentUSD(product));
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( NA )");
			}
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_ACCEPTANCE_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	/** PA Reject Letter */
	public static List<JasperPrint> generatePersonalAccidentRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("imformedDate", acceptedInfo.getInformDate());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_REJECT_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	/** Farmer Reject Letter */
	public static List<JasperPrint> generateFarmerRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}

			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.FARMER_REJECT_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	/** Life Reject Letter (not PA and Farmer) */
	public static List<JasperPrint> generateLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			if (lifeProposal.getCustomer() != null) {
				paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_REJECT_LETTER, new JREmptyDataSource());
			return Arrays.asList(jprint);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeRejectLetter", e);
		}
	}

	/** GroupLife,PA,SportMan,Farmer,SnakeBike Invoice Letter */
	public static List<JasperPrint> generateLifeNewInvoice(LifeProposal lifeProposal, PaymentDTO payment) {
		int noOfInsured = 0;
		int noOfUnit = 0;
		double premiumRate = 0.0;
		boolean isEndorse = ProposalType.ENDORSEMENT.equals(lifeProposal.getProposalType());
		String period = "";
		String typeOfInsurance = "";
		String insuredPersonName = "";
		String startDate;
		double totolSumInsured = 0.0;
		int periodMonths = lifeProposal.getPeriodOfInsurance();
		String proposalNo = lifeProposal.getProposalNo();
		String customerName = lifeProposal.getCustomerName();
		String customerAddress = lifeProposal.getCustomerAddress();
		ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		String insuredPersonAddress = insuredPerson.getFullAddress();
		Product product = insuredPerson.getProduct();
		if (lifeProposal.getOrganization() == null) {
			int numberOfInsuredPerson = lifeProposal.getProposalInsuredPersonList().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = lifeProposal.getProposalInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredPersonName = lifeProposal.getOrganization().getName();
		}
		String agentRegNo = lifeProposal.getAgentLiscenseNo();
		String agentName = lifeProposal.getAgentName();
		String invoice = payment.getInvoiceNo();
		String invoiceDate = Utils.formatDateTimeWithAMOrPM(payment.getConfirmDate());
		Calendar cal = Calendar.getInstance();
		if (lifeProposal.getStartDate() == null) {
			cal.setTime(new Date());
			startDate = Utils.formattedDate(new Date());
		} else {
			cal.setTime(lifeProposal.getStartDate());
			startDate = Utils.formattedDate(lifeProposal.getStartDate());
		}

		if (KeyFactorChecker.isGroupLife(product)) {
			typeOfInsurance = "Group Life Insurance";
			noOfInsured = lifeProposal.getProposalInsuredPersonList().size();
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
			period = periodMonths + " Year";
			// insuredPersonName = customerName;
			insuredPersonAddress = customerAddress;
		} else if (KeyFactorChecker.isPersonalAccident(product)) {
			typeOfInsurance = "Personal Accident Insurance";
			period = periodMonths + " Months";
			// insuredPersonName = customerName;
			cal.add(Calendar.MONTH, lifeProposal.getPeriodMonth());
			premiumRate = insuredPerson.getPremiumRate();
		} else if (KeyFactorChecker.isFarmer(product)) {
			period = periodMonths + " Year";
			// insuredPersonName = customerName +" +" + (numberOfInsuredPerson -
			// 1);
			typeOfInsurance = "Farmers Life Insurance";
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
		} else if (KeyFactorChecker.isSnakeBite(product.getId())) {
			period = periodMonths + " Year";
			typeOfInsurance = "Snake Bite Life Insurance";
			// insuredPersonName = customerName;
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
			noOfUnit = lifeProposal.getUnits();
		} else if (KeyFactorChecker.isSportMan(product)) {
			period = periodMonths + " Year";
			typeOfInsurance = "Sportsmen Life Insurance";
			// insuredPersonName = customerName;
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
			noOfUnit = lifeProposal.getUnits();
		}

		String endDate = Utils.formattedDate(cal.getTime());
		if (KeyFactorChecker.isSnakeBite(product.getId()) || KeyFactorChecker.isSportMan(product))
			totolSumInsured = lifeProposal.getSuminsuredPerUnit();
		else
			totolSumInsured = lifeProposal.getTotalSumInsured();

		double totalPremium = payment.getTotalAmount();
		double discount = payment.getTotalDiscountAmount();
		double stampFee = payment.getStampFees();
		double netPremium = payment.getTotalPremium();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("noOfUnit", noOfUnit);
		paramMap.put("noOfInsured", noOfInsured);
		paramMap.put("rate", premiumRate);
		paramMap.put("typeOfInsurance", typeOfInsurance);
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("invoiceNo", invoice);
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("period", period);
		paramMap.put("fromDate", startDate);
		paramMap.put("toDate", endDate);
		paramMap.put("insurdName", insuredPersonName);
		paramMap.put("address", insuredPersonAddress);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("netPremium", Utils.formattedCurrency(netPremium));
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("discount", discount);
		paramMap.put("stampFee", Utils.formattedCurrency(stampFee));
		paramMap.put("totalPremium",Utils.formattedCurrency(totalPremium));
		paramMap.put("totalSumInsured", Utils.formattedCurrency(totolSumInsured));
		paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprint;

		if (KeyFactorChecker.isFarmer(product))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_PAYMENT_INVOICE_FOR_Farmer, new JREmptyDataSource());
		else if (KeyFactorChecker.isPersonalAccidentUSD(product))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_PAYMENT_INVOICE_FOR_PA_USD, new JREmptyDataSource());
		else
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_PAYMENT_INVOICE_FOR_GroupPASnakeSport, new JREmptyDataSource());

		return Arrays.asList(jprint);
	}

	public static List<JasperPrint> generateEndorsementLifeNewInvoice(LifeEndorseInfo lifeEndorseInfo, LifeProposal lifeProposal, PaymentDTO payment) {
		int noOfInsured = 0;
		int noOfUnit = 0;
		double premiumRate = 0.0;
		boolean isEndorse = ProposalType.ENDORSEMENT.equals(lifeProposal.getProposalType());
		String period = "";
		String typeOfInsurance = "";
		String insuredPersonName = "";
		String startDate;
		int endoInsuredPerson = 0;
		double totolSumInsured = 0.0;
		int periodMonths = lifeProposal.getPeriodOfInsurance();
		String proposalNo = lifeProposal.getProposalNo();
		String customerName = lifeProposal.getCustomerName();
		String customerAddress = lifeProposal.getCustomerAddress();
		ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		String insuredPersonAddress = insuredPerson.getFullAddress();
		Product product = insuredPerson.getProduct();
		endoInsuredPerson = lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList().size();

		if (lifeProposal.getOrganization() == null) {
			int numberOfInsuredPerson = lifeProposal.getProposalInsuredPersonList().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = lifeProposal.getProposalInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredPersonName = lifeProposal.getOrganization().getName();
		}
		int addPerson = 0;
		if (lifeEndorseInfo != null) {
			addPerson = lifeEndorseInfo.getLifeEndorseInsuredPersonInfoList().size();
		}

		String agentRegNo = lifeProposal.getAgentLiscenseNo();
		String agentName = lifeProposal.getAgentName();
		String invoice = payment.getInvoiceNo();
		String invoiceDate = Utils.formatDateTimeWithAMOrPM(payment.getConfirmDate());
		Calendar cal = Calendar.getInstance();
		if (lifeProposal.getStartDate() == null) {
			cal.setTime(new Date());
			startDate = Utils.formattedDate(new Date());
		} else {
			cal.setTime(lifeProposal.getStartDate());
			startDate = Utils.formattedDate(lifeProposal.getStartDate());
		}

		if (KeyFactorChecker.isGroupLife(product)) {
			typeOfInsurance = "Group Life Insurance";
			noOfInsured = lifeProposal.getProposalInsuredPersonList().size();
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
			period = periodMonths + " Year";
			// insuredPersonName = customerName;
			insuredPersonAddress = customerAddress;
		} else if (KeyFactorChecker.isPersonalAccident(product)) {
			typeOfInsurance = "Personal Accident Insurance";
			period = periodMonths + " Months";
			// insuredPersonName = customerName;
			cal.add(Calendar.MONTH, lifeProposal.getPeriodMonth());
			premiumRate = insuredPerson.getPremiumRate();
		} else if (KeyFactorChecker.isFarmer(product)) {
			period = periodMonths + " Year";
			// insuredPersonName = customerName;
			typeOfInsurance = "Farmers Life Insurance";
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
		} else if (KeyFactorChecker.isSnakeBite(product.getId())) {
			period = periodMonths + " Year";
			typeOfInsurance = "Snake Bite Life Insurance";
			// insuredPersonName = customerName;
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
			noOfUnit = lifeProposal.getUnits();
		} else if (KeyFactorChecker.isSportMan(product)) {
			period = periodMonths + " Year";
			typeOfInsurance = "Sportsmen Life Insurance";
			// insuredPersonName = customerName;
			cal.add(Calendar.YEAR, lifeProposal.getPeriodOfYears());
			noOfUnit = lifeProposal.getUnits();
		}

		String endDate = Utils.formattedDate(cal.getTime());
		if (KeyFactorChecker.isSnakeBite(product.getId()) || KeyFactorChecker.isSportMan(product))
			totolSumInsured = lifeProposal.getSuminsuredPerUnit();
		else
			totolSumInsured = lifeProposal.getTotalSumInsured();

		double totalPremium = payment.getTotalAmount();
		double discount = payment.getTotalDiscountAmount();
		double stampFee = payment.getStampFees();
		double netPremium = payment.getTotalPremium();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("noOfUnit", noOfUnit);
		paramMap.put("noOfInsured", noOfInsured);
		paramMap.put("rate", premiumRate);
		paramMap.put("typeOfInsurance", typeOfInsurance);
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("invoiceNo", invoice);
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("policyNo", lifeEndorseInfo.getLifePolicyNo());
		paramMap.put("period", period);
		paramMap.put("fromDate", startDate);
		paramMap.put("addPerson", addPerson);
		paramMap.put("toDate", endDate);
		paramMap.put("insurdName", insuredPersonName);
		paramMap.put("address", insuredPersonAddress);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("netPremium", Utils.formattedCurrency(netPremium));
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("discount", discount);
		paramMap.put("stampFee", Utils.formattedCurrency(stampFee));
		paramMap.put("totalPremium", Utils.formattedCurrency(totalPremium));
		paramMap.put("totalSumInsured", Utils.formattedCurrency(totolSumInsured));
		paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprint;

		if (KeyFactorChecker.isFarmer(product))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDOPAYMENT_INVOICE_FOR_Farmer, new JREmptyDataSource());
		else if (KeyFactorChecker.isPersonalAccidentUSD(product))
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDOPAYMENT_INVOICE_FOR_PA_USD, new JREmptyDataSource());
		else
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDOPAYMENT_INVOICE_FOR_GroupPASnakeSport, new JREmptyDataSource());

		return Arrays.asList(jprint);

	}

	/** PublicLife,ShortTerm Invoice Letter */
	public static List<JasperPrint> generateLifePaymentInvoice(LifeProposal lifeProposal, PaymentDTO payment) {
		String invoiceDate = Utils.formatDateTimeWithAMOrPM(payment.getConfirmDate());
		Date stLifeinvoiceDate = payment.getConfirmDate();
		String invoice = payment.getInvoiceNo();
		String proposalNo = lifeProposal.getProposalNo();
		String policyHolderName = lifeProposal.getCustomerName();
		double sumInsured = lifeProposal.getTotalSumInsured();
		int periodMonths = lifeProposal.getPeriodOfInsurance();
		String period1 = periodMonths + " Years";
		String pPrd = periodMonths - 3 + " Years";
		String paymentType = lifeProposal.getPaymentType().getName();
		ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		String startDate = Utils.formattedDate(lifeProposal.getStartDate());
		String insuredName = insuredPerson.getFullName();
		String address = insuredPerson.getFullAddress();
		String agentRegNo = lifeProposal.getAgentLiscenseNo();
		String agentName = lifeProposal.getAgentName();
		double premium = lifeProposal.getPremium();
		String proposalEndate = Utils.formattedDate(DateUtils.proposalEndtDate(lifeProposal.getSubmittedDate(), periodMonths));
		String premiumEndDate = Utils.formattedDate(DateUtils.premiumEndDate(DateUtils.proposalEndtDate(lifeProposal.getSubmittedDate(), periodMonths), -3));
		double termPremium = lifeProposal.getTotalTermPremium();
		double discount = payment.getTotalDiscountAmount();
		double stampFee = payment.getStampFees();
		double totalPremium = payment.getTotalAmount();
		boolean isEndorse = ProposalType.ENDORSEMENT.equals(lifeProposal.getProposalType());
		Product product = insuredPerson.getProduct();
		boolean isStduentLife = KeyFactorChecker.isStudentLife(product.getId());

		String typeOfInsurance = "";
		if (KeyFactorChecker.isPublicLife(product)) {
			typeOfInsurance = "Endowment Life Insurance";
		} else if (KeyFactorChecker.isShortTermEndowment(product.getId())) {
			typeOfInsurance = "ShortTerm Endowment Life Insurance";
		} else if (KeyFactorChecker.isStudentLife(product.getId())) {
			typeOfInsurance = "Student Life Insurance";
		}

		if (lifeProposal.getOrganization() == null) {

			int numberOfInsuredPerson = lifeProposal.getProposalInsuredPersonList().size();
			if (numberOfInsuredPerson > 1) {
				insuredName = policyHolderName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredName = lifeProposal.getProposalInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredName = lifeProposal.getOrganization().getName();
		}
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isStduentLife", isStduentLife);
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("stLifeinvoiceDate", stLifeinvoiceDate);
		paramMap.put("invoiceNo", invoice);
		paramMap.put("startDate", startDate);
		paramMap.put("proposalEndate", proposalEndate);
		paramMap.put("premiumEndDate", premiumEndDate);
		paramMap.put("section", typeOfInsurance);
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("duenum", 1);
		paramMap.put("period1", period1);
		paramMap.put("pPrd", pPrd);
		paramMap.put("period", paymentType);
		paramMap.put("fromDate", startDate);
		paramMap.put("isEndorse", isEndorse);
		paramMap.put("insurdName", insuredName);
		paramMap.put("address", address);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("policyType", "New Policy");
		paramMap.put("premium",Utils.formattedCurrency(premium));
		paramMap.put("termPremium", Utils.formattedCurrency(termPremium));
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("policyHolderName", policyHolderName);
		paramMap.put("discount", discount);
		paramMap.put("sumInsured", Utils.formattedCurrency(sumInsured));
		paramMap.put("stampFee", stampFee);
		paramMap.put("endorsementPremium", lifeProposal.getEndorsementNetPremium());
		paramMap.put("totalPremium", Utils.formattedCurrency(totalPremium));
		paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, isStduentLife ? JasperTemplate.Student_Life_PAYMENT_INVOICE : JasperTemplate.Life_PAYMENT_INVOICE,
				new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** Endrose PublicLife,ShortTerm Invoice Letter */
	public static List<JasperPrint> generateEndorsementLifePaymentInvoice(LifeEndorseInfo lifeEndorseInfo,LifeEndorseInsuredPerson lifeEndorseInsuredPerson,LifeEndorseChange lifeEndorseChange, LifeProposal lifeProposal, PaymentDTO payment,List<Payment> oldPaymentList,List<Payment> newPaymentList,PolicyExtraAmount policyExtraAmount) {
		String invoiceDate = Utils.formatDateTimeWithAMOrPM(newPaymentList.get(0).getConfirmDate());
	
		Date stLifeinvoiceDate = newPaymentList.get(0).getConfirmDate();
		String invoice = newPaymentList.get(0).getInvoiceNo();
		String proposalNo = lifeProposal.getProposalNo();
		String policyHolderName = lifeProposal.getCustomerName();
		double sumInsured = lifeProposal.getTotalSumInsured();
		int periodMonths = lifeProposal.getPeriodOfInsurance();
		String period1 = periodMonths + " Years";
		String pPrd = periodMonths - 3 + " Years";
		String paymentType = lifeProposal.getPaymentType().getName();
		ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		String startDate = Utils.formattedDate(lifeProposal.getStartDate());
		String endDate=Utils.formattedDate(lifeProposal.getEndDate());
		String insuredName = insuredPerson.getFullName();
		String address = insuredPerson.getFullAddress();
		String agentRegNo = lifeProposal.getAgentLiscenseNo();
		String agentName = lifeProposal.getAgentName();
		double premium = lifeProposal.getPremium();
		String proposalEndate = Utils.formattedDate(DateUtils.proposalEndtDate(lifeProposal.getSubmittedDate(), periodMonths));
		String premiumEndDate = Utils.formattedDate(DateUtils.premiumEndDate(DateUtils.proposalEndtDate(lifeProposal.getSubmittedDate(), periodMonths), -3));
		double termPremium = lifeProposal.getTotalTermPremium();
		double discount = newPaymentList.get(0).getTotalDiscount();
		double stampFee = newPaymentList.get(0).getStampFees();
		double totalPremium = newPaymentList.get(0).getTotalAmount();
		boolean isEndorse = ProposalType.ENDORSEMENT.equals(lifeProposal.getProposalType());
		Product product = insuredPerson.getProduct();
		boolean isStduentLife = KeyFactorChecker.isStudentLife(product.getId());

		String typeOfInsurance = "";
		if (KeyFactorChecker.isPublicLife(product)) {
			typeOfInsurance = "Endowment Life Insurance";
		} else if (KeyFactorChecker.isShortTermEndowment(product.getId())) {
			typeOfInsurance = "ShortTerm Endowment Life Insurance";
		} else if (KeyFactorChecker.isStudentLife(product.getId())) {
			typeOfInsurance = "Student Life Insurance";
		}
		if (lifeProposal.getOrganization() == null) {

			int numberOfInsuredPerson = lifeProposal.getProposalInsuredPersonList().size();
			if (numberOfInsuredPerson > 1) {
				insuredName = policyHolderName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredName = lifeProposal.getProposalInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredName = lifeProposal.getOrganization().getName();

		}
		
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		Map<String, Object> paramMap = new HashMap<>();
		if(isEndorse) {
			paramMap.put("endorsementType",lifeEndorseChange.getLifeEndorseItem().getLabel());
			if(policyExtraAmount!=null) {
				double needPremium=lifeProposal.getTotalPremium()-policyExtraAmount.getExtraAmount();
				paramMap.put("extraPremium", Utils.getCurrencyFormatString(policyExtraAmount.getExtraAmount()));
				paramMap.put("needPremium", Utils.getCurrencyFormatString(needPremium));
			}else {
				paramMap.put("extraPremium", "0.00");
				paramMap.put("needPremium", Utils.formattedCurrency(payment.getTotalPremium()));
			}
		}
		paramMap.put("isStduentLife", isStduentLife);
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("stLifeinvoiceDate", stLifeinvoiceDate);
		paramMap.put("invoiceNo", invoice);
		paramMap.put("startDate", startDate);
		paramMap.put("proposalEndate", proposalEndate);
		paramMap.put("premiumEndDate", premiumEndDate);
		paramMap.put("section", typeOfInsurance);
		//paramMap.put("proposalNo", proposalNo);
	 paramMap.put("policyNo", lifeEndorseInfo.getLifePolicyNo());
	 int dueno = newPaymentList.stream().mapToInt(v -> v.getToTerm()).max().orElseThrow(NoSuchElementException::new);

		double oldtotalAmount=0.0;
		double newtotalAmount=0.0;
		int dueNo=0;
		double newCoverAmount=0.0;
 
			
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(lifeProposal.getStartDate());
		calendar1.add(Calendar.MONTH, lifeProposal.getPaymentType().getMonth() * (dueno - 1));
		Date dueDate = calendar1.getTime();
		
for (Payment p : oldPaymentList) {
			
			oldtotalAmount += p.getBasicPremium();
			 dueNo=oldPaymentList.size();
		}
for (Payment p : newPaymentList) {
			
	        newtotalAmount += p.getBasicPremium();
			 
		}
	
		newCoverAmount=newPaymentList.get(0).getBasicPremium()*lifeEndorseChange.getCoverTime();
		double needAmount=newPaymentList.get(0).getBasicPremium()-lifeEndorseChange.getRefundAmount();
		//int nextDue=dueNo+lifeEndorseChange.getCoverTime();
		int nextDue = newPaymentList.stream().filter(a -> a.isComplete()).mapToInt(v -> v.getToTerm()).max().orElseThrow(NoSuchElementException::new);
		Date dueDate2=calculateDateForEndorse(lifeProposal.getPaymentType().getMonth(),lifeProposal.getLifePolicy().getCoverageDate());

		
		paramMap.put("duenum",dueno);
		paramMap.put("period1", period1);
		paramMap.put("pPrd", pPrd);
		paramMap.put("period", paymentType);
		paramMap.put("fromDate",Utils.getDateFormatString(dueDate2));
		paramMap.put("isEndorse", isEndorse);
		paramMap.put("insurdName", insuredName);
		paramMap.put("address", address);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("policyType", "New Policy");
		paramMap.put("premium",Utils.getCurrencyFormatString(premium));
		paramMap.put("termPremium", Utils.getCurrencyFormatString(termPremium));
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("policyHolderName", policyHolderName);
		paramMap.put("discount", discount);
		paramMap.put("sumInsured", Utils.getCurrencyFormatString(sumInsured));
		paramMap.put("stampFee", stampFee);
		paramMap.put("endorsementPremium", lifeProposal.getEndorsementNetPremium());
		paramMap.put("totalPremium",Utils.getCurrencyFormatString(totalPremium));
		paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		if(lifeEndorseChange.getLifeEndorseItem().getLabel()=="Decrease Term") {
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, isStduentLife ? JasperTemplate.Student_Life_ENDOPAYMENT_INVOICE : JasperTemplate.Life_PAYMENT_INVOICE,
				new JREmptyDataSource());
		jasperPrintList.add(jprint);
		}
		
		
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("customerName", policyHolderName);
		map2.put("policyNo", lifeEndorseInfo.getLifePolicyNo());
		map2.put("newvalue", lifeEndorseChange.getNewValue());
		map2.put("oldvalue", lifeEndorseChange.getOldValue());
		map2.put("sumInsured", sumInsured);
		map2.put("oldPeriod", periodMonths);
		map2.put("startDate", startDate);
		map2.put("endDate", endDate);
		map2.put("oldTotalAmount",oldtotalAmount);
		map2.put("newTotalAmount",Utils.getCurrencyFormatString(newtotalAmount));
		map2.put("coverTime",lifeEndorseChange.getCoverTime());
		map2.put("newCoverAmount",lifeEndorseChange.getEndorsePremium());
		map2.put("coverAmount",Utils.getCurrencyFormatString(newCoverAmount));
		map2.put("refurmAmount",lifeEndorseChange.getRefundAmount());
		map2.put("needAmount",Utils.getCurrencyFormatString(needAmount));
		map2.put("nextDue",nextDue );
		map2.put("dueNo",dueNo);
		map2.put("dueDate",Utils.getDateFormatString(dueDate2));
		map2.put("newPremium",Utils.getCurrencyFormatString(newPaymentList.get(0).getBasicPremium()) );
		map2.put("newPremium1",newPaymentList.get(0).getBasicPremium() );
		map2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		double newEndorsePremium=newPaymentList.get(0).getAmount();
		String oldValue=lifeEndorseChange.getOldValue();
		String newValue=lifeEndorseChange.getNewValue();
		oldPaymentList=oldPaymentList.stream().map(a->{
			a.setNewEndrosePremium(newEndorsePremium);
			a.setNewValue(newValue);
			a.setOldValue(oldValue);
			return a;
		}).collect(Collectors.toList());
		map2.put("listDataSource1", new JRBeanCollectionDataSource(oldPaymentList));
		if(lifeEndorseChange.getLifeEndorseItem().getLabel()=="Increas Term") {
			JasperPrint jprint2 = JasperFactory.generateJasperPrint(map2, JasperTemplate.Endorsement_ATTFORINCREASETERM, new JREmptyDataSource());
			jasperPrintList.add(jprint2);
		}
		
		if(lifeEndorseChange.getLifeEndorseItem().getLabel()=="PaymentTypeChange") {
			List<PaymentTypeChangeReportDTO> list = new ArrayList<PaymentTypeChangeReportDTO>();
			list.addAll(generatePaymentTypeChangeReportList(oldPaymentList, newPaymentList));
			map2.put("listDataSource1", new JRBeanCollectionDataSource(list));
			JasperPrint jprint2 = JasperFactory.generateJasperPrint(map2, JasperTemplate.Endorsement_ATTFORPAYMENTTYPE, new JREmptyDataSource());
			jasperPrintList.add(jprint2);
		}
	
		if(lifeEndorseChange.getLifeEndorseItem().getLabel()=="Decrease Term") {
			JasperPrint jprint2 = JasperFactory.generateJasperPrint(map2, JasperTemplate.Endorsement_ATTFORDECREASETERM, new JREmptyDataSource());
			jasperPrintList.add(jprint2);
		}

		if(lifeEndorseChange.getLifeEndorseItem().getLabel()=="Decrease SI") {
		JasperPrint jprint2 = JasperFactory.generateJasperPrint(map2, JasperTemplate.Endorsement_ATT, new JREmptyDataSource());
		jasperPrintList.add(jprint2);
	}
		return jasperPrintList;
	}

	private static List<PaymentTypeChangeReportDTO> generatePaymentTypeChangeReportList(List<Payment> oldPaymentList, List<Payment> newPaymentList) {	
		List<PaymentTypeChangeReportDTO> dtoList = new ArrayList<>();
		//check paymentList size for loop
		if(oldPaymentList.size()> newPaymentList.size()) {
			for(int i= 0;i<oldPaymentList.size();i++) {
				//old
				PaymentTypeChangeReportDTO paymentDto = new PaymentTypeChangeReportDTO();
				paymentDto.setOldAmount(oldPaymentList.get(i).getAmount());
				paymentDto.setOldDueNo(oldPaymentList.get(i).getToTerm());
				paymentDto.setPaymentDate(oldPaymentList.get(i).getPaymentDate());
				paymentDto.setReceiptNO(oldPaymentList.get(i).getReceiptNo());
				paymentDto.setOldValue(oldPaymentList.get(i).getOldValue());
				paymentDto.setNewValue(oldPaymentList.get(i).getNewValue());

				//new
				if(i <newPaymentList.size()) {
					paymentDto.setNewAmount(newPaymentList.get(i).getAmount());
					paymentDto.setNewDueNo(i+1);
				}	
				dtoList.add(paymentDto);
			}	
		}else {
			for(int i= 0;i<newPaymentList.size();i++) {
				//old
				PaymentTypeChangeReportDTO paymentDto = new PaymentTypeChangeReportDTO();
				paymentDto.setNewAmount(newPaymentList.get(i).getAmount());
				paymentDto.setNewDueNo(i+1);
				//new
				if(i <oldPaymentList.size()) {
					paymentDto.setOldAmount(oldPaymentList.get(i).getAmount());
					paymentDto.setOldDueNo(oldPaymentList.get(i).getToTerm());
					paymentDto.setPaymentDate(oldPaymentList.get(i).getPaymentDate());
					paymentDto.setReceiptNO(oldPaymentList.get(i).getReceiptNo());
					paymentDto.setOldValue(oldPaymentList.get(i).getOldValue());
					paymentDto.setNewValue(oldPaymentList.get(i).getNewValue());
				}
				dtoList.add(paymentDto);
			}
		}
		return dtoList;		
	}

	private static Date calculateDateForEndorse(int month, Date coverageDate) {
		Calendar cal_2 = Calendar.getInstance();
		cal_2.setTime(coverageDate);
		cal_2.add(Calendar.MONTH, month);
		return cal_2.getTime();
		
	}
	/** All Life Receipt Letter */
	public static List<JasperPrint> generateLifeReceiptLetter(LifeProposal lifeProposal, Payment payment, Boolean isSportsMan) {
		List<JasperPrint> printList = new ArrayList<>();
		String insuredPersonName = "";
		ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		Product product = insuredPerson.getProduct();
		String receiptNo = payment.getReceiptNo();
		String paymentDate = Utils.formatDateTimeWithAMOrPM(payment.getPaymentDate());
		String customerName = lifeProposal.getCustomerName();
		String address = lifeProposal.getCustomerAddress();
		if (lifeProposal.getOrganization() == null) {
			int numberOfInsuredPerson = lifeProposal.getProposalInsuredPersonList().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = lifeProposal.getProposalInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredPersonName = lifeProposal.getOrganization().getName();
		}
		String insuredPersonAddress = insuredPerson.getFullAddress();
		boolean isEndorse = ProposalType.ENDORSEMENT.equals(lifeProposal.getProposalType());
		String policyNo = "";
		String policyno = "";
		if (isEndorse) {
			policyNo = lifeProposal.getLifePolicy().getPolicyNo();
			policyno = lifeProposal.getProposalNo();
		} else {
			policyNo = lifeProposal.getProposalNo();
		}

		String agentInfo = lifeProposal.getAgentNameNLiscenseNo();
		String proposalType = null;
		double discount = payment.getTotalDiscount();
		String paymentType = "";
		if (isEndorse) {
			paymentType = lifeProposal.getPaymentType()
					.getName();/* +"(ENDO)"; */
		} else {
			paymentType = lifeProposal.getPaymentType().getName();
		}

		String period = KeyFactorChecker.isPersonalAccident(product) ? lifeProposal.getPeriodOfInsurance() + " Months"
				: (lifeProposal.getPeriodOfInsurance() > 1 ? lifeProposal.getPeriodOfInsurance() + " Years" : lifeProposal.getPeriodOfInsurance() + " Year");
		String prmPeriod = lifeProposal.getPeriodOfInsurance() - 3 + " Years ";
		String startDate = DateUtils.formatDateToString(lifeProposal.getStartDate());
		String endDate = DateUtils.formatDateToString(lifeProposal.getEndDate());
		String premiumEndDate = Utils.formattedDate(DateUtils.premiumEndDate(lifeProposal.getEndDate(), -3));
		String totalSI = "";
		if (isEndorse) {
			totalSI = Utils.formattedCurrency(
					lifeProposal.getTotalSumInsured())/* +"(ENDO)" */;
		} else {
			totalSI = Utils.formattedCurrency(lifeProposal.getTotalSumInsured());
		}
		int units = lifeProposal.getUnits();
		double suminsuredPerUnit = lifeProposal.getSuminsuredPerUnit();
		double premium = payment.getTotalPremium();
		double stampFee = payment.getStampFees();
		double totalPremium = payment.getTotalAmount();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String premiumInword = convertor.getNameWithDecimal(totalPremium);
		String usdPremiumInword = convertor.getNameWithDollarDecimal(payment.getTotalPremium());
		if (stampFee > 0.0) {
			String stampFeeInword = convertor.getNameWithDecimal(stampFee);
			usdPremiumInword = usdPremiumInword.substring(0, usdPremiumInword.length() - 4);
			usdPremiumInword = convertor.getConcateDollorAndStampFee(usdPremiumInword, stampFeeInword);
		}
		String premiumRate = lifeProposal.getPremiumRateStr();
		boolean isPublicLife = false;
		boolean isGroupLife = false;
		boolean isFarmer = false;
		boolean isShortterm = false;
		boolean isPersonAccidKYT = false;
		boolean isPersonAccidUSD = false;
		boolean isSportMan = false;
		boolean isSnakeBite = false;
		boolean isStudentLife = false;
		boolean isPublicTermLife = false;

		if (KeyFactorChecker.isPublicLife(product)) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isPublicLife = true;
		} else if (KeyFactorChecker.isShortTermEndowment(product.getId())) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isShortterm = true;
		} else if (KeyFactorChecker.isGroupLife(product)) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isGroupLife = true;
			insuredPersonAddress = address;
		} else if (KeyFactorChecker.isPersonalAccidentKYT(product)) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isPersonAccidKYT = true;
		} else if (KeyFactorChecker.isPersonalAccidentUSD(product)) {

			// insuredPersonName = lifeProposal.getCustomerName();
			isPersonAccidUSD = true;
		} else if (KeyFactorChecker.isFarmer(product)) {

			// insuredPersonName = lifeProposal.getCustomerName();
			isFarmer = true;
		} else if (KeyFactorChecker.isSportMan(product)) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isSportMan = true;
			insuredPersonAddress = address;
		} else if (KeyFactorChecker.isStudentLife(product.getId())) {
			// insuredPersonName = insuredPerson.getFullName();
			isStudentLife = true;
		} else if (KeyFactorChecker.isPublicTermLife(product.getId())) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isPublicTermLife = true;
		} else {
			// insuredPersonName = lifeProposal.getCustomerName();
			isSnakeBite = true;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(lifeProposal.getStartDate());
		int dueno = lifeProposal.getPaymentType().getMonth();
		cal.add(Calendar.MONTH, dueno);
		String due = Utils.formattedDate(cal.getTime());

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isFarmer", isFarmer);
		paramMap.put("isGroupLife", isGroupLife);
		paramMap.put("isPublicLife", isPublicLife);
		paramMap.put("isShortterm", isShortterm);
		paramMap.put("isStudentLife", isStudentLife);
		paramMap.put("isSportMan", isSportMan);
		paramMap.put("isSnakeBite", isSnakeBite);
		paramMap.put("paymentType", paymentType);
		paramMap.put("receiptNo", receiptNo);
		paramMap.put("receiptDate", paymentDate);
		paramMap.put("insuredPersonName", insuredPersonName);
		if (KeyFactorChecker.isStudentLife(product.getId())) {
			paramMap.put("customerName", customerName);
		} else {
			paramMap.put("customerName", insuredPersonName);
		}
		paramMap.put("address", insuredPersonAddress);
		paramMap.put("proposalNo", policyNo);
		paramMap.put("policyno", policyno);
		paramMap.put("agent", agentInfo);
		paramMap.put("proposalType", proposalType);
		paramMap.put("policyPeriod", period);
		paramMap.put("prmPeriod", prmPeriod);
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("premiumEndDate", premiumEndDate);
		paramMap.put("totalSi", totalSI == "0.00" ? suminsuredPerUnit : totalSI);
		paramMap.put("premium", premium);
		paramMap.put("adjustAmount", "0.00");
		paramMap.put("discount", discount);
		paramMap.put("isEndorse", isEndorse);
		paramMap.put("stampFee", stampFee);
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("premiumInWord", premiumInword);
		paramMap.put("usdPremiumInword", usdPremiumInword);
		paramMap.put("billCollectTerm", "1" );
		paramMap.put("suminsuredPerUnit", suminsuredPerUnit);
		paramMap.put("unit", units);
		paramMap.put("premiumRate", premiumRate);
		paramMap.put("receiptBG", ApplicationSetting.getReceipt4Fly());
		paramMap.put("dueDate", startDate);

		for (int i = 0; i <= 1; i++) {
			paramMap.put("isCopy", i == 1 ? true : false);
			JasperPrint jprint = new JasperPrint();
			if (isPersonAccidUSD) {
				paramMap.put("noOfPerson", lifeProposal.getProposalInsuredPersonList().size());
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_PAUSD, new JREmptyDataSource());
			} else if (isGroupLife || isPersonAccidKYT) {
				paramMap.put("noOfPerson", lifeProposal.getProposalInsuredPersonList().size());
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_GROUPANDPA, new JREmptyDataSource());
			} else if (isPublicLife || isShortterm || isPublicTermLife)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_PUBLICANDSHORT, new JREmptyDataSource());
			else if (isStudentLife)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_STUDENTLIFE, new JREmptyDataSource());
			else if (isFarmer || isSportMan || isSnakeBite)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_FARMER_SPORTMAN_SNB, new JREmptyDataSource());
			printList.add(jprint);
		}
		return printList;
	}

	public static List<JasperPrint> generateLifeEndoReceiptLetter(List<LifeEndorseChange> lifeEndorseChange, LifeProposal lifeProposal, Payment payment,Boolean isSportsMan) {
		List<JasperPrint> printList = new ArrayList<>();
		String insuredPersonName = "";
		ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
		Product product = insuredPerson.getProduct();
		String receiptNo = payment.getReceiptNo();
		String paymentDate = Utils.formatDateTimeWithAMOrPM(payment.getPaymentDate());
		String customerName = lifeProposal.getCustomerName();
		String address = lifeProposal.getCustomerAddress();
		double totalPremium = payment.getTotalAmount();
		if (lifeProposal.getOrganization() == null) {
			int numberOfInsuredPerson = lifeProposal.getProposalInsuredPersonList().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = lifeProposal.getProposalInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredPersonName = lifeProposal.getOrganization().getName();
		}
		String insuredPersonAddress = insuredPerson.getFullAddress();
		boolean isEndorse = ProposalType.ENDORSEMENT.equals(lifeProposal.getProposalType());
		String policyNo = "";
		String policyno = "";
		if (isEndorse) {
			policyNo = lifeProposal.getLifePolicy().getPolicyNo();
			policyno = lifeProposal.getProposalNo();
		} else {
			policyNo = lifeProposal.getProposalNo();
		}
		String adjustAmount="0.00";
		
		String agentInfo = lifeProposal.getAgentNameNLiscenseNo();
		String proposalType = null;
		double discount = payment.getTotalDiscount();
		String paymentType = lifeProposal.getPaymentType().getName();

		paymentType = lifeProposal.getPaymentType().getName();
		String period = KeyFactorChecker.isPersonalAccident(product) ? lifeProposal.getPeriodOfInsurance() + " Months"
				: (lifeProposal.getPeriodOfInsurance() > 1 ? lifeProposal.getPeriodOfInsurance() + " Years" : lifeProposal.getPeriodOfInsurance() + " Year");
		String prmPeriod = lifeProposal.getPeriodOfInsurance() - 3 + " Years ";
		String endDate = "";
		String startDate = "";
		for (LifeEndorseChange endChange : lifeEndorseChange) {
			if (endChange != null) {
				if (endChange.getLifeEndorseItem().label == "Increas Term") {
					startDate = DateUtils.formatDateToString(lifeProposal.getStartDate());
					endDate = DateUtils.formatDateToString(lifeProposal.getEndDate()) + "(ENDO)";
				} else if (endChange.getLifeEndorseItem().label == "Decrease Term") {
					startDate = DateUtils.formatDateToString(lifeProposal.getStartDate());
					endDate = DateUtils.formatDateToString(lifeProposal.getEndDate()) + "(ENDO from  "+endChange.getOldValue()+" Years)";
				} else {
					startDate = DateUtils.formatDateToString(lifeProposal.getStartDate());
					endDate = DateUtils.formatDateToString(lifeProposal.getEndDate());
				}

			}
		}

		String totalSI = "";
		for (LifeEndorseChange endChange : lifeEndorseChange) {
			if (endChange != null) {
				if (endChange.getLifeEndorseItem().label == "Increase SI") {
					totalSI = Utils.formattedCurrency(lifeProposal.getTotalSumInsured()) + "(ENDO)";
				} else if (endChange.getLifeEndorseItem().label == "Decrease SI") {
					totalSI = Utils.formattedCurrency(lifeProposal.getTotalSumInsured()) + "(ENDO)";
				} else {
					totalSI = Utils.formattedCurrency(lifeProposal.getTotalSumInsured());
				}
			} else {
				totalSI = Utils.formattedCurrency(lifeProposal.getTotalSumInsured());
			}
		}

		String premiumEndDate = Utils.formattedDate(DateUtils.premiumEndDate(lifeProposal.getEndDate(), -3));
		int units = lifeProposal.getUnits();
		double suminsuredPerUnit = lifeProposal.getSuminsuredPerUnit();
		double premium = payment.getTotalPremium();
		double stampFee = payment.getStampFees();
		
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String premiumInword = convertor.getNameWithDecimal(totalPremium);
		String usdPremiumInword = convertor.getNameWithDollarDecimal(payment.getTotalPremium());
		if (stampFee > 0.0) {
			String stampFeeInword = convertor.getNameWithDecimal(stampFee);
			usdPremiumInword = usdPremiumInword.substring(0, usdPremiumInword.length() - 4);
			usdPremiumInword = convertor.getConcateDollorAndStampFee(usdPremiumInword, stampFeeInword);
		}
		String premiumRate = lifeProposal.getPremiumRateStr();
		boolean isPublicLife = false;
		boolean isGroupLife = false;
		boolean isFarmer = false;
		boolean isShortterm = false;
		boolean isPersonAccidKYT = false;
		boolean isPersonAccidUSD = false;
		boolean isSportMan = false;
		boolean isSnakeBite = false;
		boolean isStudentLife = false;
		boolean isPublicTermLife = false;

		if (KeyFactorChecker.isPublicLife(product)) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isPublicLife = true;
		} else if (KeyFactorChecker.isShortTermEndowment(product.getId())) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isShortterm = true;
		} else if (KeyFactorChecker.isGroupLife(product)) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isGroupLife = true;
			insuredPersonAddress = address;
		} else if (KeyFactorChecker.isPersonalAccidentKYT(product)) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isPersonAccidKYT = true;
		} else if (KeyFactorChecker.isPersonalAccidentUSD(product)) {

			// insuredPersonName = lifeProposal.getCustomerName();
			isPersonAccidUSD = true;
		} else if (KeyFactorChecker.isFarmer(product)) {

			// insuredPersonName = lifeProposal.getCustomerName();
			isFarmer = true;
		} else if (KeyFactorChecker.isSportMan(product)) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isSportMan = true;
			insuredPersonAddress = address;
		} else if (KeyFactorChecker.isStudentLife(product.getId())) {
			// insuredPersonName = insuredPerson.getFullName();
			isStudentLife = true;
		} else if (KeyFactorChecker.isPublicTermLife(product.getId())) {
			// insuredPersonName = lifeProposal.getCustomerName();
			isPublicTermLife = true;
		} else {
			// insuredPersonName = lifeProposal.getCustomerName();
			isSnakeBite = true;
		}

	
		 int dueno =lifeProposal.getLifePolicy().getLastPaymentTerm();		 
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(lifeProposal.getStartDate());
			calendar1.add(Calendar.MONTH, lifeProposal.getPaymentType().getMonth() * (dueno - 1));
			Date dueDate = calendar1.getTime();
			Date dueDate2=calculateDateForEndorse(lifeProposal.getPaymentType().getMonth(),lifeProposal.getLifePolicy().getCoverageDate());

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isFarmer", isFarmer);
		paramMap.put("isGroupLife", isGroupLife);
		paramMap.put("isPublicLife", isPublicLife);
		paramMap.put("isShortterm", isShortterm);
		paramMap.put("isStudentLife", isStudentLife);
		paramMap.put("isSportMan", isSportMan);
		paramMap.put("isSnakeBite", isSnakeBite);
		paramMap.put("paymentType", paymentType);
		paramMap.put("receiptNo", receiptNo);
		paramMap.put("receiptDate", paymentDate);
		paramMap.put("insuredPersonName", insuredPersonName);
		paramMap.put("customerName", customerName);
		paramMap.put("address", insuredPersonAddress);
		paramMap.put("proposalNo", policyNo);
		paramMap.put("policyNo", policyno);
		paramMap.put("agent", agentInfo);
		paramMap.put("proposalType", proposalType);
		paramMap.put("policyPeriod", period);
		paramMap.put("prmPeriod", prmPeriod);
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("premiumEndDate", premiumEndDate);
		paramMap.put("totalSi", totalSI == "0.00" ? suminsuredPerUnit : totalSI);
		paramMap.put("premium", premium);
		paramMap.put("adjustAmount", adjustAmount);
		paramMap.put("discount", discount);
		paramMap.put("isEndorse", isEndorse);
		paramMap.put("stampFee", stampFee);
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("premiumInWord", premiumInword);
		paramMap.put("usdPremiumInword", usdPremiumInword);
		paramMap.put("billCollectTerm", String.valueOf(dueno));
		paramMap.put("suminsuredPerUnit", suminsuredPerUnit);
		paramMap.put("unit", units);
		paramMap.put("premiumRate", premiumRate);
		paramMap.put("receiptBG", ApplicationSetting.getReceipt4Fly());
		paramMap.put("dueDate",Utils.getDateFormatString(dueDate2));

		for (int i = 0; i <= 1; i++) {
			paramMap.put("isCopy", i == 1 ? true : false);
			JasperPrint jprint = new JasperPrint();
			if (isPersonAccidUSD) {
				paramMap.put("noOfPerson", lifeProposal.getProposalInsuredPersonList().size());
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDORECEIPT_FOR_PAUSD, new JREmptyDataSource());
			} else if (isGroupLife || isPersonAccidKYT) {
				for (LifeEndorseChange endChange : lifeEndorseChange) {
					if (endChange.getLifeEndorseInsuredPerson().getInsuredPersonEndorseStatus().getLabel() == "New") {
						paramMap.put("noOfPerson", lifeProposal.getProposalInsuredPersonList().size() + " á€¦á€¸ (ENDO)");
					} else if (endChange.getLifeEndorseInsuredPerson().getInsuredPersonEndorseStatus().getLabel() == "Terminate") {
						paramMap.put("noOfPerson", lifeProposal.getProposalInsuredPersonList().size() + "á€¦á€¸  (ENDO)");
					} else {
						paramMap.put("noOfPerson", lifeProposal.getProposalInsuredPersonList().size());
					}
				}

				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDORECEIPT_FOR_GROUPANDPA, new JREmptyDataSource());
			} else if (isPublicLife || isShortterm || isPublicTermLife)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDORECEIPT_FOR_PUBLICANDSHORT, new JREmptyDataSource());
			else if (isStudentLife)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDORECEIPT_FOR_STUDENTLIFE, new JREmptyDataSource());
			else if (isFarmer || isSportMan || isSnakeBite)
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDORECEIPT_FOR_FARMER_SPORTMAN_SNB, new JREmptyDataSource());
			printList.add(jprint);
		}
		return printList;
	}

	public static List<JasperPrint> generateSportManAbroadCertificatAndInvoice(List<SportManTravelAbroad> sportManTravelAbroadList, Payment payment) {
		List<JasperPrint> printList = new ArrayList<JasperPrint>();
		List<JasperPrint> print1 = new ArrayList<JasperPrint>();
		List<JasperPrint> print2 = new ArrayList<JasperPrint>();
		LifePolicy lifePolicy = sportManTravelAbroadList.get(0).getPolicyInsuredPerson().getLifePolicy();
		print1 = generateSportManAbroadInvoice(lifePolicy, payment);
		printList.addAll(print1);
		print2 = generateSportManAbroadCertificate(sportManTravelAbroadList, payment);
		printList.addAll(print2);
		return printList;
	}

	private static List<JasperPrint> generateSportManAbroadInvoice(LifePolicy lifePolicy, Payment payment) {
		int noOfInsured = 0;
		int noOfUnit = 0;
		double rate = 0.0;
		double totolSumInsured = 1000000.0;
		String proposalNo = lifePolicy.getProposalNo();
		String customerName = lifePolicy.getCustomerName();
		String address = lifePolicy.getCustomerAddress();
		String agentRegNo = lifePolicy.getAgent() == null ? " " : lifePolicy.getAgent().getLiscenseNo();
		String agentName = lifePolicy.getAgent() == null ? " " : lifePolicy.getAgent().getFullName();
		String invoice = payment.getInvoiceNo();
		String invoiceDate = Utils.formattedDate(payment.getConfirmDate());
		String typeOfInsurance = "Sportsmen Life Insurance";
		noOfUnit = lifePolicy.getTotalUnit();
		double totalPremium = payment.getTotalAmount();
		String stampFee = String.valueOf(payment.getStampFees());
		String netPremium = String.valueOf(payment.getTotalPremium());
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("noOfUnit", noOfUnit);
		paramMap.put("noOfInsured", noOfInsured);
		paramMap.put("rate", rate);
		paramMap.put("typeOfInsurance", typeOfInsurance);
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("invoiceNo", invoice);
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("period", lifePolicy.getPeriodOfInsurance() + " Year");
		paramMap.put("fromDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
		paramMap.put("toDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
		paramMap.put("insurdName", customerName);
		paramMap.put("address", address);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("netPremium", netPremium);
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("stampFee", stampFee);
		paramMap.put("discount", payment.getTotalDiscount());
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("totalSumInsured", totolSumInsured);
		paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprint;
		jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_PAYMENT_INVOICE_FOR_GroupPASnakeSport, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	private static List<JasperPrint> generateSportManAbroadCertificate(List<SportManTravelAbroad> sportManTravelAbroadList, Payment payment) {
		LifePolicy lifePolicy = sportManTravelAbroadList.get(0).getPolicyInsuredPerson().getLifePolicy();
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		String policyNo = lifePolicy.getPolicyNo();
		JasperPrint jprint = null;
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		for (SportManTravelAbroad sportMan : sportManTravelAbroadList) {
			PolicyInsuredPerson insuredPerson = sportMan.getPolicyInsuredPerson();
			PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
			double premium = sportMan.getTotalPremium();
			String premiumInword = convertor.getNameWithDecimal(premium);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("agenyName", agentName);
			paramMap.put("agenyLicenseNo", agentLicenseNo);
			paramMap.put("policyNo", policyNo);
			paramMap.put("startDate", Utils.formattedDate(sportMan.getTravelStartDate()));
			paramMap.put("endDate", Utils.formattedDate(sportMan.getTravelEndDate()));
			paramMap.put("travelPath", sportMan.getFromCity() + " To " + sportMan.getToCity());
			paramMap.put("insuredName", insuredPerson.getFullName());
			paramMap.put("insuredNrc", insuredPerson.getIdNo());
			paramMap.put("typeOfSport", insuredPerson.getTypesOfSport() != null ? insuredPerson.getTypesOfSport().getName() : null);
			paramMap.put("insuredAddress",
					insuredPerson.getResidentAddress() != null ? insuredPerson.getResidentAddress().getFullResidentAddress() : lifePolicy.getCustomerAddress());
			String phno = insuredPerson.getCustomer() != null ? insuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo() : lifePolicy.getCustomerPhoneNo();
			paramMap.put("phno", phno);
			paramMap.put("premium", Utils.formattedCurrency(premium));
			paramMap.put("premiumInword", premiumInword);
			paramMap.put("benefiName", beneifitPerson.getFullName());
			paramMap.put("benefiNrc", beneifitPerson.getIdNo());
			paramMap.put("relationship", beneifitPerson.getRelationship().getName());
			paramMap.put("benifiPhon", phno);
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SPORT_MAN_ABROAD_CERTIFICATE, new JREmptyDataSource());
			jprintList.add(jprint);
		}
		return jprintList;
	}

	/** ShortTerm Policy Letter */
	public static List<JasperPrint> generateShortTermLifePolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		String paymentDate = (paymentDTO.getPaymentDate() == null) ? "-" : Utils.formattedDate(paymentDTO.getPaymentDate());
		String startDate = (lifePolicy.getActivedPolicyStartDate() == null) ? "-" : Utils.formattedDate(lifePolicy.getActivedPolicyStartDate());
		String endDate = (lifePolicy.getActivedPolicyEndDate() == null) ? "-" : Utils.formattedDate(lifePolicy.getActivedPolicyEndDate());
		String endDateString = (lifePolicy.getActivedPolicyEndDate() == null) ? "-" : Utils.formattedDate(lifePolicy.getActivedPolicyEndDate(), "dd-MMMM_yyyy");
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		List<PolicyInsuredPersonBeneficiaries> beneifitPersonList = insuredPerson.getPolicyInsuredPersonBeneficiariesList();

		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("nrcNo", insuredPerson.getIdNo());
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
		paramMap.put("paymentDate", Utils.formattedDate(paymentDTO.getPaymentDate()));
		paramMap.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
		paramMap.put("insuredName", insuredPerson.getFullName());
		paramMap.put("nextYrAge", insuredPerson.getAge());
		paramMap.put("insuredAddress", insuredPerson.getResidentAddress().getFullResidentAddress());
		if (insuredPerson.getOccupation() != null)
			paramMap.put("occupation", insuredPerson.getOccupation().getName());
		else
			paramMap.put("occupation", " ");

		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("sumInsured", lifePolicy.getTotalSumInsured());
		paramMap.put("paymentType", lifePolicy.getPaymentType().getMonth());
		paramMap.put("paymentTypeName", lifePolicy.getPaymentType().getName());
		if (lifePolicy.getPaymentType().getMonth() > 1) {
			paramMap.put("isYearly", true);
		} else {
			paramMap.put("isYearly", false);

		}
		paramMap.put("termPremium", lifePolicy.getTotalPremium());
		paramMap.put("invoicePremium", lifePolicy.getTotalTermPremium());
		paramMap.put("coverageDateString", lifePolicy.getTimeSlotListStr());
		int size = beneifitPersonList.size();
		JasperPrint benefitJprint = null;
		if (size > 2) {
			paramMap.put("benefiName", "AS PER ATTACHMENT");
			paramMap.put("relationship", "AS PER ATTACHMENT");
			Map<String, Object> paramMapBenefit = new HashMap<String, Object>();
			paramMapBenefit.put("policyNo", lifePolicy.getPolicyNo());
			paramMapBenefit.put("idNo", insuredPerson.getIdNo());
			paramMapBenefit.put("insuredPersonName", lifePolicy.getCustomerName());
			paramMapBenefit.put("nrc", insuredPerson.getIdNo());
			paramMapBenefit.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			paramMapBenefit.put("listDataSource", new JRBeanCollectionDataSource(beneifitPersonList));
			benefitJprint = JasperFactory.generateJasperPrint(paramMapBenefit, JasperTemplate.BENEFICIARY_ATTACH, new JREmptyDataSource());
		} else {
			PolicyInsuredPersonBeneficiaries beneifitPerson = beneifitPersonList.get(0);
			paramMap.put("benefiName", beneifitPerson.getFullName());
			paramMap.put("relationship", beneifitPerson.getRelationship().getName());
			paramMap.put("percentage", beneifitPerson.getPercentage());
			if (size > 1) {
				PolicyInsuredPersonBeneficiaries beneifitPerson2 = beneifitPersonList.get(1);
				paramMap.put("benefiName2", beneifitPerson2.getFullName());
				paramMap.put("relationship2", beneifitPerson2.getRelationship().getName());
				paramMap.put("percentage2", beneifitPerson2.getPercentage());
			}
		}
		paramMap.put("currencySymbol", "Ks-");
		paramMap.put("endDateString", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate(), "dd-MMMM_yyyy"));
		paramMap.put("premiumEndDate", Utils.formattedDate(lifePolicy.abstractDatyByPaymentType(), "dd-MMMM_yyyy"));
		JasperPrint jprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SHT_LIFE_POLICY_ENG, new JREmptyDataSource());
		JasperPrint jprint2 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SHT_LIFE_POLICY, new JREmptyDataSource());
		jasperPrintList.add(jprint1);
		jasperPrintList.add(jprint2);
		if (benefitJprint != null) {
			jasperPrintList.add(benefitJprint);
		}

		JasperPrint termAndCjprint = JasperFactory.generateJasperPrint(null, JasperTemplate.STL_TERM_CONDITION, new JREmptyDataSource());
		JasperPrint termAndCjprint2 = JasperFactory.generateJasperPrint(null, JasperTemplate.STL_TERM_CONDITION2, new JREmptyDataSource());
		JasperPrint termAndCjprint3 = JasperFactory.generateJasperPrint(null, JasperTemplate.STL_TERM_CONDITION3, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCjprint);
		Utils.removeBlankPages(termAndCjprint2);
		Utils.removeBlankPages(termAndCjprint3);
		jasperPrintList.add(termAndCjprint);
		jasperPrintList.add(termAndCjprint2);
		jasperPrintList.add(termAndCjprint3);

		return jasperPrintList;
	}

	/** GroupLife Policy Letter */
	public static List<JasperPrint> generateGroupLifePolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		Date startDate = lifePolicy.getActivedPolicyStartDate();
		Date endDate = lifePolicy.getActivedPolicyEndDate();
		String insuredPersonName = "";
		String customerName = lifePolicy.getCustomerName();
		if (lifePolicy.getOrganization() == null) {
			int numberOfInsuredPerson = lifePolicy.getInsuredPersonInfo().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = customerName;
			}
		} else {
			insuredPersonName = lifePolicy.getOrganization().getOwnerName();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("paymentDate", Utils.formattedDate(paymentDTO.getPaymentDate()));
		paramMap.put("startDate", Utils.formattedDate(startDate));
		paramMap.put("insuredName", insuredPersonName);
		paramMap.put("organizationName", lifePolicy.getOrganizationName());
		paramMap.put("insuredAddress", lifePolicy.getCustomerAddress());
		paramMap.put("phno", lifePolicy.getCustomerPhoneNo());
		paramMap.put("numberOfperson", lifePolicy.getInsuredPersonInfo().size());
		paramMap.put("sumInsured", Utils.formattedCurrency(lifePolicy.getTotalSumInsured()));
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("termPremium", Utils.formattedCurrency(lifePolicy.getTotalPremium()));
		paramMap.put("currencySymbol", "Kyats");
		paramMap.put("endDate", Utils.formattedDate(endDate));
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.GP_LIFE_POLICY, new JREmptyDataSource());
		jasperPrintList.add(jprint);
		Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
		insuredPesronMap_2.put("numberOfperson", lifePolicy.getInsuredPersonInfo().size());
		insuredPesronMap_2.put("startDate", Utils.formattedDate(startDate));
		insuredPesronMap_2.put("endDate", Utils.formattedDate(endDate));
		List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
		for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
			perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
		}
		insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
		insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.GP_LIFE_PERSON_ATT, new JREmptyDataSource());
		jasperPrintList.add(jprint2);
		JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.GL_TERM_CONDITION, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJprint);
		jasperPrintList.add(termAndCJprint);
		return jasperPrintList;
	}

	/** PA Policy Letter */
	public static List<JasperPrint> generatePersonalAccidentPolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		boolean isPAUSD = KeyFactorChecker.isPersonalAccidentUSD(insuredPerson.getProduct());
		PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);

		List<PolicyInsuredPersonBeneficiaries> beneficiariesList = insuredPerson.getPolicyInsuredPersonBeneficiariesList();
		double sumInsured = lifePolicy.getTotalSumInsured();
		double premium = paymentDTO.getTotalPremium();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String sumInsuredInword = convertor.getNameWithDecimal(sumInsured);
		String premiumInword = convertor.getNameWithDecimal(premium);
		boolean isMultiplePerson = false;
		int insuredPersonSize = lifePolicy.getInsuredPersonInfo().size();
		if (insuredPersonSize > 1) {
			isMultiplePerson = true;
		}
		String insuredPersonName = "";
		String customerName = lifePolicy.getCustomerName();
		if (lifePolicy.getOrganization() == null) {
			int numberOfInsuredPerson = lifePolicy.getInsuredPersonInfo().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = lifePolicy.getInsuredPersonInfo().get(0).getFullName();
			}
		} else {
			insuredPersonName = lifePolicy.getOrganization().getName();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> paramMap1 = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("beneficiariesList", new JRBeanCollectionDataSource(beneficiariesList));
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("paymentDate", Utils.formattedDate(paymentDTO.getPaymentDate()));
		paramMap.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
		paramMap.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
		paramMap.put("insuredName", insuredPersonName);
		paramMap.put("insuredNrc", insuredPerson.getIdNo());
		paramMap.put("insuredAddress", insuredPerson.getResidentAddress().getFullResidentAddress());
		paramMap.put("occupation", insuredPerson.getOccupationName());
		paramMap.put("phno", insuredPerson.getPhoneForView());
		paramMap.put("sumInsured", Utils.formattedCurrency(sumInsured));
		paramMap.put("currencyCode", insuredPerson.getProduct().getCurrency().getCurrencyCode());
		paramMap.put("premium", Utils.formattedCurrency(premium));
		paramMap.put("sumInsuredInword", sumInsuredInword);
		paramMap.put("premiumInword", premiumInword);
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("benefiName", beneifitPerson.getFullName());
		paramMap.put("benefiNrc", beneifitPerson.getIdNoForView());
		paramMap.put("organizationName", lifePolicy.getOrganization() != null ? lifePolicy.getOrganization().getName() : " ");
		paramMap.put("age", beneifitPerson.getAge());
		paramMap.put("dateOfBirth", beneifitPerson.getDateOfBirthForView());
		paramMap.put("relationship", beneifitPerson.getRelationship().getName());
		paramMap.put("benefAddress", beneifitPerson.getFullAddress());
		paramMap.put("benifiPhon", beneifitPerson.getPhoneForView());
		paramMap1 = new HashMap<>(paramMap);
		paramMap1.remove("beneficiariesList");
		paramMap1.put("beneficiariesList", new JRBeanCollectionDataSource(beneficiariesList));
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		String personAttJasperPath = isPAUSD ? JasperTemplate.PA_POLICY_PERSON_ATT_ENG : JasperTemplate.PA_POLICY_PERSON_ATT;
		if (isPAUSD) {
			JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_POLICY_ENG, new JREmptyDataSource());
			jasperPrintList.add(jprint);
		} else {
			if (beneficiariesList.size() > 1) {
				JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_POLICY_ENG_MULTI_BENE, new JREmptyDataSource());
				JasperPrint jprint1 = JasperFactory.generateJasperPrint(paramMap1, JasperTemplate.PA_POLICY_MULTIPLE_BENE, new JREmptyDataSource());
				jasperPrintList.add(jprint);
				jasperPrintList.add(jprint1);
			} else {
				JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_POLICY_ENG, new JREmptyDataSource());
				JasperPrint jprint1 = JasperFactory.generateJasperPrint(paramMap1, JasperTemplate.PA_POLICY, new JREmptyDataSource());
				jasperPrintList.add(jprint);
				jasperPrintList.add(jprint1);

			}
		}
		if (isMultiplePerson) {
			Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
			Map<String, Object> insuredPesronMap_3 = new HashMap<String, Object>();

			insuredPesronMap_2.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
			insuredPesronMap_2.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
			insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());

			insuredPesronMap_3.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
			insuredPesronMap_3.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
			insuredPesronMap_3.put("policy4Fly", ApplicationSetting.getPolicy4Fly());

			List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
			for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
				perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
			}
			insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
			insuredPesronMap_3.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));

			if (isPAUSD) {
				JasperPrint jprint = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.PA_POLICY_PERSON_ATT_ENG, new JREmptyDataSource());
				jasperPrintList.add(jprint);
			} else {
				JasperPrint jprint = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.PA_POLICY_PERSON_ATT_ENG, new JREmptyDataSource());
				JasperPrint jprint1 = JasperFactory.generateJasperPrint(insuredPesronMap_3, JasperTemplate.PA_POLICY_PERSON_ATT, new JREmptyDataSource());
				jasperPrintList.add(jprint);
				jasperPrintList.add(jprint1);

			}
		}
		if (isPAUSD) {
			JasperPrint termAndCEngJprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_TERM_CONDITION_ENG, new JREmptyDataSource());
			JasperPrint termAndCEngJprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_TERM_CONDITION_ENG1, new JREmptyDataSource());
			Utils.removeBlankPages(termAndCEngJprint);
			Utils.removeBlankPages(termAndCEngJprint1);
			jasperPrintList.add(termAndCEngJprint);
			jasperPrintList.add(termAndCEngJprint1);
		} else {
			JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.PA_TERM_CONDITION, new JREmptyDataSource());
			JasperPrint termAndCJprint1 = JasperFactory.generateJasperPrint(null, JasperTemplate.PA_TERM_CONDITION1, new JREmptyDataSource());
			JasperPrint termAndCEngJprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_TERM_CONDITION_ENG, new JREmptyDataSource());
			JasperPrint termAndCEngJprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PA_TERM_CONDITION_ENG1, new JREmptyDataSource());
			Utils.removeBlankPages(termAndCEngJprint);
			Utils.removeBlankPages(termAndCEngJprint1);
			Utils.removeBlankPages(termAndCJprint);
			Utils.removeBlankPages(termAndCJprint1);
			jasperPrintList.add(termAndCEngJprint);
			jasperPrintList.add(termAndCEngJprint1);
			jasperPrintList.add(termAndCJprint);
			jasperPrintList.add(termAndCJprint1);
		}
		return jasperPrintList;
	}

	/** Farmer Policy Letter */
	public static List<JasperPrint> generateFarmerPolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		String insuredPersonName = "";
		String customerName = lifePolicy.getCustomerName();
		if (lifePolicy.getOrganization() == null) {

			int numberOfInsuredPerson = lifePolicy.getInsuredPersonInfo().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = lifePolicy.getInsuredPersonInfo().get(0).getFullName();
			}
		} else {
			insuredPersonName = lifePolicy.getOrganization().getName();
		}
		PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("paymentDate", paymentDTO.getPaymentDate());
		paramMap.put("startDate", lifePolicy.getActivedPolicyStartDate());
		paramMap.put("insuredName", insuredPersonName);
		paramMap.put("insuredFatherName", insuredPerson.getFatherName());
		paramMap.put("age", insuredPerson.getAge());
		paramMap.put("insuredNrc", insuredPerson.getIdNo());
		paramMap.put("insuredAddress", insuredPerson.getResidentAddress() != null ? insuredPerson.getResidentAddress().getFullResidentAddress() : lifePolicy.getCustomerAddress());
		paramMap.put("dateOfBirth", insuredPerson.getDateOfBirth());
		paramMap.put("phno", insuredPerson.getCustomer() != null ? insuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo() : lifePolicy.getCustomerPhoneNo());
		paramMap.put("sumInsured", Utils.formattedCurrency(lifePolicy.getTotalSumInsured()));
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("benefiName", beneifitPerson.getFullName());
		paramMap.put("benefiNrc", beneifitPerson.getIdNo());
		paramMap.put("benefiAddress", beneifitPerson.getResidentAddress() == null ? " " : beneifitPerson.getResidentAddress().getFullResidentAddress());
		paramMap.put("relationship", beneifitPerson.getRelationship().getName());
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.FARMER_POLICY, new JREmptyDataSource());
		Utils.removeBlankPages(jprint);
		jprintList.add(jprint);
		JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.FM_TERM_CONDITION, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJprint);
		jprintList.add(termAndCJprint);
		return jprintList;
	}

	/** SnakeBike Policy Letter */
	public static List<JasperPrint> generateSnakeBitePolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
		double sumInsured = lifePolicy.getLifeProposal().getSuminsuredPerUnit();
		double premium = lifePolicy.getTotalPremium();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String sumInsuredInword = convertor.getNameWithDecimal(sumInsured);
		String premiumInword = convertor.getNameWithDecimal(premium);
		String insuredPersonName = "";
		String customerName = lifePolicy.getCustomerName();
		int numberOfInsuredPerson = lifePolicy.getInsuredPersonInfo().size();
		if (numberOfInsuredPerson > 1) {
			insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
		} else {
			insuredPersonName = lifePolicy.getInsuredPersonInfo().get(0).getFullName();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("startDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
		paramMap.put("paymentDate", Utils.formattedDate(paymentDTO.getPaymentDate()));
		paramMap.put("endDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
		paramMap.put("insuredName", insuredPerson.getFullName());
		paramMap.put("fatherName", insuredPerson.getFatherName());
		paramMap.put("insuredNrc", insuredPerson.getIdNo());
		paramMap.put("insuredAddress", insuredPerson.getResidentAddress() != null ? insuredPerson.getResidentAddress().getFullResidentAddress() : lifePolicy.getCustomerAddress());
		paramMap.put("dateOfBirth", insuredPerson.getDateOfBirth());
		String phno = insuredPerson.getCustomer() != null ? insuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo() : lifePolicy.getCustomerPhoneNo();
		paramMap.put("phno", phno);
		paramMap.put("sumInsured", Utils.formattedCurrency(sumInsured));
		paramMap.put("premium", Utils.formattedCurrency(premium));
		paramMap.put("sumInsuredInword", sumInsuredInword);
		paramMap.put("premiumInword", premiumInword);
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("benefiName", beneifitPerson.getFullName());
		paramMap.put("benefiNrc", beneifitPerson.getIdNoForView());
		if (beneifitPerson.getRelationship() == null) {
			paramMap.put("relationship", "-");
		} else {
			paramMap.put("relationship", beneifitPerson.getRelationship().getName());
		}
		paramMap.put("benifiPhon", phno);
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SNAKEBITE_POLICY, new JREmptyDataSource());
		jprintList.add(jprint);
		JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.SNB_TERM_CONDITION, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJprint);
		jprintList.add(termAndCJprint);
		return jprintList;
	}

	/** SportMan Policy Letter */
	public static List<JasperPrint> generateSportManPolicyLetter(LifePolicy lifePolicy, PaymentDTO dto) {
		List<PolicyInsuredPerson> personList = lifePolicy.getPolicyInsuredPersonList();
		int insuredPersonSize = personList.size();
		boolean isMultiple = insuredPersonSize > 1 ? true : false;
		String startDate = Utils.formattedDate(lifePolicy.getActivedPolicyStartDate());
		String endDate = Utils.formattedDate(lifePolicy.getActivedPolicyEndDate());
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		PolicyInsuredPersonBeneficiaries beneifitPerson = insuredPerson.getPolicyInsuredPersonBeneficiariesList().get(0);
		double sumInsured = lifePolicy.getTotalSumInsured();
		double premium = lifePolicy.getTotalPremium();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String sumInsuredInword = sumInsured > 0.00 ? convertor.getNameWithDecimal(sumInsured) : "0.00";
		String premiumInword = convertor.getNameWithDecimal(premium);

		String insuredPersonName = "";
		String customerName = lifePolicy.getCustomerName();
		if (lifePolicy.getOrganization() == null) {
			int numberOfInsuredPerson = lifePolicy.getInsuredPersonInfo().size();
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = lifePolicy.getInsuredPersonInfo().get(0).getFullName();
			}
		} else {
			insuredPersonName = lifePolicy.getOrganization().getName();
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("paymentDate", Utils.formattedDate(dto.getPaymentDate()));
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("insuredName", insuredPersonName);
		paramMap.put("insuredNrc", insuredPerson.getIdNoForView());
		paramMap.put("typeOfSport", insuredPerson.getTypesOfSport() != null ? insuredPerson.getTypesOfSport().getName() : null);
		paramMap.put("insuredAddress", insuredPerson.getResidentAddress().getFullResidentAddress());
		String phno = insuredPerson.getCustomer() != null ? insuredPerson.getCustomer().getContentInfo().getPhoneOrMoblieNo() : lifePolicy.getCustomerPhoneNo();
		paramMap.put("phno", phno);
		paramMap.put("sumInsured", Utils.formattedCurrency(sumInsured));
		paramMap.put("premium", Utils.formattedCurrency(premium));
		paramMap.put("sumInsuredInword", sumInsuredInword);
		paramMap.put("premiumInword", premiumInword);
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("benefiName", beneifitPerson.getFullName());
		paramMap.put("benefiNrc", beneifitPerson.getIdNoForView());
		paramMap.put("organizationName", lifePolicy.getOrganization() != null ? lifePolicy.getOrganization().getName() : " ");
		paramMap.put("relationship", beneifitPerson.getRelationship().getName());
		paramMap.put("benifiPhon", beneifitPerson.getPhoneForView());
		paramMap.put("benifiAddress", beneifitPerson.getFullAddress());

		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SPORT_MAN_POLICY, new JREmptyDataSource());
		jprintList.add(jprint);
		if (isMultiple) {
			Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
			insuredPesronMap_2.put("startDate", startDate);
			insuredPesronMap_2.put("endDate", endDate);
			insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
			for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
				perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
			}
			insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
			JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.SPORT_MAN_PERSON_ATT, new JREmptyDataSource());
			jprintList.add(jprint2);
		}
		JasperPrint termAndCJprint = JasperFactory.generateJasperPrint(null, JasperTemplate.SPTM_TERM_CONDITION, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJprint);
		jprintList.add(termAndCJprint);
		return jprintList;
	}

	/** PublicLife Policy Letter */
	public static List<JasperPrint> generatePublicLifePolicyLetter(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
		String agentName = lifePolicy.getAgentName();
		PolicyInsuredPerson insuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
		List<PolicyInsuredPersonBeneficiaries> beneifitPersonList = insuredPerson.getPolicyInsuredPersonBeneficiariesList();
		String policyNo = lifePolicy.getPolicyNo();
		double sumInsured = lifePolicy.getTotalSumInsured();
		String insuredName = insuredPerson.getFullName();
		int nextYrAge = insuredPerson.getAge();
		String insuredAddress = lifePolicy.getCustomerAddress();
		String occupation = insuredPerson.getOccupation() != null ? insuredPerson.getOccupation().getName() : "";
		Date startDate = lifePolicy.getActivedPolicyStartDate();
		String endDate = Utils.formattedDate(lifePolicy.getActivedPolicyEndDate());
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		Map<String, Object> paramMapCover = new HashMap<String, Object>();
		paramMapCover.put("policyNo", policyNo);
		paramMapCover.put("customerName", lifePolicy.getCustomerName());
		paramMapCover.put("totalSumInsured", Utils.formattedCurrency(lifePolicy.getSumInsured()));
		JasperPrint jprintCover = JasperFactory.generateJasperPrint(paramMapCover, JasperTemplate.PUBLIC_LIFE_POLICY_COVER, new JREmptyDataSource());
		jasperPrintList.add(jprintCover);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("agenyName", agentName);
		paramMap.put("agenyLicenseNo", agentLicenseNo);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("paymentDate", Utils.formattedDate(paymentDTO.getPaymentDate()));
		paramMap.put("startDate", Utils.formattedDate(startDate));
		paramMap.put("insuredName", insuredName);
		paramMap.put("nextYrAge", nextYrAge);
		paramMap.put("insuredAddress", insuredAddress);
		paramMap.put("occupation", occupation);
		paramMap.put("period", lifePolicy.getPeriod());
		paramMap.put("sumInsured",Utils.formattedCurrency(sumInsured));
		paramMap.put("paymentType", lifePolicy.getPaymentType().getMonth());
		paramMap.put("termPremium", Utils.formattedCurrency(lifePolicy.getTotalPremium()));
		paramMap.put("invoicePremium", Utils.formattedCurrency(lifePolicy.getTotalTermPremium()));
		paramMap.put("coverageDateString", lifePolicy.getTimeSlotListStr());
		if (lifePolicy.getPaymentType().getMonth() > 1) {
			paramMap.put("isYearly", true);
		} else {
			paramMap.put("isYearly", false);

		}
		JasperPrint benefitJprint = null;
		int size = beneifitPersonList.size();
		PolicyInsuredPersonBeneficiaries beneifitPerson = null;
		if (size > 2) {
			paramMap.put("benefiName", "AS PER ATTACHMENT");
			paramMap.put("relationship", "AS PER ATTACHMENT");
			Map<String, Object> paramMapBenefit = new HashMap<String, Object>();
			paramMapBenefit.put("policyNo", lifePolicy.getPolicyNo());
			paramMapBenefit.put("idNo", insuredPerson.getIdNo());
			paramMapBenefit.put("insuredPersonName", lifePolicy.getCustomerName());
			paramMapBenefit.put("nrc", insuredPerson.getIdNo());
			paramMapBenefit.put("listDataSource", new JRBeanCollectionDataSource(beneifitPersonList));
			paramMapBenefit.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			benefitJprint = JasperFactory.generateJasperPrint(paramMapBenefit, JasperTemplate.BENEFICIARY_ATTACH, new JREmptyDataSource());
		} else {
			beneifitPerson = beneifitPersonList.get(0);
			paramMap.put("benefiName", beneifitPerson.getFullName());
			paramMap.put("relationship", beneifitPerson.getRelationship().getName());
			paramMap.put("percentage", beneifitPerson.getPercentage());
			if (size > 1) {
				PolicyInsuredPersonBeneficiaries beneifitPerson2 = beneifitPersonList.get(1);
				paramMap.put("benefiName2", beneifitPerson2.getFullName());
				paramMap.put("relationship2", beneifitPerson2.getRelationship().getName());
				paramMap.put("percentage2", beneifitPerson2.getPercentage());
			}
		}
		paramMap.put("currencySymbol", "Ks-");
		paramMap.put("endDateString", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate(), "dd-MMMM_yyyy"));
		paramMap.put("premiumEndDate", Utils.formattedDate(lifePolicy.abstractDatyByPaymentType(), "dd-MMMM_yyyy"));
		paramMap.put("endDate", endDate);
		JasperPrint jprintPolicy = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PUBLIC_LIFE_POLICY, new JREmptyDataSource());
		jasperPrintList.add(jprintPolicy);
		if (benefitJprint != null) {
			jasperPrintList.add(benefitJprint);
		}
		Map<String, Object> paramMapSummary = new HashMap<String, Object>();
		paramMapSummary.put("policyNo", policyNo);
		paramMapSummary.put("policyPeriod", lifePolicy.getPeriodOfYears());
		paramMapSummary.put("sumInsured", Utils.formattedCurrency(sumInsured));
		paramMapSummary.put("agentInfo", agentName + " ( " + agentLicenseNo + " )");
		paramMapSummary.put("name", insuredName);
		paramMapSummary.put("occupation", occupation);
		paramMapSummary.put("address", insuredAddress);
		paramMapSummary.put("nextYearAge", nextYrAge);
		paramMapSummary.put("submittedDate", lifePolicy.getLifeProposal().getSubmittedDate());
		paramMapSummary.put("startDate", startDate);
		paramMapSummary.put("premiumRate", null);
		paramMapSummary.put("premium", Utils.formattedCurrency(lifePolicy.getTotalPremium()));
		paramMapSummary.put("paymentType", lifePolicy.getPaymentType().getName());
		paramMapSummary.put("paidDate", lifePolicy.getCoverageDate());
		paramMapSummary.put("endDate", endDate);
		paramMapSummary.put("benificaryName", beneifitPerson.getFullName());
		paramMapSummary.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
		JasperPrint jprintPolicySummary = JasperFactory.generateJasperPrint(paramMapSummary, JasperTemplate.PUBLIC_LIFE_POLICY_SUMMARY, new JREmptyDataSource());
		jasperPrintList.add(jprintPolicySummary);
		JasperPrint termAndCJpring = JasperFactory.generateJasperPrint(null, JasperTemplate.PL_TERM_CONDITION, new JREmptyDataSource());
		JasperPrint termAndCJpring1 = JasperFactory.generateJasperPrint(null, JasperTemplate.PL_TERM_CONDITION1, new JREmptyDataSource());
		JasperPrint termAndCJpring2 = JasperFactory.generateJasperPrint(null, JasperTemplate.PL_TERM_CONDITION2, new JREmptyDataSource());
		Utils.removeBlankPages(termAndCJpring);
		Utils.removeBlankPages(termAndCJpring1);
		Utils.removeBlankPages(termAndCJpring2);
		jasperPrintList.add(termAndCJpring);
		jasperPrintList.add(termAndCJpring1);
		jasperPrintList.add(termAndCJpring2);
		return jasperPrintList;
	}

	public static Date calculateEndDateForStudentLife(Date startDate, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, month);
		return cal.getTime();
	}

	public static List<JasperPrint> generateStudentLifePolicyJasperPrint(LifePolicy lifePolicy, PaymentDTO paymentDTO) {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		try {
			Map<String, Object> coverMap = new HashMap<String, Object>();
			double payAmount = 0.0;
			Date insuredPersonDateOfBirth;
			Date firstBeneDate;
			Date secondBeneDate;
			Date thirdBeneDate;
			Date fourthBeneDate;
			String phone = null;
			String email = null;
			int premiumPeriod = lifePolicy.getPeriodOfYears() - 3;
			DecimalFormat curFormat = new DecimalFormat("#,###");
			PolicyInsuredPerson policyInsuredPerson = lifePolicy.getInsuredPersonInfo().get(0);
			String agentLicenseNo = lifePolicy.getAgentLiscenseNo();
			coverMap.put("policyNo", lifePolicy.getPolicyNo());
			coverMap.put("customerName", lifePolicy.getCustomerName());
			coverMap.put("insuredPersonName", policyInsuredPerson.getFullName());
			coverMap.put("totalsumInsured", curFormat.format(lifePolicy.getSumInsured()));
			coverMap.put("nextPremiumDate", lifePolicy.getTimeSlotListStr());
			InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_INFORM_POLICY_COVER_LETTER);
			JasperReport jreport1 = JasperCompileManager.compileReport(inputStream1);
			JasperPrint jprint1 = JasperFillManager.fillReport(jreport1, coverMap, new JREmptyDataSource());
			jpList.add(jprint1);

			Map<String, Object> paramIssueDetails = new HashMap<String, Object>();
			paramIssueDetails.put("policyNo", lifePolicy.getPolicyNo());
			Agent agent = lifePolicy.getAgent();
			if (agent != null) {
				paramIssueDetails.put("agent", agent.getFullName());
			} else {
				paramIssueDetails.put("agent", "-");
			}

			paramIssueDetails.put("insuredPersonName", policyInsuredPerson.getFullName());
			paramIssueDetails.put("insuredPersonNRC", policyInsuredPerson.getIdNo());
			paramIssueDetails.put("insuredPersonDOB", "(" + policyInsuredPerson.getAge() + " year)" + Utils.formattedDate(policyInsuredPerson.getDateOfBirth()));
			paramIssueDetails.put("InsageForNextYear", policyInsuredPerson.getAge());
			paramIssueDetails.put("insuredPersonAddress",
					policyInsuredPerson.getResidentAddress() != null ? policyInsuredPerson.getResidentAddress().getFullResidentAddress() : "");

			if (Gender.MALE.equals(lifePolicy.getCustomer().getGender())) {
				paramIssueDetails.put("fatherName", lifePolicy.getCustomerName());
				paramIssueDetails.put("motherName", policyInsuredPerson.getParentName());
			} else if (Gender.FEMALE.equals(lifePolicy.getCustomer().getGender())) {
				paramIssueDetails.put("motherName", lifePolicy.getCustomerName());
				paramIssueDetails.put("fatherName", policyInsuredPerson.getParentName());
			}

			paramIssueDetails.put("schoolName", policyInsuredPerson.getSchoolName());
			paramIssueDetails.put("gradeInfo", policyInsuredPerson.getGradeInfoName());

			paramIssueDetails.put("customerName", lifePolicy.getCustomer().getFullName());
			paramIssueDetails.put("customerNRC", lifePolicy.getCustomer().getFullIdNo());
			paramIssueDetails.put("customerDOB", "(" + lifePolicy.getCustomer().getAgeForNextYear() + " year)" + Utils.formattedDate(lifePolicy.getCustomer().getDateOfBirth()));
			paramIssueDetails.put("cusageForNextYear", lifePolicy.getCustomer().getAgeForNextYear());
			paramIssueDetails.put("customerAddress", lifePolicy.getCustomer().getFullAddress());
			paramIssueDetails.put("workAddress", lifePolicy.getCustomer().getFullOfficeAddress());
			if (lifePolicy.getCustomer().getContentInfo() != null) {
				phone = lifePolicy.getCustomer().getContentInfo().getPhoneOrMoblieNo();
				email = lifePolicy.getCustomer().getContentInfo().getEmail();
			}

			paramIssueDetails.put("phone", phone);
			paramIssueDetails.put("email", email != null ? email : "-");
			paramIssueDetails.put("sumInsured", curFormat.format(lifePolicy.getSumInsured()));
			paramIssueDetails.put("periodYears", lifePolicy.getPeriodOfYears());

			paramIssueDetails.put("totalPremium", lifePolicy.getTotalPremium());
			paramIssueDetails.put("termPremium", lifePolicy.getTotalTermPremium());
			paramIssueDetails.put("paymentType", lifePolicy.getPaymentType().getName());
			paramIssueDetails.put("paymentTypeMonth", lifePolicy.getPaymentType().getMonth());
			paramIssueDetails.put("nextPremiumDate", lifePolicy.getTimeSlotListStr());
			paramIssueDetails.put("agentLicenseNo", agentLicenseNo);
			paramIssueDetails.put("activedPolicyStartDate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
			paramIssueDetails.put("activePolicyEndDate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
			Date endDate = DateUtils.proposalEndtDate(lifePolicy.getActivedPolicyEndDate(), -3);
			Date lastPaymentDate = calculateEndDateForStudentLife(endDate, (lifePolicy.getPaymentType().getMonth()) * -1);
			paramIssueDetails.put("lastPaymentDate", Utils.formattedDate(lastPaymentDate));
			paramIssueDetails.put("premiumEndDate", Utils.formattedDate(endDate));
			paramIssueDetails.put("paymentDate", Utils.formattedDate(paymentDTO.getPaymentDate()));
			paramIssueDetails.put("premiunPeriod", premiumPeriod);
			firstBeneDate = DateUtils.addYears(lifePolicy.getActivedPolicyStartDate(), 17 - policyInsuredPerson.getAge());
			secondBeneDate = DateUtils.addYears(firstBeneDate, 1);
			thirdBeneDate = DateUtils.addYears(secondBeneDate, 1);
			fourthBeneDate = DateUtils.addYears(thirdBeneDate, 1);

			int payAmountPercentage = 25;
			payAmount = (lifePolicy.getSumInsured() * payAmountPercentage) / 100;

			int year = DateUtils.getYearFromDate(lifePolicy.getActivedPolicyStartDate());
			int month = DateUtils.getMonthFromDate(lifePolicy.getActivedPolicyStartDate()) + 1;
			int day = DateUtils.getDayFromDate(lifePolicy.getActivedPolicyStartDate());

			paramIssueDetails.put("firstBeneDate", firstBeneDate);
			paramIssueDetails.put("secondBeneDate", secondBeneDate);
			paramIssueDetails.put("thirdBeneDate", thirdBeneDate);
			paramIssueDetails.put("fourthBeneDate", fourthBeneDate);
			paramIssueDetails.put("payAmount", curFormat.format(payAmount));
			paramIssueDetails.put("year", year);
			paramIssueDetails.put("month", month);
			paramIssueDetails.put("day", day);

			InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_INFORM_POLICY_ISSUE_LETTER);
			JasperReport jreport2 = JasperCompileManager.compileReport(inputStream2);
			JasperPrint jprint2 = JasperFillManager.fillReport(jreport2, paramIssueDetails, new JREmptyDataSource());
			InputStream inputStream3 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENT_TERM_CONDITION);
			JasperReport jreport3 = JasperCompileManager.compileReport(inputStream3);
			JasperPrint jprint3 = JasperFillManager.fillReport(jreport3, null, new JREmptyDataSource());
			InputStream inputStream4 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENT_TERM_CONDITION_2);
			JasperReport jreport4 = JasperCompileManager.compileReport(inputStream4);
			JasperPrint jprint4 = JasperFillManager.fillReport(jreport4, null, new JREmptyDataSource());
			jpList.add(jprint2);
			jpList.add(jprint3);
			jpList.add(jprint4);
			return jpList;

		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate document Student life policy issue", e);
		}
	}

	/** Life Bill Collection Noti Letter */
	public static List<JasperPrint> generateLifePolicyNotification(List<LifePolicy> policies, String dirPath, String fileName) {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		try {
			for (LifePolicy lifePolicy : policies) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				int policyPeriodYears = 0;
				int policyPremiumTime = 0;
				Date activePolicyStartDate = lifePolicy.getActivedPolicyStartDate();
				PolicyInsuredPerson insuredPerson = lifePolicy.getPolicyInsuredPersonList().get(0);
				Date activePolicyEndDate = lifePolicy.getActivedPolicyEndDate();
				int policyPeriodMonths = org.ace.insurance.common.Utils.monthsBetween(activePolicyStartDate, activePolicyEndDate, false, false);
				if (policyPeriodMonths > 12) {
					policyPeriodYears = policyPeriodMonths / 12;
					policyPremiumTime = policyPeriodYears - 3;
				}
				int month = lifePolicy.getPaymentType().getMonth();
				int remainingTerm = 0;
				int currentTerm = 0;
				if (month > 0) {

					if (policyPeriodMonths / month != lifePolicy.getLastPaymentTerm()) {
						if (KeyFactorChecker.isStudentLife(insuredPerson.getProduct().getId())) {
							remainingTerm = ((policyPremiumTime * 12) / month) - lifePolicy.getLastPaymentTerm();
							currentTerm = lifePolicy.getLastPaymentTerm() + 1;
						} else {
							remainingTerm = (policyPeriodMonths / month) - lifePolicy.getLastPaymentTerm();
							currentTerm = lifePolicy.getLastPaymentTerm() + 1;
						}
					} else {
						currentTerm = lifePolicy.getLastPaymentTerm() + 1;
					}

					Calendar cal = Calendar.getInstance();
					paramMap.put("insuredPersonName", lifePolicy.getInsuredPersonInfo().get(0).getFullName());
					paramMap.put("insuredPersonAddress", lifePolicy.getCustomerAddress());
					paramMap.put("phoneNo", lifePolicy.getCustomerPhoneNo());
					paramMap.put("customerName", lifePolicy.getCustomerName());
					paramMap.put("salePersonName", lifePolicy.getAgentName());
					paramMap.put("sumInsured", lifePolicy.getTotalSumInsuredString());
					paramMap.put("policyNo", lifePolicy.getPolicyNo());
					paramMap.put("basicTermPremium", lifePolicy.getTotalBasicTermPremiumString());
					paramMap.put("activePolicyStartDate", Utils.formattedDate(activePolicyStartDate));
					paramMap.put("activePolicyEndDate", Utils.formattedDate(activePolicyEndDate));
					paramMap.put("policyLifeTime", policyPeriodYears);
					paramMap.put("month", month);
					paramMap.put("policyPremiumTime", policyPremiumTime);
					paramMap.put("currentTerm", currentTerm);
					paramMap.put("remainingTerm", remainingTerm);
					paramMap.put("currentYear", cal.get(Calendar.YEAR));
					paramMap.put("dueDay", DateUtils.getDayFromDate(lifePolicy.getCoverageDate()));
					paramMap.put("dueMonth", (DateUtils.getMonthFromDate(lifePolicy.getCoverageDate())) + 1);
					paramMap.put("dueYear", DateUtils.getYearFromDate(lifePolicy.getCoverageDate()));
					paramMap.put("dayNoti", DateUtils.getDayWithMyanmarLanguage(new Date()));
					paramMap.put("monthNoti", DateUtils.getMonthWithMyanmarLanguage(new Date()));
					paramMap.put("yearNoti", DateUtils.getYearWithMyanmarLanguage(new Date()));
					paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
					paramMap.put("isShortTerm", KeyFactorChecker.isShortTermEndowment(insuredPerson.getProduct().getId()));
					paramMap.put("isStudentLife", KeyFactorChecker.isStudentLife(insuredPerson.getProduct().getId()));

					InputStream inputStr = Thread.currentThread().getContextClassLoader()
							.getResourceAsStream(KeyFactorChecker.isStudentLife(insuredPerson.getProduct().getId()) ? JasperTemplate.STUDENTLIFE_NOTIFICATION_LETTER
									: JasperTemplate.LIFE_POLICY_NOTIFICATION_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
				}
			}
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifePolicyNotification", e);
		}
		return jpList;
	}

	/** Life Bill Collection Noti Letter */
	public static List<JasperPrint> generateLifePolicyRenewal(List<LifePolicy> policies, String dirPath, String fileName) {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		try {
			for (LifePolicy lifePolicy : policies) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				int policyPeriodYears = 0;
				Date activePolicyStartDate = lifePolicy.getActivedPolicyStartDate();
				PolicyInsuredPerson insuredPerson = lifePolicy.getPolicyInsuredPersonList().get(0);
				Product product = insuredPerson.getProduct();
				Date activePolicyEndDate = lifePolicy.getCoverageDate();
				List<PolicyInsuredPerson> personList = lifePolicy.getPolicyInsuredPersonList();
				int insuredPersonSize = personList.size();
				boolean isMultiple = insuredPersonSize > 1 ? true : false;
				String startDate = Utils.formattedDate(lifePolicy.getActivedPolicyStartDate());
				String endDate = Utils.formattedDate(lifePolicy.getActivedPolicyEndDate());
				int policyPeriodMonths = org.ace.insurance.common.Utils.monthsBetween(activePolicyStartDate, activePolicyEndDate, false, false);
				if (policyPeriodMonths > 12) {
					policyPeriodYears = policyPeriodMonths / 12;
				}
				int month = lifePolicy.getPaymentType().getMonth();
				int remainingTerm = 0;
				int currentTerm = 0;
				if (month > 0) {
					if (policyPeriodMonths / month != lifePolicy.getLastPaymentTerm()) {
						remainingTerm = policyPeriodMonths / month - lifePolicy.getLastPaymentTerm();
						currentTerm = lifePolicy.getLastPaymentTerm() + 1;
					}
				} else {
					currentTerm = lifePolicy.getLastPaymentTerm() + 1;
				}
				double neetopayPremium = 100 + lifePolicy.getPremium();
				DecimalFormat curFormat = new DecimalFormat("#,###");
				Calendar cal = Calendar.getInstance();
				paramMap.put("productName", MyanmarLanguae.getProductNameMyanmarLanguage(product));
				paramMap.put("name", lifePolicy.getCustomerName());
				paramMap.put("policyNo", lifePolicy.getPolicyNo());
				paramMap.put("address", lifePolicy.getCustomerAddress());
				paramMap.put("phone", lifePolicy.getCustomerPhoneNo());
				paramMap.put("agentName", lifePolicy.getAgentNameNLiscenseNo());
				paramMap.put("activePolicyStartdate", Utils.formattedDate(activePolicyStartDate));
				paramMap.put("activePolicyEnddate", Utils.formattedDate(activePolicyEndDate));
				paramMap.put("day", DateUtils.getDayWithMyanmarLanguage(new Date()));
				paramMap.put("month", DateUtils.getMonthWithMyanmarLanguage(new Date()));
				paramMap.put("year", DateUtils.getYearWithMyanmarLanguage(new Date()));
				paramMap.put("suminsured", curFormat.format(lifePolicy.getSumInsured()));
				paramMap.put("premium", curFormat.format(lifePolicy.getPremium()));
				paramMap.put("numberofinsured", lifePolicy.getInsuredPersonInfo().size());
				paramMap.put("neetopay", curFormat.format(neetopayPremium));

				if (KeyFactorChecker.isSportMan(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct())) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_POLICY_SPRENEWAL_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
					if (isMultiple) {
						Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
						insuredPesronMap_2.put("startDate", startDate);
						insuredPesronMap_2.put("endDate", endDate);
						insuredPesronMap_2.put("policyNo", lifePolicy.getPolicyNo());
						insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
						List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
						for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
							perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
						}
						insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
						JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.SPORT_MAN_PERSON_ATTFORRC, new JREmptyDataSource());
						jpList.add(jprint2);
					}
				} else if (KeyFactorChecker.isFarmer(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct())) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_POLICY_RENEWAL_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
				} else if (KeyFactorChecker.isPersonalAccident(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct())) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_POLICY_PERSONALRENEWAL_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
					if (isMultiple) {
						Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
						insuredPesronMap_2.put("startDate", startDate);
						insuredPesronMap_2.put("endDate", endDate);
						insuredPesronMap_2.put("policyNo", lifePolicy.getPolicyNo());
						insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
						List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
						for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
							perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
						}
						insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
						JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.SPORT_MAN_PERSON_ATTFORRC, new JREmptyDataSource());
						jpList.add(jprint2);
					}
				} else if (KeyFactorChecker.isShortTermEndowment(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getId())) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_POLICY_RENEWAL_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
				} else if (KeyFactorChecker.isGroupLife(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct())) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_POLICY_GROUPLIFERENEWAL_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
					if (isMultiple) {
						Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
						insuredPesronMap_2.put("startDate", startDate);
						insuredPesronMap_2.put("endDate", endDate);
						insuredPesronMap_2.put("policyNo", lifePolicy.getPolicyNo());
						insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
						List<PolicyInsuredPersonDTO> perDTOList = new ArrayList<PolicyInsuredPersonDTO>();
						for (PolicyInsuredPerson person : lifePolicy.getInsuredPersonInfo()) {
							perDTOList.add(PolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
						}
						insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
						JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.SPORT_MAN_PERSON_ATTFORRC, new JREmptyDataSource());
						jpList.add(jprint2);
					}
				} else {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_POLICY_RENEWAL_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
				}
				JasperPrint jprint3 = JasperFactory.generateJasperPrint(null , JasperTemplate.BANK_DETAIL_LETTER, new JREmptyDataSource());
				jpList.add(jprint3);
			}
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifePolicyRenewal", e);
		}
		return jpList;
	}

	public static List<JasperPrint> generateMedicalPolicyRenewal(List<MedicalPolicy> policies,MedicalProposal medicalproposal,String dirPath, String fileName) {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		try {
			int basicUnit = 0;
			double basicPremium = 0.0;
			int additionalUnit1 = 0;
			int additionalUnit2 = 0;
			double additionalPremium1 = 0.0;
			double additionalPremium2 = 0.0;
			double addOnPremium = 0.0;
			double premium = 0.0;
			//update code for renewal premium calculation
			List<MedicalProposalInsuredPerson> proposalInsuredPerson=  medicalproposal.getMedicalProposalInsuredPersonList();	

			for (MedicalPolicy medicalPolicy : policies) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				int policyPeriodYears = 0;
				Date activePolicyStartDate = medicalPolicy.getActivedPolicyStartDate();
				Product product = medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct();
				// PolicyInsuredPerson insuredPerson =
				// medicalPolicy.getPolicyInsuredPersonList().get(0);
				Date activePolicyEndDate = medicalPolicy.getActivedPolicyEndDate();
				int policyPeriodMonths = org.ace.insurance.common.Utils.monthsBetween(activePolicyStartDate, activePolicyEndDate, false, false);
				if (policyPeriodMonths > 12) {
					policyPeriodYears = policyPeriodMonths / 12;
				}

				for (MedicalProposalInsuredPerson person : proposalInsuredPerson) {
					
					basicUnit += person.getUnit();							
					basicPremium += person.getPremium();
					premium += person.getPremium() + person.getAddOnPremium();
					// termPremium += person.getTotalTermPremium();
					// oneYearPremium += person.getTotalPremium();
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
				
				//update code for renewal premium calculation(One Year Premium)		
				double totalPremium = basicPremium + additionalPremium1 + additionalPremium2;
				double stampFees = 300 * basicUnit;
				double needtopay = stampFees + totalPremium;
				
				int month = medicalPolicy.getPaymentType().getMonth();
				int remainingTerm = 0;
				int currentTerm = 0;

				if (month > 0) {
					if (policyPeriodMonths / month != medicalPolicy.getLastPaymentTerm()) {
						remainingTerm = policyPeriodMonths / month - medicalPolicy.getLastPaymentTerm();
						currentTerm = medicalPolicy.getLastPaymentTerm() + 1;
					}
				} else {
					currentTerm = medicalPolicy.getLastPaymentTerm() + 1;
				}
				//DecimalFormat curFormat = new DecimalFormat("#,###");

				
				List<MedicalPolicyInsuredPerson> personList = medicalPolicy.getPolicyInsuredPersonList();
				int insuredPersonSize = personList.size();
				boolean isMultiple = insuredPersonSize > 1 ? true : false;
				String startDate = Utils.formattedDate(medicalPolicy.getActivedPolicyStartDate());
				String endDate = Utils.formattedDate(medicalPolicy.getActivedPolicyEndDate());
				MedicalPolicyInsuredPerson medicalPolicyInsuredPerson = medicalPolicy.getPolicyInsuredPersonList().get(0);
				String productId = medicalPolicyInsuredPerson.getProduct().getId();
				Calendar cal = Calendar.getInstance();
				paramMap.put("name", medicalPolicy.getCustomerName());
				paramMap.put("productName", MyanmarLanguae.getProductNameMyanmarLanguage(product));
				paramMap.put("address", medicalPolicy.getCustomerAddress());
				paramMap.put("policyNo", medicalPolicy.getPolicyNo());
				paramMap.put("phone", medicalPolicy.getCustomerPhoneNo());
				paramMap.put("agentName", medicalPolicy.getAgentNameNLiscenseNo().isEmpty() || medicalPolicy.getAgentNameNLiscenseNo() == null ? "NA" : medicalPolicy.getAgentNameNLiscenseNo());
				paramMap.put("activePolicyStartdate", Utils.formattedDate(activePolicyStartDate));
				paramMap.put("activePolicyEnddate", Utils.formattedDate(activePolicyEndDate));
				paramMap.put("day", DateUtils.getDayWithMyanmarLanguage(new Date()));
				paramMap.put("month", DateUtils.getMonthWithMyanmarLanguage(new Date()));
				paramMap.put("year", DateUtils.getYearWithMyanmarLanguage(new Date()));
				paramMap.put("suminsured", (medicalPolicy.getTotalSumInsuredString()));
				//paramMap.put("premium", Utils.formattedCurrency(medicalPolicy.getTotalPremium()));
				paramMap.put("premium", Utils.formattedCurrency(premium));
				paramMap.put("additionalUnit1", additionalUnit1);
				paramMap.put("additionalUnit2", additionalUnit2);
				paramMap.put("additionalPremium1", additionalPremium1);
				paramMap.put("additionalPremium2", additionalPremium2);
				paramMap.put("basicUnit", basicUnit + (basicUnit == 1 ? " Unit" : " Units"));
				paramMap.put("basicPremium", Utils.formattedCurrency(basicPremium));
				paramMap.put("totalPremium", Utils.formattedCurrency(totalPremium));
				paramMap.put("stampFees", Utils.formattedCurrency(stampFees));
				paramMap.put("needtopay", Utils.formattedCurrency(needtopay));

				if (KeyFactorChecker.isCritical(medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct().getId())) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.MEDICAL_POLICY_CRITICALRENEWAL_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
					Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
					insuredPesronMap_2.put("startDate", startDate);
					insuredPesronMap_2.put("endDate", endDate);
					insuredPesronMap_2.put("policyNo", medicalPolicy.getPolicyNo());
					insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
					List<MedicalPolicyInsuredPersonDTO> perDTOList = new ArrayList<MedicalPolicyInsuredPersonDTO>();
					for (MedicalPolicyInsuredPerson person : medicalPolicy.getPolicyInsuredPersonList()) {
						perDTOList.add(MedicalPolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
					}

					insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
					if (KeyFactorChecker.isGroupCriticalIllness(productId)) {
						JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.SPORT_MAN_PERSON_ATTFORRCFORMD, new JREmptyDataSource());
						jpList.add(jprint2);
					}

				} else if (KeyFactorChecker.isHealth(medicalPolicy.getPolicyInsuredPersonList().get(0).getProduct().getId())) {
					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.MEDICAL_POLICY_HEALTHRENEWAL_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);

					Map<String, Object> insuredPesronMap_2 = new HashMap<String, Object>();
					insuredPesronMap_2.put("startDate", startDate);
					insuredPesronMap_2.put("endDate", endDate);
					insuredPesronMap_2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
					List<MedicalPolicyInsuredPersonDTO> perDTOList = new ArrayList<MedicalPolicyInsuredPersonDTO>();
					for (MedicalPolicyInsuredPerson person : medicalPolicy.getPolicyInsuredPersonList()) {
						perDTOList.add(MedicalPolicyInsuredPersonFactory.createPolicyInsuredPersonDTO(person));
					}

					insuredPesronMap_2.put("listDataSource", new JRBeanCollectionDataSource(perDTOList));
					if (KeyFactorChecker.isGroupHealth(productId)) {
						JasperPrint jprint2 = JasperFactory.generateJasperPrint(insuredPesronMap_2, JasperTemplate.SPORT_MAN_PERSON_ATTFORRCFORMD, new JREmptyDataSource());
						jpList.add(jprint2);
					}
				} else {

					InputStream inputStr = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.MICRO_POLICY_RENEWAL_LETTER);
					JasperReport report = JasperCompileManager.compileReport(inputStr);
					JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());
					jpList.add(print);
				}
				JasperPrint jprint3 = JasperFactory.generateJasperPrint(null , JasperTemplate.BANK_DETAIL_LETTER, new JREmptyDataSource());
				jpList.add(jprint3);
			}
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifePolicyRenewal", e);
		}
		return jpList;
	}

	/** Life Bill Collection Invoice Letter */
	public static List<JasperPrint> generateLifeBillPaymentInvoice(List<BillCollectionCashReceiptDTO> cashReceiptDTOs,LifeEndorseChange lifeEndorseChange,PolicyExtraAmount policyExtraAmount) {
		List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
		Product product = cashReceiptDTOs.get(0).getLifePolicy().getPolicyInsuredPersonList().get(0).getProduct();
		Date coverage = cashReceiptDTOs.get(0).getLifePolicy().getCoverageDate();
		String typeOfInsurance = "";
		if (KeyFactorChecker.isPublicLife(product)) {
			typeOfInsurance = "Endowment Life Insurance";
		} else if (KeyFactorChecker.isShortTermEndowment(product.getId())) {
			typeOfInsurance = "ShortTerm Endowment Life Insurance";
		} else if (KeyFactorChecker.isStudentLife(product.getId())) {
			typeOfInsurance = "Student Life Insurance";
		}
		Agent agent;
		int periodMonths;
		String period1;
		String paymentPeriod;
		String premiumEndDate;
		String paymentType;
		String agentRegNo;
		String agentName;
		for (BillCollectionCashReceiptDTO cashReceiptDTO : cashReceiptDTOs) {
			Payment payment = cashReceiptDTO.getPayment();
			LifePolicy policy = cashReceiptDTO.getLifePolicy();
			periodMonths = policy.getPeriodOfYears();
			period1 = periodMonths + " Years";
			paymentPeriod = periodMonths - 3 + " Years";
			premiumEndDate = Utils.formattedDate(DateUtils.premiumEndDate(policy.getActivedPolicyEndDate(), -3));
			paymentType = policy.getPaymentType().getName();
			int term = payment.getFromTerm();
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(policy.getActivedPolicyStartDate());
			calendar1.add(Calendar.MONTH, payment.getPaymentType().getMonth() * (term - 1));
			Date dueDate1 = calendar1.getTime();
			int paymentTimes = payment.getToTerm() - payment.getFromTerm() + 1;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dueDate1);
			calendar.add(Calendar.MONTH, payment.getPaymentType().getMonth() * paymentTimes);
			Date toDate = calendar.getTime();

			agent = policy.getAgent();
			agentRegNo = agent == null ? " " : agent.getLiscenseNo();
			if (agent == null) {
				if (SaleChannelType.WALKIN.getLabel() == policy.getSaleChannelType().getLabel()) {
					agentName = "WALKIN";
				} else {
					agentName = "DIRECTMARKETING";
				}
			} else {
				agentName = agent.getFullName();
			}
			
			String sumInsured="";
			if (lifeEndorseChange != null) {
				
						if (lifeEndorseChange.getLifeEndorseItem().label == "Increas Term") {
						period1=	period1 +"(ENDO From "+lifeEndorseChange.getOldValue()+"Years )";
						} else if (lifeEndorseChange.getLifeEndorseItem().label == "Decrease Term") {
							period1=	period1 +"(ENDO From "+lifeEndorseChange.getOldValue()+"Years )";
						} 

				
				}else {
					period1=period1;
				}
			if (lifeEndorseChange != null) {
				
				if (lifeEndorseChange.getLifeEndorseItem().label == "Decrease Term") {
				period1=	period1 +"(ENDO From "+lifeEndorseChange.getOldValue()+"Years )";
				} else if (lifeEndorseChange.getLifeEndorseItem().label == "Decrease Term") {
					period1=	period1 +"(ENDO From "+lifeEndorseChange.getOldValue()+"Years )";
				} 

		
		}else {
			period1=period1;
		}
				if (lifeEndorseChange != null) {
			
					
						 if (lifeEndorseChange.getLifeEndorseItem().label == "Decrease SI") {
							 sumInsured = Utils.formattedCurrency(policy.getTotalSumInsured()) +"(ENDO From "+lifeEndorseChange.getOldValue()+")";
						} else {
							sumInsured = Utils.formattedCurrency(policy.getTotalSumInsured());
						}
					 
				}else {
					sumInsured = Utils.formattedCurrency(policy.getTotalSumInsured());
				}

				
				if (lifeEndorseChange != null) {
				
						 if (lifeEndorseChange.getLifeEndorseItem().label == "PaymentTypeChange") {
							paymentType = policy.getPaymentType().getName() +"(ENDO From "+lifeEndorseChange.getOldValue()+")";
						} else {
							paymentType = policy.getPaymentType().getName();
						}
				}else {
					paymentType = policy.getPaymentType().getName();
					}
			
			
			Map<String, Object> paramMap = new HashMap<>();
			boolean isEndorse = ProposalType.ENDORSEMENT.equals(policy.getLifeProposal().getProposalType());
			paramMap.put("isEndorse", isEndorse);
			paramMap.put("invoiceNo", payment.getInvoiceNo());
			paramMap.put("section", typeOfInsurance);
			
			
			if(lifeEndorseChange!=null) {
				paramMap.put("endorsementType",lifeEndorseChange.getLifeEndorseItem().getLabel());
				if(policyExtraAmount!=null) {
					double needPremium=policy.getTotalBasicTermPremium()-policyExtraAmount.getExtraAmount();
					paramMap.put("extraPremium", Utils.getCurrencyFormatString(policyExtraAmount.getExtraAmount()));
					paramMap.put("needPremium", Utils.getCurrencyFormatString(needPremium));
					paramMap.put("totalPremium", Utils.formattedCurrency(needPremium));
				}else {
					paramMap.put("extraPremium", "0.00");
					paramMap.put("needPremium", Utils.formattedCurrency(payment.getTotalPremium()));
					paramMap.put("totalPremium", Utils.formattedCurrency(payment.getTotalPremium()));
				}
			}
			
			paramMap.put("duenum", payment.getPaymentTermStrings());
			paramMap.put("period1", period1);
			paramMap.put("period", paymentType);
			paramMap.put("fromDate", Utils.formattedDate(dueDate1));
			paramMap.put("insurdName", policy.getPolicyInsuredPersonList().get(0).getFullName());
			paramMap.put("policyHolderName", policy.getCustomerName());
			paramMap.put("address", policy.getCustomerAddress());
			paramMap.put("agentRegNo", agentRegNo);
			paramMap.put("agentName", agentName);
			paramMap.put("policyType", "New Policy");
			paramMap.put("premium", Utils.formattedCurrency(policy.getTotalPremium()));
			paramMap.put("termPremium", Utils.formattedCurrency(payment.getTotalPremium()));
			paramMap.put("adjPlus", 0.00);
			paramMap.put("adjMinus", 0.00);
			paramMap.put("discount", payment.getSpecialDiscount());
			paramMap.put("sumInsured",sumInsured);
			paramMap.put("stampFee", payment.getStampFees());
			paramMap.put("startDate", Utils.formattedDate(policy.getActivedPolicyStartDate()));
			paramMap.put("proposalEndate", Utils.formattedDate(policy.getActivedPolicyEndDate()));
			paramMap.put("pPrd", paymentPeriod);
			paramMap.put("premiumEndDate", premiumEndDate);
			
			paramMap.put("invoiceDate", Utils.formatDateTimeWithAMOrPM(payment.getConfirmDate()));
			paramMap.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			if(policy.getPolicyNo()==null) {
			paramMap.put("policyNo",null);
			paramMap.put("proposalNo", policy.getLifeProposal().getProposalNo());
			}else {
				paramMap.put("policyNo",policy.getPolicyNo());
				paramMap.put("proposalNo",policy.getLifeProposal().getProposalNo());
			}
			
			
			if((lifeEndorseChange != null) && (lifeEndorseChange.getLifeEndorseItem().label == "PaymentTypeChange"||lifeEndorseChange.getLifeEndorseItem().label == "Decrease SI"||lifeEndorseChange.getLifeEndorseItem().label == "Increas Term"||lifeEndorseChange.getLifeEndorseItem().label == "Decrease Term") ) {
				JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, KeyFactorChecker.isStudentLife(product.getId()) ? JasperTemplate.Student_Life_ENDOPAYMENT_INVOICE : JasperTemplate.Life_ENDOPAYMENT_INVOICE,
						new JREmptyDataSource());
				jasperPrintList.add(jprint);
			}
			else {
				paramMap.put("totalPremium", Utils.formattedCurrency(payment.getTotalPremium()));
				JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap,
						KeyFactorChecker.isStudentLife(product.getId()) ? JasperTemplate.Student_Life_PAYMENT_INVOICE : JasperTemplate.Life_BILLCOllECTIONPAYMENT_INVOICE, new JREmptyDataSource());
				jasperPrintList.add(jprint);
			}
			
		}
		return jasperPrintList;
	}

	/** Life Bill Collection Receipt Letter */
	public static List<JasperPrint> generateLifeBillCashReceipt(LifePolicy lifePolicy, Payment payment,List<LifeEndorseChange> lifeEndorseChange,PolicyExtraAmount policyExtraAmount) {
		List<JasperPrint> printList = new ArrayList<>();
		String receiptNo = payment.getReceiptNo();
		String insuredPersonName="";
		Date paymentDate = payment.getPaymentDate();
		String customerName = lifePolicy.getCustomerName();
		String address = lifePolicy.getCustomerAddress();
		if (lifePolicy.getOrganization() == null) {
			int numberOfInsuredPerson = lifePolicy.getPolicyInsuredPersonList().size();	
			if (numberOfInsuredPerson > 1) {
				insuredPersonName = customerName + "  +" + (numberOfInsuredPerson - 1);
			} else {
				insuredPersonName = lifePolicy.getPolicyInsuredPersonList().get(0).getFullName();
			}
		} else {
			insuredPersonName = lifePolicy.getOrganization().getName();
		}
		String policyNo = lifePolicy.getPolicyNo();
		String agentInfo = lifePolicy.getAgentNameNLiscenseNo();
		String proposalType = null;
		double discount = payment.getSpecialDiscount();
		String paymentType = lifePolicy.getPaymentType().getName();
		String period = lifePolicy.getPeriodOfInsurance() + " Years";
		String startDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyStartDate());
		String endDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyEndDate());
		String totalSI = "";
		double premium = payment.getTotalPremium();
		double stampFee = payment.getStampFees();
		double totalPremium = payment.getTotalAmount();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String premiumInword = convertor.getNameWithDecimal(totalPremium);
		int term = payment.getFromTerm();
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(lifePolicy.getActivedPolicyStartDate());
		calendar1.add(Calendar.MONTH, payment.getPaymentType().getMonth() * (term - 1));
		Date dueDate = calendar1.getTime();
		int paymentTimes = payment.getToTerm() - payment.getFromTerm() + 1;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dueDate);
		calendar.add(Calendar.MONTH, payment.getPaymentType().getMonth() * paymentTimes);
		Date toDate = calendar.getTime();
		boolean isPublicLife = false;
		boolean isShortterm = false;
		boolean isStudentLife = false;
		String adjustAmount="";
		boolean isEndorse = ProposalType.ENDORSEMENT.equals(lifePolicy.getLifeProposal().getProposalType());
		if(policyExtraAmount!=null) {
			 adjustAmount=Utils.getCurrencyFormatString(policyExtraAmount.getExtraAmount());
			 totalPremium = payment.getTotalAmount()-policyExtraAmount.getExtraAmount();
		}else
		{
			 adjustAmount="0.00";
			 totalPremium = payment.getTotalAmount();
		}

		String policyno = "";
		if (isEndorse) {
			policyNo = lifePolicy.getPolicyNo();
			policyno = lifePolicy.getEndorsementNo();
		} else {
			policyNo = lifePolicy.getPolicyNo();
		}
		
		if (lifeEndorseChange!=null && !lifeEndorseChange.isEmpty()) {
		for (LifeEndorseChange endChange : lifeEndorseChange) {
				if (endChange.getLifeEndorseItem().label == "Increas Term") {
					startDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyStartDate());
					endDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyEndDate()) +"(ENDO From "+endChange.getOldValue()+"Years)";
				} else if (endChange.getLifeEndorseItem().label == "Decrease Term") {
					startDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyStartDate());
					endDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyEndDate()) + "(ENDO From "+endChange.getOldValue()+" Years)";
				} 

			}
		}
		else {
			startDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyStartDate());
			endDate = DateUtils.formatDateToString(lifePolicy.getActivedPolicyEndDate());
		}
		if (lifeEndorseChange!=null && !lifeEndorseChange.isEmpty()) {
		for (LifeEndorseChange endChange : lifeEndorseChange) {
			
				 if (endChange.getLifeEndorseItem().label == "Decrease SI") {
					totalSI = Utils.formattedCurrency(lifePolicy.getTotalSumInsured()) +"(ENDO From  "+endChange.getOldValue()+")";
				} else {
					totalSI = Utils.formattedCurrency(lifePolicy.getTotalSumInsured());
				}
			} 
		}else {
			
			totalSI = Utils.formattedCurrency(lifePolicy.getTotalSumInsured());
		}

		
		if (lifeEndorseChange!=null && !lifeEndorseChange.isEmpty()) {
		for (LifeEndorseChange endChange : lifeEndorseChange) {
				 if (endChange.getLifeEndorseItem().label == "PaymentTypeChange") {
					paymentType = lifePolicy.getPaymentType().getName() +"(ENDO From  "+endChange.getOldValue()+")";
				} else {
					paymentType = lifePolicy.getPaymentType().getName();
				}
			
		}
		}else {
			paymentType = lifePolicy.getPaymentType().getName();
			}
		Product product = lifePolicy.getPolicyInsuredPersonList().get(0).getProduct();
		if (KeyFactorChecker.isPublicLife(product))
			isPublicLife = true;
		else if (KeyFactorChecker.isShortTermEndowment(product.getId()))
			isShortterm = true;
		else if (KeyFactorChecker.isStudentLife(product.getId()))
			isStudentLife = true;
		String prmPeriod = lifePolicy.getPeriodOfInsurance() - 3 + " Years ";
		String premiumEndDate = DateUtils.formatDateToString(DateUtils.premiumEndDate(lifePolicy.getActivedPolicyEndDate(), -3));
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("isPublicLife", isPublicLife);
		paramMap.put("isStudentLife", isStudentLife);
		paramMap.put("isEndorse", isEndorse);
		paramMap.put("isShortterm", isShortterm);
		paramMap.put("paymentType", paymentType);
		paramMap.put("receiptNo", receiptNo);
		paramMap.put("receiptDate", Utils.formatDateTimeWithAMOrPM(paymentDate));
		paramMap.put("insuredPersonName", insuredPersonName);
		paramMap.put("customerName", customerName);
		paramMap.put("address", address);
		paramMap.put("proposalNo", policyNo);
		paramMap.put("policyNo", policyno);
		paramMap.put("agent", agentInfo);
		paramMap.put("proposalType", proposalType);
		paramMap.put("policyPeriod", period);
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("totalSi", totalSI);
		paramMap.put("premium", premium);
		paramMap.put("dueDate",DateUtils.getDateFormatString(dueDate));
		paramMap.put("adjustAmount",adjustAmount);
		paramMap.put("discount", discount);
		paramMap.put("stampFee", stampFee);
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("premiumInWord", premiumInword);
		paramMap.put("billCollectTerm",payment.getPaymentTermStrings());
		paramMap.put("receiptBG", ApplicationSetting.getReceipt4Fly());
		paramMap.put("prmPeriod", prmPeriod);
		paramMap.put("premiumEndDate", premiumEndDate);

		for (int i = 0; i <= 1; i++) {
			paramMap.put("isCopy", i == 1 ? true : false);
			JasperPrint jprint = new JasperPrint();
			if (isPublicLife || isShortterm ) {
				if(isEndorse) {
					if((lifeEndorseChange!=null && !lifeEndorseChange.isEmpty()) && (lifeEndorseChange.get(0).getLifeEndorseItem().label == "PaymentTypeChange"||lifeEndorseChange.get(0).getLifeEndorseItem().label == "Decrease SI"||lifeEndorseChange.get(0).getLifeEndorseItem().label == "Increas Term"||lifeEndorseChange.get(0).getLifeEndorseItem().label == "Decrease Term")) {
					jprint=JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDORECEIPT_FOR_PUBLICANDSHORT, new JREmptyDataSource());
					}else {
						jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_PUBLICANDSHORT, new JREmptyDataSource());
					}
				}else {
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_PUBLICANDSHORT, new JREmptyDataSource());
				}
				printList.add(jprint);
			}else {
				jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_RECEIPT_FOR_STUDENTLIFE, new JREmptyDataSource());
				printList.add(jprint);
			}
			
			
		}
		return printList;
	}

	public static List<JasperPrint> generateDisabilityLifeClaimLetter(LifeClaimProposal lifeClaimProposal, PaymentDTO paymentDTO) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("productName", MyanmarLanguae.getProductNameMyanmarLanguage(lifeClaimProposal.getProduct()));
		paramMap.put("insuPerson", lifeClaimProposal.getClaimPerson().getJSPName());
		paramMap.put("occuranceDate", Utils.formattedDate(lifeClaimProposal.getReportDate()));
		paramMap.put("claimProposalNo", lifeClaimProposal.getClaimProposalNo());
		paramMap.put("claimAmount", Utils.getCurrencyFormatString(lifeClaimProposal.getTotalClaimAmont()));
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String currrencyword = convertor.getNameWithDecimal(lifeClaimProposal.getTotalClaimAmont());
		paramMap.put("totalClaimAmount", currrencyword);
		paramMap.put("policyNo", lifeClaimProposal.getLifePolicy().getPolicyNo());
		paramMap.put("myanmarAmount", currrencyword);
		paramMap.put("receiptNo", getReceiptNoOrBankAccount(paymentDTO));
		paramMap.put("paymentDate", Utils.formattedDate(paymentDTO.getPaymentDate()));
		paramMap.put("currentDate", Utils.formattedDate(new Date()));
		paramMap.put("policyStartDate", Utils.formattedDate(lifeClaimProposal.getLifePolicy().getActivedPolicyStartDate()));
		paramMap.put("policyEndDate", Utils.formattedDate(lifeClaimProposal.getLifePolicy().getActivedPolicyEndDate()));
		paramMap.put("customerName", lifeClaimProposal.getLifePolicy().getCustomerName());
		paramMap.put("insuredPersonName", lifeClaimProposal.getClaimPerson().getFullName());
		paramMap.put("suminsured", Utils.formattedCurrency(lifeClaimProposal.getLifePolicy().getSumInsured()));
		paramMap.put("day", DateUtils.getDayFromDate(new Date()));
		// TODO FIXME PSH, change return value to value+1 in getMonthFromDate
		// method and update the related method
		paramMap.put("month", DateUtils.getMonthFromDate(new Date()) + 1);
		paramMap.put("year", DateUtils.getYearFromDate(new Date()));
		paramMap.put("date", DateUtils.getDateFormatString(new Date()));
		paramMap.put("occurancedDate", DateUtils.getDateFormatString(lifeClaimProposal.getOccuranceDate()));
		paramMap.put("occurancedPlace", lifeClaimProposal.getOccurancePlace());
		paramMap.put("claimNo", lifeClaimProposal.getClaimProposalNo());
		List<JasperPrint> printList = new ArrayList<>();
		JasperPrint jprint = new JasperPrint();
		JasperPrint jprint1 = new JasperPrint();
		if (KeyFactorChecker.isSportMan(lifeClaimProposal.getProduct())) {
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SPORTMAN_LIFE_CLAIM_ISSUE_LETTER_1, new JREmptyDataSource());
			jprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.SPORTMAN_LIFE_CLAIM_ISSUE_LETTER_2, new JREmptyDataSource());
			printList.add(jprint);
			printList.add(jprint1);
		} else if (KeyFactorChecker.isGroupLife(lifeClaimProposal.getProduct())) {
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.GROUP_LIFE_CLAIM_ISSUE_LETTER_1, new JREmptyDataSource());
			jprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.GROUP_LIFE_CLAIM_ISSUE_LETTER_2, new JREmptyDataSource());
			printList.add(jprint);
			printList.add(jprint1);
		} else {
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.DISABILITY_LIFE_CLAIM_ISSUE_LETTER, new JREmptyDataSource());
			printList.add(jprint);
		}
		return printList;
	}

	private static String getReceiptNoOrBankAccount(PaymentDTO paymentDTO) {
		switch (paymentDTO.getPaymentChannel()) {
			case CASHED:
				return paymentDTO.getReceiptNo();
			case TRANSFER:
				return paymentDTO.getPoNo();
			case CHEQUE:
				return paymentDTO.getChequeNo();
			default:
				return paymentDTO.getReceiptNo();
		}

	}

	public static List<JasperPrint> generateDisabilityLifeClaimInformLetter(LifeClaimProposal lifeClaimProposal) {
		StringBuilder builders = new StringBuilder();
		Product product = lifeClaimProposal.getProduct();
		boolean isDisability = lifeClaimProposal.getLifePolicyClaim() instanceof DisabilityLifeClaim;
		boolean isDeath = lifeClaimProposal.getLifePolicyClaim() instanceof LifeDeathClaim;
		String printTitle = "";
		String disa = "";
		String det = "";
		String res = "";
		if (KeyFactorChecker.isPublicLife(product)) {
			printTitle = "LIFEENDORSEMENT_ISSUELETTER_LABEL_62";
		} else if (KeyFactorChecker.isShortTermEndowment(product.getId())) {
			printTitle = "LIFEENDORSEMENT_ISSUELETTER_LABEL_58";
		} else if (KeyFactorChecker.isGroupLife(lifeClaimProposal.getProduct())) {
			printTitle = "LIFEENDORSEMENT_ISSUELETTER_LABEL_60";
		} else if (KeyFactorChecker.isFarmer(lifeClaimProposal.getProduct())) {
			printTitle = "LIFEENDORSEMENT_ISSUELETTER_LABEL_59";
		} else if (KeyFactorChecker.isSportMan(lifeClaimProposal.getProduct())) {
			printTitle = "LIFEENDORSEMENT_ISSUELETTER_LABEL_57";
		} else if (KeyFactorChecker.isStudentLife(product.getId())) {
			printTitle = "LIFEENDORSEMENT_ISSUELETTER_LABEL_63";
		} else if (KeyFactorChecker.isPersonalAccident(product)) {
			printTitle = "LIFEENDORSEMENT_ISSUELETTER_LABEL_64";
		} else if (KeyFactorChecker.isSnakeBite(product.getId())) {
			printTitle = "LIFEENDORSEMENT_ISSUELETTER_LABEL_61";
		}
		List<JasperPrint> printList = new ArrayList<>();
		if (isDisability || isDeath) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (isDisability) {
				StringBuilder builder = new StringBuilder();
				paramMap.put("causeOfDis", ((DisabilityLifeClaim) lifeClaimProposal.getLifePolicyClaim()).getDisabilityLifeClaimList().get(0).getCauseofdisability());
				for (DisabilityLifeClaimPartLink link : ((DisabilityLifeClaim) lifeClaimProposal.getLifePolicyClaim()).getDisabilityLifeClaimList()) {
					builder.append(link.getCauseofPropose());

				}
				disa = MyanmarLanguae.getMyanmarLanguaeString("DISA_LABEL") + builder.toString();
				paramMap.put("causeOfInform", disa);
			} else if (isDeath) {
				paramMap.put("causeOfDis", ((LifeDeathClaim) lifeClaimProposal.getLifePolicyClaim()).getCauseofdeath());
				String causeOfInform = ((LifeDeathClaim) lifeClaimProposal.getLifePolicyClaim()).getCauseofPropose();
				String death = disa = MyanmarLanguae.getMyanmarLanguaeString("DET_LABEL").concat(causeOfInform);

				paramMap.put("causeOfInform", death);
			}

			paramMap.put("reason", ((LifePolicyClaim) lifeClaimProposal.getLifePolicyClaim()).getApprovedReason());

			paramMap.put("todayDate", DateUtils.formattedDate(lifeClaimProposal.getReportDate()));
			paramMap.put("policyNo", lifeClaimProposal.getLifePolicy().getPolicyNo());
			paramMap.put("productDesc", MyanmarLanguae.getMyanmarLanguaeString(printTitle));
			paramMap.put("SI", lifeClaimProposal.getClaimPerson().getSumInsured());
			paramMap.put("premium", lifeClaimProposal.getClaimPerson().getBasicTermPremium());
			paramMap.put("period", "( " + Utils.formattedDate(lifeClaimProposal.getClaimPerson().getLifePolicy().getActivedPolicyStartDate()) + " to "
					+ Utils.formattedDate(lifeClaimProposal.getClaimPerson().getLifePolicy().getActivedPolicyEndDate()) + " )");
			JasperPrint jprint = new JasperPrint();
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.DISABILITY_LIFE_CLAIM_INFORM_LETTER, new JREmptyDataSource());
			printList.add(jprint);
		}
		return printList;
	}

	public static List<JasperPrint> generateLifClaimPaymentSlipLetter(LifeClaimProposal lifeClaimProposal, int claimCount, ClaimAcceptedInfo acceptedInfo) {
		String claimPaymentStatus = resolveCountToLetter(claimCount);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("policyNo", lifeClaimProposal.getLifePolicy().getPolicyNo());
		paramMap.put("CNNo", lifeClaimProposal.getClaimProposalNo());
		paramMap.put("claimPaymentStatus", claimPaymentStatus);
		double totalClaimAmount = lifeClaimProposal.getTotalClaimAmont() + acceptedInfo.getMedicalfees();
		double kyat = Math.floor(totalClaimAmount);
		double claimAmountpyar = (totalClaimAmount - kyat) * 100;

		double medicalFeeKyat = Math.floor(acceptedInfo.getMedicalfees());
		double medicalFeepyar = (acceptedInfo.getMedicalfees() - medicalFeeKyat) * 100;
		paramMap.put("totalClaimAmount", totalClaimAmount);
		paramMap.put("claimAmountKyat", kyat);
		paramMap.put("pyas", claimAmountpyar);
		paramMap.put("medicalFeeKyat", medicalFeeKyat);
		paramMap.put("medicalFeepyar", medicalFeepyar);
		paramMap.put("customerName", lifeClaimProposal.getClaimPerson().getFullName());
		paramMap.put("dollarAmount", "");
		String a = lifeClaimProposal.getPyaee();
		paramMap.put("pyaee", lifeClaimProposal.getPyaee());
		if (null != lifeClaimProposal.getHospital()) {
			paramMap.put("hospitalName", lifeClaimProposal.getHospital().getName());
		}
		paramMap.put("claimAmount", lifeClaimProposal.getTotalClaimAmont());
		paramMap.put("date", Utils.formattedDate(new Date()));
		List<JasperPrint> printList = new ArrayList<>();
		JasperPrint jprint1 = new JasperPrint();
		JasperPrint jprint2 = new JasperPrint();
		Map<String, Object> paramMap2 = new HashMap<>();

		jprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_CLAIM_PAYMENT_LETTER_1, new JREmptyDataSource());
		jprint2 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_CLAIM_PAYMENT_LETTER_2, new JREmptyDataSource());
		printList.add(jprint1);
		printList.add(jprint2);
		return printList;
	}

	private static String resolveCountToLetter(int count) {
		switch (count) {
			case 1:

				return "1st Claim Payment";
			case 2:
				return "2nd Claims Payment";
			case 3:
				return "3rd Claims Payment";
			case 4:
				return "4th Claims Payment";
			case 5:
				return "5th Claims Payment";
			default:
				return " 1st Claim Payment";
		}
	}

	public static List<JasperPrint> generateClaimPaymentLetter(LifeClaimProposal lifeClaimProposal, Payment payment, int claimCount) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String claimPaymentStatus = resolveCountToLetter(claimCount);
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		paramMap.put("myanmarAmount", convertor.getNameWithDecimal(lifeClaimProposal.getTotalClaimAmont()));
		paramMap.put("vendorName", lifeClaimProposal.getPyaee());
		if (payment.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
			paramMap.put("paymentChannel", payment.getBank().getName());
			paramMap.put("remark", payment.getPoNo());
		} else if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
			paramMap.put("paymentChannel", payment.getBank().getName());
			paramMap.put("remark", payment.getChequeNo());
		} else {
			paramMap.put("paymentChannel", payment.getPaymentChannel().getLabel());
			paramMap.put("remark", " ");
		}
		paramMap.put("voucherNo", payment.getReceiptNo());
		paramMap.put("amount", lifeClaimProposal.getTotalClaimAmont());
		paramMap.put("insuredPersonName", lifeClaimProposal.getClaimPerson().getFullName());
		paramMap.put("date", Utils.formattedDate(payment.getPaymentDate()));
		paramMap.put("productName", lifeClaimProposal.getProduct().getName());
		paramMap.put("claimNo", lifeClaimProposal.getClaimProposalNo());
		paramMap.put("claimPaymentStatus", claimPaymentStatus);
		paramMap.put("CUR", payment.getCurrency().getCurrencyCode());
		List<JasperPrint> printList = new ArrayList<>();
		JasperPrint jprint1 = new JasperPrint();
		jprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_CLAIM_PAYMENT_LETTER, new JREmptyDataSource());

		printList.add(jprint1);

		return printList;
	}

	public static List<JasperPrint> generateClaimMedicalFeesPaymentLetter(MEDFEES001 dto, String pyaee, List<PolicyInsuredPerson> claimPersonList) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		paramMap.put("myanmarAmount", convertor.getNameWithDecimal(dto.getMedicalFees()));
		paramMap.put("vendorName", pyaee);
		paramMap.put("paymentChannel", dto.getPaymentChannel().getLabel());
		paramMap.put("voucherNo", dto.getInvoiceNo());
		paramMap.put("remark", " ");
		paramMap.put("amount", dto.getMedicalFees());
		paramMap.put("insuredPersonName", dto.getInsuredPersonName());
		paramMap.put("claimperson", claimPersonList.get(0).getFullName());
		paramMap.put("date", Utils.formattedDate(new Date()));
		paramMap.put("productName", claimPersonList.get(0).getProduct().getProductContent().getName());
		paramMap.put("claimNo", dto.getClaimNo());

		paramMap.put("dataSource", new JRBeanCollectionDataSource(claimPersonList));
		List<JasperPrint> printList = new ArrayList<>();
		JasperPrint jprint1 = new JasperPrint();
		jprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_CLAIM_MEDICALFEES_PAYMENT_LETTER, new JREmptyDataSource());

		printList.add(jprint1);

		return printList;
	}

	public static List<JasperPrint> generateClaimMedicalFeesSanction(List<LifeClaimMedicalFeeDTO> dtoList, Hospital hospital) {
		List<JasperPrint> jpringList = new ArrayList<JasperPrint>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> paramMap1 = new HashMap<String, Object>();

		double totalMedicalFees = 0.00;
		String sanctionNo = "";
		for (LifeClaimMedicalFeeDTO sanction : dtoList) {
			totalMedicalFees = totalMedicalFees + sanction.getMedicalFee();
			sanctionNo = sanction.getSanctionNo();

		}

		Date minDate = dtoList.stream().map(LifeClaimMedicalFeeDTO::getMedicalFeesStartDate).min(Date::compareTo).get();
		Date maxDate = dtoList.stream().map(LifeClaimMedicalFeeDTO::getMedicalFeesStartDate).max(Date::compareTo).get();

		String startDate = Utils.formattedDate(minDate);
		String endDate = Utils.formattedDate(maxDate);
		paramMap.put("todayDate", Utils.formattedDate(new Date()));
		paramMap.put("hospitalName", hospital.getName());
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("count", dtoList.size());
		paramMap.put("totalMedicalFees", Utils.formattedCurrency(totalMedicalFees));
		paramMap1.put("startDate", startDate);
		paramMap1.put("hospitalName", hospital.getName());
		paramMap1.put("todayDate", Utils.formattedDate(new Date()));
		paramMap1.put("endDate", endDate);
		paramMap1.put("sanctionNo", sanctionNo);
		Date inform = new Date();
		inform = dtoList.get(0).getInformDate();
		String infromdate = Utils.getYearMonthDateFormatString(inform);
		paramMap1.put("informDate", infromdate);

		paramMap1.put("dtoList", new JRBeanCollectionDataSource(dtoList));

		// JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap,
		// JasperTemplate.MEDICAL_FEES_SANCTION, new JREmptyDataSource());
		// jpringList.add(jprint);

		JasperPrint jprint2 = JasperFactory.generateJasperPrint(paramMap1, JasperTemplate.MEDICAL_FEES_SANCTION_1, new JREmptyDataSource());
		jpringList.add(jprint2);

		return jpringList;
	}

	public static List<JasperPrint> generateNonFinancialReport(LifeProposal nonDto, List<LifeEndorseChange> change, LifeEndorseInfo dto,List<LifeEndorseBeneficiary> lifeEndorseBeneficiary) throws SystemException {
		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
		Map<String, Object> params = new HashMap<String, Object>();
		List<LifeEndorseChange> endorsechangecopy = new ArrayList<>();
		endorsechangecopy.addAll(change);
		List<LifeEndorseChange> endorsechangecopy1 = new ArrayList<>();
		endorsechangecopy1.addAll(change);
		List<LifeEndorseChange> endorsechangecopy2 = new ArrayList<>();
		endorsechangecopy2.addAll(change);
		Product product = nonDto.getProposalInsuredPersonList().get(0).getProduct();
		boolean isPublicLife;
		boolean isShortermEndownment;
		boolean isPersonalAccident;
		boolean isGroupLife;
		isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
		isPublicLife = KeyFactorChecker.isPublicLife(product);
		isGroupLife = KeyFactorChecker.isGroupLife(product);
		isPersonalAccident = KeyFactorChecker.isPersonalAccident(product);
		
		
		String productName = "";
		String productDescription = "";
		if (isPublicLife) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_LIFE_LABEL") + "အသက်အာမခံ";
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else if (isShortermEndownment) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("SHORT_TERM_LIFE_LABEL") + "အသက်အာမခံ";
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else if (isPersonalAccident) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("PERSONAL_ACCIDENT_LABEL_1");
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else {
			productName = MyanmarLanguae.getMyanmarLanguaeString("GROUP_LIFE_LABEL") + "အသက်အာမခံ";
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("GROUP_ACCEPTED_LABEL");
		}

		
		for (LifeEndorseChange endChange : change) {
			if (endChange.getLifeEndorseItem().getLabel() == "Name Change") {
				//String benes = endChange.getLifeEndorseBeneficiaryInfoList().get(0).getFullName();
				params.put("dayNoti", DateUtils.getDayWithMyanmarLanguage(dto.getEndorsementDate()));
				params.put("monthNoti", DateUtils.getMonthWithMyanmarLanguage(dto.getEndorsementDate()));
				params.put("yearNoti", DateUtils.getYearWithMyanmarLanguage(dto.getEndorsementDate()));
				params.put("productName", (productName));
				params.put("endorseNo", nonDto.getProposalNo());
				params.put("policyNo", dto.getLifePolicyNo());
				params.put("customerName", nonDto.getProposalInsuredPersonList().get(0).getFullName());
				params.put("beneName", endChange.getOldValue());
				params.put("newbeneName", endChange.getNewValue());
				params.put("startDate", Utils.getDateFormatString(nonDto.getStartDate()));
				params.put("endDate", Utils.getDateFormatString(nonDto.getEndDate()));
				params.put("inFormDate", Utils.formattedDate(dto.getEndorsementDate()));
				JasperPrint jprint1 = JasperFactory.generateJasperPrint(params, JasperTemplate.ENDORSEMENT_NON_FINANCIAL_LETTER_ByName, new JREmptyDataSource());
				jpList.add(jprint1);
			}

			if (endChange.getLifeEndorseItem().getLabel()  == "NRCNo Change") {
				//update code for beneName
				for(LifeEndorseBeneficiary bene :lifeEndorseBeneficiary) {
					String beneficiaryNo = bene.getBeneficiaryNo();
					String endorseChangeBeneNo = endChange.getBeneficiaryNo();
					if(beneficiaryNo.equals(endorseChangeBeneNo)) {
						params.put("beneName",  bene.getFullName());
					}
				}
				params.put("dayNoti", DateUtils.getDayWithMyanmarLanguage(dto.getEndorsementDate()));
				params.put("monthNoti", DateUtils.getMonthWithMyanmarLanguage(dto.getEndorsementDate()));
				params.put("yearNoti", DateUtils.getYearWithMyanmarLanguage(dto.getEndorsementDate()));
				params.put("productName", (productName));
				params.put("endorseNo", nonDto.getProposalNo());
				params.put("policyNo", dto.getLifePolicyNo());
				params.put("customerName", nonDto.getProposalInsuredPersonList().get(0).getFullName());				
				params.put("startDate", Utils.getDateFormatString(nonDto.getStartDate()));
				params.put("endDate", Utils.getDateFormatString(nonDto.getEndDate()));
				params.put("inFormDate", Utils.formattedDate(dto.getEndorsementDate()));
				params.put("oldNRC", endChange.getOldValue() != null ? endChange.getOldValue() : "Still Applying");
				params.put("newNRC", endChange.getNewValue() != null ? endChange.getNewValue() : "Still Applying");

				JasperPrint jprint2 = JasperFactory.generateJasperPrint(params, JasperTemplate.ENDORSEMENT_NON_FINANCIAL_LETTER_ByNRC, new JREmptyDataSource());
				jpList.add(jprint2);
			}
			if (endChange.getLifeEndorseItem().getLabel()  == "AddressChange") {
				//update code for beneName
				for(LifeEndorseBeneficiary bene :lifeEndorseBeneficiary) {
					String beneficiaryNo = bene.getBeneficiaryNo();
					String endorseChangeBeneNo = endChange.getBeneficiaryNo();
					if(beneficiaryNo.equals(endorseChangeBeneNo)) {
						params.put("beneName",  bene.getFullName());
					}
				}
				params.put("dayNoti", DateUtils.getDayWithMyanmarLanguage(dto.getEndorsementDate()));
				params.put("monthNoti", DateUtils.getMonthWithMyanmarLanguage(dto.getEndorsementDate()));
				params.put("yearNoti", DateUtils.getYearWithMyanmarLanguage(dto.getEndorsementDate()));
				params.put("productName", productName);
				params.put("endorseNo", nonDto.getProposalNo());
				params.put("policyNo", dto.getLifePolicyNo());
				params.put("customerName",  nonDto.getProposalInsuredPersonList().get(0).getFullName());
				//params.put("beneName", beneName);
				params.put("startDate", Utils.getDateFormatString(nonDto.getStartDate()));
				params.put("endDate", Utils.getDateFormatString(nonDto.getEndDate()));
				params.put("inFormDate", Utils.formattedDate(dto.getEndorsementDate()));
				params.put("oldAddress", endChange.getOldValue());
				params.put("newAddress", endChange.getNewValue());

				JasperPrint jprint3 = JasperFactory.generateJasperPrint(params, JasperTemplate.ENDORSEMENT_NON_FINANCIAL_LETTER_ByAddress, new JREmptyDataSource());
				jpList.add(jprint3);
			}
		}
		
		for (LifeEndorseChange endChange : endorsechangecopy) {	
			//String benes = endChange.getLifeEndorseBeneficiaryInfoList().get(0).getFullName();
			params.put("dayNoti", DateUtils.getDayWithMyanmarLanguage(dto.getEndorsementDate()));
			params.put("monthNoti", DateUtils.getMonthWithMyanmarLanguage(dto.getEndorsementDate()));
			params.put("yearNoti", DateUtils.getYearWithMyanmarLanguage(dto.getEndorsementDate()));
			params.put("productName", productName);
			params.put("endorseNo", nonDto.getProposalNo());
			params.put("policyNo", dto.getLifePolicyNo());
			//params.put("beneName", bene);
			params.put("customerName",  nonDto.getProposalInsuredPersonList().get(0).getFullName());
			params.put("startDate", Utils.getDateFormatString(nonDto.getStartDate()));
			params.put("endDate", Utils.getDateFormatString(nonDto.getEndDate()));
			params.put("inFormDate", Utils.formattedDate(dto.getEndorsementDate()));
			//Name Change
			if(endChange.getLifeEndorseItem().getLabel()  == "Name Change") {
				params.put("bene",  endChange.getNewValue());
				params.put("oldbeneName", endChange.getOldValue());
			}
			//NRC Change
			if(endChange.getLifeEndorseItem().getLabel()  == "NRCNo Change") {
				params.put("oldNRC", endChange.getOldValue() != null ? endChange.getOldValue() : "Still Applying");
				params.put("newNRC", endChange.getNewValue() != null ? endChange.getNewValue() : "Still Applying");
			}
			//RelationShip Change
			if(endChange.getLifeEndorseItem().getLabel()  == "Relationship Change") {
				params.put("oldrelationship", endChange.getOldValue());
				params.put("newrelationship", endChange.getNewValue());	
			}
			//Address Change
			if(endChange.getLifeEndorseItem().getLabel()  == "AddressChange") {
				Boolean isAddressChange = true;
				params.put("oldAddress", endChange.getOldValue());
				params.put("newAddress", endChange.getNewValue());
				params.put("isAddressChange", isAddressChange);
			}
			
			if (endChange.getLifeEndorseItem().getLabel()  == "Relationship Change") {	
				JasperPrint jprint4 = JasperFactory.generateJasperPrint(params, JasperTemplate.ENDORSEMENT_NON_FINANCIAL_LETTER_ByBeneficail, new JREmptyDataSource());
				jpList.add(jprint4);
			}

		}
		return jpList;
	}

	public static List<JasperPrint> generateReplaceBeneficialInfoList(List<BeneficiariesInfoDTO> oldbeneficialInfoList, List<BeneficiariesInfoDTO> newbeneficialInfoList, LifePolicy lifePolicy) {
		
		boolean isShortermEndownment = KeyFactorChecker.isShortTermEndowment(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct().getId());
		boolean isPublicLife = KeyFactorChecker.isPublicLife(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct());
		boolean isPersonalAccident = KeyFactorChecker.isPersonalAccident(lifePolicy.getPolicyInsuredPersonList().get(0).getProduct());

		String productName = "";
		String productDescription = "";
		if (isPublicLife) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_LIFE_LABEL") + "အသက်အာမခံ";
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else if (isShortermEndownment) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("SHORT_TERM_LIFE_LABEL") + "အသက်အာမခံ";
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else if (isPersonalAccident) {
			productName = MyanmarLanguae.getMyanmarLanguaeString("PERSONAL_ACCIDENT_LABEL_1");
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
		} else {
			productName = MyanmarLanguae.getMyanmarLanguaeString("GROUP_LIFE_LABEL") + "အသက်အာမခံ";
			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("GROUP_ACCEPTED_LABEL");
		}

		
		List<JasperPrint> jpringList = new ArrayList<JasperPrint>();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("productName", productName);
		paramMap.put("policyNo", lifePolicy.getPolicyNo());
		paramMap.put("oldbeneficialInfoList", new JRBeanCollectionDataSource(oldbeneficialInfoList));
		paramMap.put("newbeneficialInfoList", new JRBeanCollectionDataSource(newbeneficialInfoList));
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_ENDORSE_PERSON_REPLACE, new JREmptyDataSource());
		jpringList.add(jprint);
		return jpringList;
	}

	
//	public static List<JasperPrint> generateNonFinancialReportForReplaceBeneficiaryPerson(LifeProposal nonDto, List<LifeEndorseChange> change, LifeEndorseInfo dto) throws SystemException {
//		List<JasperPrint> jpList = new ArrayList<JasperPrint>();
//		Map<String, Object> params = new HashMap<String, Object>();
//		Product product = nonDto.getProposalInsuredPersonList().get(0).getProduct();
//		boolean isPublicLife;
//		boolean isShortermEndownment;
//		boolean isPersonalAccident;
//		boolean isGroupLife;
//		isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
//		isPublicLife = KeyFactorChecker.isPublicLife(product);
//		isGroupLife = KeyFactorChecker.isGroupLife(product);
//		isPersonalAccident = KeyFactorChecker.isPersonalAccident(product);
//		String productName = "";
//		String productDescription = "";
//		if (isPublicLife) {
//			productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_LIFE_LABEL") + "ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¡ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã‚Â¾ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¹ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¡ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¾Ã‚Â¢ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¯Ã‚Â¿Ã‚Â½ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¶";
//			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
//		} else if (isShortermEndownment) {
//			productName = MyanmarLanguae.getMyanmarLanguaeString("SHORT_TERM_LIFE_LABEL") + "ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¡ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã‚Â¾ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¹ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¡ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¾Ã‚Â¢ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¯Ã‚Â¿Ã‚Â½ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¶";
//			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
//		} else if (isPersonalAccident) {
//			productName = MyanmarLanguae.getMyanmarLanguaeString("PERSONAL_ACCIDENT_LABEL_1");
//			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("ACCEPTED_LABEL");
//		} else {
//			productName = MyanmarLanguae.getMyanmarLanguaeString("GROUP_LIFE_LABEL") + "ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¡ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€¦Ã‚Â¾ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¹ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¡ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¬ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â‚¬Å¾Ã‚Â¢ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¯Ã‚Â¿Ã‚Â½ÃƒÆ’Ã‚Â¡ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬Ãƒâ€šÃ‚Â¶";
//			productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("GROUP_ACCEPTED_LABEL");
//		}
//
//		for (LifeEndorseChange endChange : change) {			
//				String bene = endChange.getLifeEndorseBeneficiaryInfoList().get(0).getFullName();
//				params.put("dayNoti", DateUtils.getDayWithMyanmarLanguage(dto.getEndorsementDate()));
//				params.put("monthNoti", DateUtils.getMonthWithMyanmarLanguage(dto.getEndorsementDate()));
//				params.put("yearNoti", DateUtils.getYearWithMyanmarLanguage(dto.getEndorsementDate()));
//				params.put("productName", productName);
//				params.put("endorseNo", nonDto.getProposalNo());
//				params.put("policyNo", dto.getLifePolicyNo());
//				//params.put("beneName", bene);
//				params.put("customerName", nonDto.getProposalInsuredPersonList().get(0).getFullName());
//				params.put("startDate", Utils.getDateFormatString(nonDto.getStartDate()));
//				params.put("endDate", Utils.getDateFormatString(nonDto.getEndDate()));
//				params.put("inFormDate", Utils.formattedDate(dto.getEndorsementDate()));
//				//Name Change
//				if(endChange.getLifeEndorseValue().label == "Name Change") {
//					params.put("bene",  endChange.getNewValue());
//					params.put("oldbeneName", endChange.getOldValue());
//				}
//				//NRC Change
//				if(endChange.getLifeEndorseValue().label == "NRCNo Change") {
//					params.put("oldNRC", endChange.getOldValue());
//					params.put("newNRC", endChange.getNewValue());
//				}
//				//RelationShip Change
//				if(endChange.getLifeEndorseValue().label == "Relationship Change") {
//					params.put("oldrelationship", endChange.getOldValue());
//					params.put("newrelationship", endChange.getNewValue());	
//				}
//				//Address Change
//				if(endChange.getLifeEndorseValue().label == "AddressChange") {
//					Boolean isAddressChange = true;
//					params.put("oldAddress", endChange.getOldValue());
//					params.put("newAddress", endChange.getNewValue());
//					params.put("isAddressChange", isAddressChange);
//				}
//
//
//			}
//		JasperPrint jprint4 = JasperFactory.generateJasperPrint(params, JasperTemplate.ENDORSEMENT_NON_FINANCIAL_LETTER_ByBeneficail, new JREmptyDataSource());
//		jpList.add(jprint4);
//		return jpList;
//	}

}
