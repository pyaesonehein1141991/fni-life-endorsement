package org.ace.insurance.web.common;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.ace.insurance.accept.AcceptedInfo;
import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.CurrencyUtils;
import org.ace.insurance.life.claim.LifeClaim;
import org.ace.insurance.life.claim.LifeDisabilityClaim;
import org.ace.insurance.life.paidUp.LifePaidUpProposal;
import org.ace.insurance.life.policy.EndorsementLifePolicyPrint;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policyHistory.LifePolicyHistory;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.product.Product;
import org.ace.insurance.report.ClaimVoucher.ClaimVoucherDTO;
import org.ace.insurance.report.TLF.TLFVoucherDTO;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.web.common.document.JasperFactory;
import org.ace.insurance.web.common.document.JasperTemplate;
import org.ace.insurance.web.common.myanmarLanguae.MyanmarLanguae;
import org.ace.insurance.web.common.ntw.mym.AbstractMynNumConvertor;
import org.ace.insurance.web.common.ntw.mym.DefaultConvertor;
import org.ace.insurance.web.common.ntw.mym.NumberToNumberConvertor;
import org.ace.insurance.web.manage.life.claim.LifeClaimDischargeFormDTO;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.web.ApplicationSetting;
import org.joda.time.Period;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

public class DocumentBuilder extends BasicDAO {
	public static void generateCustomerInfo(Customer customer, String dirPath, String fileName) {
		try {
			List<JasperPrint> jlist = new ArrayList<JasperPrint>();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("name", customer.getFullName());
			params.put("dateOfBirth", customer.getDateOfBirth());
			params.put("idNo", customer.getFullIdNo());
			params.put("birthMark", customer.getBirthMark());
			params.put("maritalStatus", customer.getMaritalStatus().getLabel());
			params.put("nationality", customer.getCountry() == null ? "" : customer.getCountry().getName());
			params.put("religion", customer.getReligion() == null ? "" : customer.getReligion().getName());
			params.put("qualification", customer.getQualification() == null ? "" : customer.getQualification().getName());
			params.put("bankBranch", customer.getBankBranch() == null ? "" : customer.getBankBranch().getName());
			params.put("accountNo", customer.getBankAccountNo());
			params.put("industry", customer.getIndustry() == null ? "" : customer.getIndustry().getName());
			params.put("occupation", customer.getOccupation() == null ? "" : customer.getOccupation().getName());
			params.put("salary", customer.getSalary());
			params.put("residentAddress", customer.getFullAddress());
			params.put("officeAddress", customer.getOfficeAddress().getOfficeAddress());
			params.put("parmanentAddress", customer.getPermanentAddress().getFullAddress());
			params.put("mobile", customer.getContentInfo().getMobile());
			params.put("email", customer.getContentInfo().getEmail());
			params.put("fax", customer.getContentInfo().getFax());

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.CUS_INFO);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, params, new JREmptyDataSource());
			if (jprint.getPages().size() > 1) {
				jprint.getPages().remove(1);
			}

			jlist.add(jprint);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jlist);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate CustomerInfo", e);
		}
	}

	public static void generatePrintSetUpLifePolicy(EndorsementLifePolicyPrint endorsementPolicyPrint, LifeProposal lifeProposal, LifePolicy lifePolicy,
			LifePolicyHistory lifePolicyHistory, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", lifePolicy.getLifeProposal().getProposalNo());
			paramMap.put("extraRegulation", endorsementPolicyPrint.getExtraRegulation());
			paramMap.put("currentDate", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("submittedDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicy.getLifeProposal().getSubmittedDate()));
			paramMap.put("commenmanceDate", org.ace.insurance.common.Utils.getDateFormatString(lifePolicyHistory.getCommenmanceDate()));
			paramMap.put("policyNo", lifePolicyHistory.getPolicyNo());
			paramMap.put("customerName", lifePolicyHistory.getCustomerName());
			paramMap.put("customerAddress", lifePolicyHistory.getCustomerAddress());
			paramMap.put("endorsementDescription", endorsementPolicyPrint.getEndorsementDescription());
			paramMap.put("endorseChange", endorsementPolicyPrint.getEndorseChange());
			paramMap.put("endorseChangeDetail", endorsementPolicyPrint.getEndorseChangeDetail());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_ENDORSE_ISSUE);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			if (jprint.getPages().size() > 1) {
				jprint.removePage(1);
			}
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate PrintSetUp LifePolicy", e);
		}

	}

	public static void generateLifeDeathClaimAcceptanceLetter(LifeClaim lifeClaim, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeClaim.getClaimInsuredPerson().getFullName());
			paramMap.put("customerAddress", lifeClaim.getClaimInsuredPerson().getFullResidentAddress());
			paramMap.put("policyNo", lifeClaim.getLifePolicy().getPolicyNo());
			String agentName = (lifeClaim.getLifePolicy().getAgent() == null) ? null : lifeClaim.getLifePolicy().getAgent().getFullName();
			paramMap.put("agent", agentName);
			paramMap.put("claimNo", lifeClaim.getClaimRequestId());
			paramMap.put("currentDate", claimAcceptedInfo.getInformDate());
			paramMap.put("liabilitiesAmount", claimAcceptedInfo.getClaimAmount());
			paramMap.put("loanAmount", lifeClaim.getTotalLoanAmount());
			paramMap.put("loanInterest", lifeClaim.getTotalLoanInterest());
			paramMap.put("renewalAmount", lifeClaim.getTotalRenewelAmount());
			paramMap.put("renewalInterest", lifeClaim.getTotalRenewelInterest());
			// paramMap.put("totalAmount", lifeClaim.getTotalNetClaimAmount());
			paramMap.put("serviceCharges", claimAcceptedInfo.getServicesCharges());
			paramMap.put("totalAmount", claimAcceptedInfo.getTotalAmount());
			paramMap.put("publicLife", isPublicLife(lifeClaim));

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_CLAIM_ACCEPT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeClaimAcceptanceLetter", e);
		}
	}

	private static boolean isPublicLife(LifeClaim lifeClaim) {
		if (lifeClaim.getLifePolicy().getPolicyInsuredPersonList().size() == 1) {
			return true;
		}
		return false;
	}

	public static void generateLifeDeathClaimRejectLetter(LifeClaim lifeClaim, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeClaim.getClaimInsuredPerson().getFullName());
			paramMap.put("currentDate", claimAcceptedInfo.getInformDate());
			if (lifeClaim.getClaimInsuredPerson() != null) {
				paramMap.put("nrc", lifeClaim.getClaimInsuredPerson().getIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("policyNo", lifeClaim.getLifePolicy().getPolicyNo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_CLAIM_REJECT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeDeathClaimRejectLetter", e);
		}
	}

	public static void generateLifeDisabilityClaimAcceptanceLetter(LifeDisabilityClaim disabilityClaim, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		try {

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", disabilityClaim.getClaimInsuredPerson().getFullName());
			paramMap.put("customerAddress", disabilityClaim.getClaimInsuredPerson().getFullResidentAddress());
			paramMap.put("policyNo", disabilityClaim.getLifePolicy().getPolicyNo());
			String agentName = (disabilityClaim.getLifePolicy().getAgent() == null) ? null : disabilityClaim.getLifePolicy().getAgent().getFullName();
			paramMap.put("agent", agentName);
			paramMap.put("claimNo", disabilityClaim.getClaimRequestId());
			paramMap.put("currentDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			paramMap.put("liabilitiesAmount", claimAcceptedInfo.getClaimAmount());
			paramMap.put("loanAmount", disabilityClaim.getTotalLoanAmount());
			paramMap.put("loanInterest", disabilityClaim.getTotalLoanInterest());
			paramMap.put("renewalAmount", disabilityClaim.getTotalRenewelAmount());
			paramMap.put("renewalInterest", disabilityClaim.getTotalRenewelInterest());
			// paramMap.put("totalAmount", lifeClaim.getTotalNetClaimAmount());
			paramMap.put("serviceCharges", claimAcceptedInfo.getServicesCharges());
			paramMap.put("totalAmount", claimAcceptedInfo.getTotalAmount());
			paramMap.put("waitingPeriod", disabilityClaim.getWaitingPeriod());
			// paramMap.put("disabilityClaimType",disabilityClaim.getDisabilityClaimType());
			paramMap.put("publicLife", isPublicLife(disabilityClaim));

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_DIS_CLAIM_ACCEPT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeDisabilityClaimAcceptanceLetter", e);
		}
	}

	public static void generateLifeDisabilityClaimRejectLetter(LifeDisabilityClaim disabilityClaim, ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", disabilityClaim.getClaimInsuredPerson().getFullName());
			paramMap.put("currentDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			if (disabilityClaim.getClaimInsuredPerson() != null) {
				paramMap.put("nrc", disabilityClaim.getClaimInsuredPerson().getIdNo());
			} else {
				paramMap.put("nrc", "");
			}
			paramMap.put("policyNo", disabilityClaim.getLifePolicy().getPolicyNo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_DIS_CLAIM_REJECT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate lifeDisabilityClaimRejectLetter", e);
		}
	}

	public static void generateLifeDisabilityClaimCashReceipt(LifeClaim lifeClaim, PaymentDTO payment, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();

			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				paramMap.put("bankAccountNo", payment.getBankAccountNo());
				paramMap.put("bank", payment.getBank().getName());
				paramMap.put("chequePayment", Boolean.TRUE);
				paramMap.put("receiptType", "Cheque Payment");
				paramMap.put("receiptName", "Payment No.");
			} else {
				paramMap.put("chequePayment", Boolean.FALSE);
				paramMap.put("receiptType", "Cash Payment");
				paramMap.put("receiptName", "Payment No.");
			}
			paramMap.put("claimNo", lifeClaim.getClaimRequestId());
			paramMap.put("policyNo", lifeClaim.getLifePolicy().getPolicyNo());
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("currentDate", payment.getConfirmDate());
			String agentName = (lifeClaim.getLifePolicy().getAgent() == null) ? null : lifeClaim.getLifePolicy().getAgent().getFullName();
			paramMap.put("agent", agentName);
			paramMap.put("insuredPerson", lifeClaim.getLifePolicy().getCustomerName());
			paramMap.put("customerAddress", lifeClaim.getLifePolicy().getCustomerAddress());
			paramMap.put("liabilitiesAmount", payment.getClaimAmount());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("totalAmount", payment.getTotalClaimAmount());
			paramMap.put("loanAmount", lifeClaim.getClaimInsuredPerson().getLoanAmount());
			paramMap.put("loanInterest", payment.getLoanInterest());
			paramMap.put("renewelAmount", lifeClaim.getClaimInsuredPerson().getRenewelAmount());
			paramMap.put("renewelInterest", payment.getRenewalInterest());
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_DIS_CLAIM_RECEIPT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);

			jasperListPDFExport(jasperPrintList, dirPath, fileName);
			/*
			 * FileHandler.forceMakeDirectory(dirPath); String outputFile =
			 * dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate FireCashReceipt", e);
		}
	}

	public static void generateLifeDeathClaimCashReceipt(LifeClaim lifeClaim, PaymentDTO payment, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();

			if (payment.getPaymentChannel().equals(PaymentChannel.CHEQUE)) {
				paramMap.put("bankAccountNo", payment.getBankAccountNo());
				paramMap.put("bank", payment.getBank().getName());
				paramMap.put("chequePayment", Boolean.TRUE);
				paramMap.put("receiptType", "Cheque Payment");
				paramMap.put("receiptName", "Payment No.");
			} else {
				paramMap.put("chequePayment", Boolean.FALSE);
				paramMap.put("receiptType", "Cash Payment");
				paramMap.put("receiptName", "Payment No.");
			}
			paramMap.put("claimNo", lifeClaim.getClaimRequestId());
			paramMap.put("policyNo", lifeClaim.getLifePolicy().getPolicyNo());
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("currentDate", payment.getConfirmDate());
			String agentName = (lifeClaim.getLifePolicy().getAgent() == null) ? null : lifeClaim.getLifePolicy().getAgent().getFullName();
			paramMap.put("agent", agentName);
			paramMap.put("insuredPerson", lifeClaim.getLifePolicy().getCustomerName());
			paramMap.put("customerAddress", lifeClaim.getLifePolicy().getCustomerAddress());
			paramMap.put("liabilitiesAmount", payment.getClaimAmount());
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("totalAmount", payment.getTotalClaimAmount());
			paramMap.put("loanAmount", lifeClaim.getClaimInsuredPerson().getLoanAmount());
			paramMap.put("loanInterest", payment.getLoanInterest());
			paramMap.put("renewelAmount", lifeClaim.getClaimInsuredPerson().getRenewelAmount());
			paramMap.put("renewelInterest", payment.getRenewalInterest());
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_CLAIM_RECEIPT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);

			jasperListPDFExport(jasperPrintList, dirPath, fileName);
			/*
			 * FileHandler.forceMakeDirectory(dirPath); String outputFile =
			 * dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate FireCashReceipt", e);
		}
	}

	public static void generateLifeDisabilityClaimDischargeCertificate(LifeClaimDischargeFormDTO discharge, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();

			String presentDate = Utils.formattedDate(discharge.getPresentDate());
			String liabilitiesAmount = Utils.formattedCurrency(discharge.getClaimAmount());
			paramMap.put("policyNo", discharge.getPolicyNo());
			paramMap.put("customerName", discharge.getCustomerName());
			paramMap.put("liabilitiesAmount", liabilitiesAmount);
			paramMap.put("sumInsured", discharge.getSumInsured());
			paramMap.put("presentDate", presentDate);

			paramMap.put("beneficiaryName", discharge.getBeneficiaryName());
			// paramMap.put("disabilityDate",);
			paramMap.put("commencementDate", discharge.getCommenmanceDate());
			paramMap.put("premium", discharge.getRenewelAmount());
			paramMap.put("renewelInterest", discharge.getRenewelInterest());
			paramMap.put("loanAmount", discharge.getLoanAmount());
			paramMap.put("loanInterest", discharge.getLoanInterest());
			paramMap.put("serviceCharges", discharge.getServiceCharges());
			paramMap.put("netAmount", discharge.getNetClaimAmount());
			paramMap.put("witnessName", " ");
			paramMap.put("witnessAddress", " ");
			paramMap.put("nrc", discharge.getIdNo());
			paramMap.put("fatherName", discharge.getFatherName());
			paramMap.put("customerAddress", discharge.getAddress());

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_DISCHARGE_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);

			jasperListPDFExport(jasperPrintList, dirPath, fileName);
			/*
			 * FileHandler.forceMakeDirectory(dirPath); String outputFile =
			 * dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate DischargeCertificate", e);
		}
	}

	public static void generateLifeClaimDischargeCertificate(LifeClaimDischargeFormDTO discharge, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			String presentDate = Utils.formattedDate(discharge.getPresentDate());
			String liabilitiesAmount = Utils.formattedCurrency(discharge.getClaimAmount());
			paramMap.put("policyNo", discharge.getPolicyNo());
			paramMap.put("customerName", discharge.getInsuredPersonName());
			paramMap.put("liabilitiesAmount", liabilitiesAmount);
			paramMap.put("beneficiaryName", discharge.getBeneficiaryName());
			paramMap.put("sumInsured", discharge.getSumInsured());
			paramMap.put("commencementDate", discharge.getCommenmanceDate());
			paramMap.put("premium", discharge.getRenewelAmount());
			paramMap.put("renewelInterest", discharge.getRenewelInterest());
			paramMap.put("loanAmount", discharge.getLoanAmount());
			paramMap.put("loanInterest", discharge.getLoanInterest());
			paramMap.put("netAmount", discharge.getNetClaimAmount());
			paramMap.put("nrc", discharge.getIdNo());
			paramMap.put("fatherName", discharge.getFatherName());
			paramMap.put("customerAddress", discharge.getAddress());
			paramMap.put("presentDate", presentDate);
			paramMap.put("serviceCharges", discharge.getServiceCharges());
			paramMap.put("maturityDate", discharge.getMaturityDate());
			paramMap.put("witnessName", " ");
			paramMap.put("witnessAddress", " ");

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_CLAIM_DISCHARGE_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			jasperPrintList.add(jprint);

			jasperListPDFExport(jasperPrintList, dirPath, fileName);
			/*
			 * FileHandler.forceMakeDirectory(dirPath); String outputFile =
			 * dirPath + fileName;
			 * JasperExportManager.exportReportToPdfFile(jprint, outputFile);
			 */
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate DischargeCertificate", e);
		}
	}

	// for generate TLFVoucher
	public static void generateTLFVoucher(List<TLFVoucherDTO> cashReceiptDTOList, String fullTemplateFilePath, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("TableDataSource", new JRBeanCollectionDataSource(cashReceiptDTOList));
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fullTemplateFilePath);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	// for generate ClaimVoucher
	public static void generateClaimVoucher(List<ClaimVoucherDTO> claimVoucherDTOList, String fullTemplateFilePath, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("TableDataSource", new JRBeanCollectionDataSource(claimVoucherDTOList));

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fullTemplateFilePath);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate AcceptanceLetter", e);
		}
	}

	/* for Life Surrender Proposal Inform */
	public static void generateLifeSurrenderInformForm(LifeSurrenderProposal surrenderProposal, ClaimAcceptedInfo claimAcceptedInfo, Date lastPaymentDate, Date firstPaymentDate, String dirPath, String fileName) {
		LifePolicy lifePolicy = surrenderProposal.getLifePolicy();
		double loanAmount = 0.0;
		double loanInterest = 0.0;
		double renewalAmount = 0.0;
		double renewalInterest = 0.0;
		int paymentMonth;
		int year;
		Period paymentPeriod;
		paymentPeriod = Utils.getPeriod(lifePolicy.getActivedPolicyStartDate(), lifePolicy.getCoverageDate(), false, true);
		paymentMonth = paymentPeriod.getMonths();
		year = paymentPeriod.getYears();
		try {

			Map<String, Object> paramMap = new HashMap<String, Object>();
			Product product = surrenderProposal.getLifePolicy().getPolicyInsuredPersonList().get(0).getProduct();
			boolean isPublicLife;
			boolean isShortermEndownment;
			boolean isStudentLife;
			boolean isPublicTermLife;
			isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
			isPublicLife = KeyFactorChecker.isPublicLife(product);
			isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
			isPublicTermLife = KeyFactorChecker.isPublicTermLife(product.getId());
			String productName = "";
			String productDescription = "";
			if (isPublicLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			} else if (isShortermEndownment) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("SHORT_TERM_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			} else if (isStudentLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("STUDENT_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			} else if (isPublicTermLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_TERM_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			} else {
				productName = MyanmarLanguae.getMyanmarLanguaeString("GROUP_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			}
			paramMap.put("product", productDescription);
			paramMap.put("currentDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			paramMap.put("policyNo", surrenderProposal.getPolicyNo());
			paramMap.put("customerName", lifePolicy.getCustomerName());
			paramMap.put("customerAddress", lifePolicy.getCustomerAddress());
			paramMap.put("surrenderAmount", Utils.getCurrencyFormatString(surrenderProposal.getSurrenderAmount()));
			paramMap.put("proposalNo", surrenderProposal.getProposalNo());
			paramMap.put("serviceCharges", claimAcceptedInfo.getServicesCharges());
			paramMap.put("activepolicystartdate", Utils.formattedDate(firstPaymentDate));
			paramMap.put("activepolicyenddate", Utils.formattedDate(surrenderProposal.getLifePolicy().getActivedPolicyEndDate()));
			paramMap.put("policyNo", surrenderProposal.getPolicyNo());
			paramMap.put("SI", Utils.getCurrencyFormatString(surrenderProposal.getLifePolicy().getSumInsured()));
			paramMap.put("todayDate", Utils.formattedDate(surrenderProposal.getSubmittedDate()));
			//paramMap.put("lastpaymentDate", Utils.formattedDate(surrenderProposal.getLifePolicy().getLastPaymentDate()));
			paramMap.put("lastpaymentDate", Utils.formattedDate(lastPaymentDate));
			paramMap.put("policyperiod", surrenderProposal.getLifePolicy().getPeriodOfYears());
			paramMap.put("paymentType", surrenderProposal.getLifePolicy().getPaymentType().getName());
			paramMap.put("premium", Utils.getCurrencyFormatString(surrenderProposal.getLifePolicy().getInsuredPersonInfo().get(0).getBasicTermPremium()));
			paramMap.put("paymentyear", year);
			paramMap.put("paymentMonth", paymentMonth);
			paramMap.put("dueNo", surrenderProposal.getLifePolicy().getLastPaymentTerm());
			paramMap.put("coveragedate", Utils.formattedDate(surrenderProposal.getLifePolicy().getCoverageDate()));
			paramMap.put("receivePremium", Utils.getCurrencyFormatString(surrenderProposal.getReceivedPremium()));
			double si = surrenderProposal.getLifePolicy().getSumInsured();
			paramMap.put("loanAmount", loanAmount);
			paramMap.put("loanInterest", loanInterest);
			paramMap.put("renewalAmount", renewalAmount);
			paramMap.put("renewalInterest", renewalInterest);
			paramMap.put("totalAmount", claimAcceptedInfo.getTotalAmount());
			paramMap.put("percentage", Double.valueOf(Math.round((surrenderProposal.getSurrenderAmount() / surrenderProposal.getReceivedPremium()) * 100)).intValue());

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_SURR_INFORM_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate report lifeSurrenderInform", e);
		}
	}

	public static void generateLifeSurrenderRejectLetter(LifeSurrenderProposal surrenderProposal, ClaimAcceptedInfo claimAcceptedInfo, String rejectDirPath,
			String rejectFileName) {
		LifePolicy lifePolicy = surrenderProposal.getLifePolicy();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", surrenderProposal.getProposalNo());
			paramMap.put("informedDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			paramMap.put("customerName", lifePolicy.getCustomerName());
			paramMap.put("nrc", lifePolicy.getCustomerNRC());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_SURR_REJECT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(rejectDirPath);
			String outputFile = rejectDirPath + rejectFileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate report Life Surrender Reject Letter", e);
		}
	}

	public static void generateLifeSurrenderpaymentletter(LifeSurrenderProposal surrenderProposal, List<PaymentTrackDTO> paymentTrackList, PaymentDTO payment,
			String dirPath, String fileName) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		int size = paymentTrackList.size() - 1;
		paramMap.put("vendorName", surrenderProposal.getPayee());
		if (!payment.getPaymentChannel().equals(PaymentChannel.CASHED)) {
			paramMap.put("paymentChannel", payment.getBank().getName());
			paramMap.put("number", payment.getPaymentChannel().equals(PaymentChannel.TRANSFER) ? payment.getPoNo() : payment.getChequeNo());
		} else {
			paramMap.put("paymentChannel", payment.getPaymentChannel().getLabel());
			paramMap.put("number", " ");
		}

		paramMap.put("voucherNo", payment.getReceiptNo());
		paramMap.put("date", Utils.formattedDate(payment.getPaymentDate()));
		// paramMap.put("CUR", currency.getCurrencyCode());
		paramMap.put("dueNo", surrenderProposal.getLifePolicy().getLastPaymentTerm());
		paramMap.put("paymentDate", Utils.formattedDate(paymentTrackList.get(size).getPaymentDate()));
		paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
		paramMap.put("productName", surrenderProposal.getClaimProduct().getName());
		paramMap.put("customerName", surrenderProposal.getLifePolicy().getCustomer().getFullName());
		paramMap.put("amount", surrenderProposal.getSurrenderAmount());
		paramMap.put("policyNo", surrenderProposal.getLifePolicy().getPolicyNo());
		paramMap.put("myanmarAmount", convertor.getNameWithDecimal(surrenderProposal.getSurrenderAmount()));

		List<JasperPrint> printList = new ArrayList<>();
		JasperPrint jprint1 = new JasperPrint();
		jprint1 = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_SURRENDER_PAYMENT_LETTER, new JREmptyDataSource());

		printList.add(jprint1);
		jasperListPDFExport(printList, dirPath, fileName);

	}

	public static void generateLifeSurrenderCashReceipt(LifeSurrenderProposal surrenderProposal, Double result, List<Payment> paymentTrackList,ClaimAcceptedInfo claimAcceptedInfo, PaymentDTO payment,Date firstPaymentDate, String dirPath,
			String fileName) {
		LifePolicy lifePolicy = surrenderProposal.getLifePolicy();
		double suminsured = lifePolicy.getSumInsured();
		Date startDate = lifePolicy.getActivedPolicyStartDate();
		Date endDate = lifePolicy.getActivedPolicyEndDate();
		int paidMonth = Utils.monthsBetween(startDate, endDate, false, true);
		String proposalNo = surrenderProposal.getProposalNo();
		String customerName = lifePolicy.getCustomerName();
		String customerAddress = lifePolicy.getCustomerAddress();
		String policyNo = surrenderProposal.getPolicyNo();
		double surrenderAmount = surrenderProposal.getSurrenderAmount();
		double lifePremium = surrenderProposal.getLifePremium();
		double serviceCharges = payment.getServicesCharges();
		double loan = 0.0;
		double loanInterest = 0.0;
		double teeMyaeInterest = 0.0;
		double totalAmount = payment.getTotalClaimAmount() - (loan + loanInterest + teeMyaeInterest + lifePremium);
		//29042021
		//int year;
		//int paymentMonth;
		//Period paymentPeriod;
		//Period paymentPeriod = Utils.getPeriod(lifePolicy.getActivedPolicyStartDate(), lifePolicy.getCoverageDate(), false, true);
		//int paymentMonth = paymentPeriod.getMonths();
		//int year = paymentPeriod.getYears();
		
		int paymentMonth = 0;
		int year = 0;
		int totalMonth = 0;
		
		OptionalInt toTerm =OptionalInt.of(surrenderProposal.getLifePolicy().getLastPaymentTerm());
		if(toTerm.isPresent()) {
			if(surrenderProposal.getLifePolicy().getPaymentType().getName().equals("ANNUAL") && surrenderProposal.getLifePolicy().getPaymentType().getName().equals("LUMPSUM")) {
				year = toTerm.getAsInt();
			} else {
				if(surrenderProposal.getLifePolicy().getPaymentType().getName().equals("SEMI-ANNUAL")) {
					totalMonth = toTerm.getAsInt() * 6;
				} else if(surrenderProposal.getLifePolicy().getPaymentType().getName().equals("QUARTER")) {
					totalMonth = toTerm.getAsInt() * 3;
				} else {
					totalMonth = toTerm.getAsInt();
				}
				year = totalMonth / 12;
				paymentMonth = totalMonth % 12;
			}
		}
		
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		String totalAmountInMMText = convertor.getName(totalAmount);
		String totalAmountInMM = NumberToNumberConvertor.getMyanmarNumber(totalAmount, true);
		String surrenderAmountInMM = NumberToNumberConvertor.getMyanmarNumber(surrenderAmount, true);
		String loanInMM = NumberToNumberConvertor.getMyanmarNumber(loan, true);
		String loanInterestInMM = NumberToNumberConvertor.getMyanmarNumber(loanInterest, true);
		String lifePremiumInMM = NumberToNumberConvertor.getMyanmarNumber(lifePremium, true);
		String serviceChargesInMM = NumberToNumberConvertor.getMyanmarNumber(serviceCharges, true);
		String teeMyaeInterestInMM = NumberToNumberConvertor.getMyanmarNumber(teeMyaeInterest, true);

		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", proposalNo);
			paramMap.put("policyNo", policyNo);
			paramMap.put("cashReceiptNo", payment.getReceiptNo());
			paramMap.put("surrenderAmount", surrenderAmount);
			paramMap.put("loan", loan);
			paramMap.put("loanInterest", loanInterest);
			paramMap.put("serviceCharges", payment.getServicesCharges());
			paramMap.put("lifePremium", lifePremium);
			paramMap.put("teeMyaeinterest", teeMyaeInterest);
			paramMap.put("suminsured", suminsured);
			paramMap.put("paidPremium", surrenderProposal.getReceivedPremium());
			paramMap.put("totalAmount", totalAmount);
			paramMap.put("confirmDate", payment.getConfirmDate());
			paramMap.put("insuredPerson", customerName);
			paramMap.put("claimType", "Surrender Claim");
			paramMap.put("customerAddress", customerAddress);
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			Currency currency = new Currency();
			currency.setCurrencyCode(CurrencyUtils.getCurrencyCode(null));
			paramMap.put("currencyFormat", CurrencyUtils.getCurrencyFormat(currency));
			paramMap.put("currencyCode", currency.getCurrencyCode());
			paramMap.put("currencySymbol", "KYT");

			/* For surrender jasper2 */
			Product product = surrenderProposal.getLifePolicy().getPolicyInsuredPersonList().get(0).getProduct();
			boolean isPublicLife;
			boolean isShortermEndownment;
			boolean isStudentLife;
			boolean isPublicTermLife;
			isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
			isPublicLife = KeyFactorChecker.isPublicLife(product);
			isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
			isPublicTermLife = KeyFactorChecker.isPublicTermLife(product.getId());
			String productName = "";
			String productDescription = "";
			if (isPublicLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else if (isShortermEndownment) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("SHORT_TERM_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
				productName = "Short Term Endowment Life Surrender Value Calculation";
			} else if (isStudentLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("STUDENT_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else if (isPublicTermLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_TERM_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else {
				productName = MyanmarLanguae.getMyanmarLanguaeString("GROUP_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			}
			paramMap.put("product", productName);
			paramMap.put("insuredPersonName", surrenderProposal.getLifePolicy().getCustomer().getFullName());
			paramMap.put("policyNo", surrenderProposal.getPolicyNo());
			paramMap.put("sumInsured", Utils.getCurrencyFormatString(surrenderProposal.getLifePolicy().getSumInsured()));
			paramMap.put("activepolicystartdate", Utils.formattedDate(surrenderProposal.getLifePolicy().getActivedPolicyStartDate()));
			paramMap.put("firstdate", Utils.formattedDate(firstPaymentDate));
			paramMap.put("activepolicyenddate", Utils.formattedDate(surrenderProposal.getLifePolicy().getActivedPolicyEndDate()));
			paramMap.put("policyperiod", surrenderProposal.getLifePolicy().getPeriodOfYears());
			paramMap.put("paymentType", surrenderProposal.getLifePolicy().getPaymentType().getName());
			paramMap.put("premium", Utils.getCurrencyFormatString(surrenderProposal.getLifePolicy().getInsuredPersonInfo().get(0).getBasicTermPremium()));
			paramMap.put("paymentyear", surrenderProposal.getPaymentYear());
			paramMap.put("coveragedate", Utils.formattedDate(surrenderProposal.getLifePolicy().getCoverageDate()));
			//paramMap.put("lastPaymentDate", Utils.formattedDate(surrenderProposal.getLifePolicy().getLastPaymentDate()));
			paramMap.put("lastPaymentDate", Utils.formattedDate(paymentTrackList.get(paymentTrackList.size() -1).getPaymentDate()));
			paramMap.put("receivePremium", Utils.getCurrencyFormatString(surrenderProposal.getReceivedPremium()));
			paramMap.put("todayDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			double si = surrenderProposal.getLifePolicy().getSumInsured();
			paramMap.put("paymentMonth", paymentMonth);
			paramMap.put("year", year);
			paramMap.put("result", result);
			paramMap.put("percentage", Double.valueOf(Math.round((surrenderProposal.getSurrenderAmount() / surrenderProposal.getReceivedPremium()) * 100)).intValue());
			paramMap.put("finalpremium", Utils.getCurrencyFormatString(surrenderProposal.getSurrenderAmount()));
			
			if (surrenderProposal.getLifePremium() != 0.0) {
				int month = 12 - paymentMonth;
				double premium = surrenderProposal.getLifePolicy().getInsuredPersonInfo().get(0).getBasicTermPremium();
				double needpremium = 0;
				double finalPremium = (result * si) / 1000;
				//paramMap.put("result", result);
				paramMap.put("finalPremium", Utils.getCurrencyFormatString(finalPremium));
				//paramMap.put("finalpremium", Utils.getCurrencyFormatString(finalPremium - needpremium));
				//paramMap.put("todayDate", Utils.formattedDate(new Date()));
				//paramMap.put("month", month);
				if(surrenderProposal.getLifePolicy().getPaymentType().getName().equals("SEMI-ANNUAL")) {
					int semi_annual = month / 6;
					needpremium = premium * semi_annual;
					paramMap.put("month", semi_annual);
				} else if(surrenderProposal.getLifePolicy().getPaymentType().getName().equals("QUARTER")) {
					int quarter =  month / 3;
					needpremium = premium * quarter;
					paramMap.put("month", quarter);
				} else if(surrenderProposal.getLifePolicy().getPaymentType().getName().equals("MONTHLY")) {
					needpremium = premium * month;
					paramMap.put("month", month);					
				}
				paramMap.put("needpremium", Utils.getCurrencyFormatString(needpremium));
				paramMap.put("dueNo", surrenderProposal.getLifePolicy().getLastPaymentTerm());
				paramMap.put("needPeriod", year + 1);

				InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_SURR_RECEIPT_LETTERformonth);
				JasperReport report1 = JasperCompileManager.compileReport(stream1);
				JasperPrint print1 = JasperFillManager.fillReport(report1, paramMap, new JREmptyDataSource());
				jasperPrintList.add(print1);
			}else {
				InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_SURR_RECEIPT_LETTER);
				JasperReport report1 = JasperCompileManager.compileReport(stream1);
				JasperPrint print1 = JasperFillManager.fillReport(report1, paramMap, new JREmptyDataSource());
				jasperPrintList.add(print1);
			}

			Map<String, Object> Map2 = new HashMap<String, Object>();
			Map2.put("customerName", surrenderProposal.getLifePolicy().getCustomer().getFullName());
			Map2.put("fromterm", 1);
			Map2.put("toterm", surrenderProposal.getLifePolicy().getLastPaymentTerm());
			Map2.put("receivePremium", Utils.getCurrencyFormatString(surrenderProposal.getReceivedPremium()));
			Map2.put("surrenderAmount", Utils.getCurrencyFormatString(surrenderAmount));
			Map2.put("currentData", "Surrender");
			Map2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			Map2.put("percentage", "("+String.valueOf(Double.valueOf(Math.round((surrenderProposal.getSurrenderAmount() / surrenderProposal.getReceivedPremium()) * 100)).intValue())+"%)");
			Map2.put("listDataSource", new JRBeanCollectionDataSource(paymentTrackList));
			JasperPrint jprint2 = JasperFactory.generateJasperPrint(Map2, JasperTemplate.Surrender_ATT, new JREmptyDataSource());
			jasperPrintList.add(jprint2);
			jasperListPDFExport(jasperPrintList, dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeSurrenderCashReceipt", e);
		}
	}

	public static void generateLifeSurrenderIssue(LifeSurrenderProposal proposal, String dirPath, String fileName) {
		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			Map<String, Object> paramMap2 = new HashMap<String, Object>();
			Product product = proposal.getLifePolicy().getPolicyInsuredPersonList().get(0).getProduct();
			boolean isPublicLife;
			boolean isShortermEndownment;
			boolean isStudentLife;
			boolean isPublicTermLife;
			isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
			isPublicLife = KeyFactorChecker.isPublicLife(product);
			isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
			isPublicTermLife = KeyFactorChecker.isPublicTermLife(product.getId());
			
			paramMap.put("empty", " ");
			paramMap.put("policyNo", proposal.getPolicyNo());
			paramMap.put("todayDate", Utils.formattedDate(proposal.getLifePolicy().getIssueDate()));
			paramMap.put("policyDate", Utils.formattedDate(proposal.getLifePolicy().getActivedPolicyStartDate()));
			paramMap.put("insuredPersonName", proposal.getLifePolicy().getInsuredPersonInfo().get(0).getFullName());
			paramMap.put("timesUpOrDeadDate", " ");
			paramMap.put("requestedPerson", proposal.getLifePolicy().getInsuredPersonInfo().get(0).getFullName());
			paramMap.put("sumInsured", Utils.getCurrencyFormatString(proposal.getLifePolicy().getSumInsured()));
			paramMap.put("premium", Utils.getCurrencyFormatString(proposal.getLifePremium()));
			// currently only premium , add the new minus values later
			double netAmountToMinus = proposal.getLifePremium();
			paramMap.put("netAmountToMinus", Utils.getCurrencyFormatString(netAmountToMinus));
			paramMap.put("netAmountToPay", Utils.getCurrencyFormatString(proposal.getSurrenderAmount()));
			// final amount = netAmountToPay - netAmountToMinus
			// double finalAmount = proposal.getSurrenderAmount() - proposal.getLifePremium();
			paramMap.put("finalAmount", Utils.getCurrencyFormatString(proposal.getSurrenderAmount()));
			paramMap.put("insuredPersonNRC", proposal.getLifePolicy().getInsuredPersonInfo().get(0).getIdNo());
			paramMap.put("reqAmount", Utils.getCurrencyFormatString(proposal.getSurrenderAmount()));
			paramMap.put("paymentDate", Utils.formattedDate(new Date()));
			AbstractMynNumConvertor convertor = new DefaultConvertor();
			paramMap.put("activedPolicyEndDate", Utils.formattedDate(proposal.getLifePolicy().getActivedPolicyEndDate()));
			paramMap.put("activepolicystartdate", Utils.formattedDate(proposal.getLifePolicy().getActivedPolicyStartDate()));
			//paramMap.put("finalAmountText", convertor.getName(finalAmount));

			//paramMap.put("finalAmountText", convertor.getNameWithDecimal(finalAmount));
			//paramMap.put("finalAmountNumeric", Utils.getCurrencyFormatString(finalAmount));
			paramMap.put("finalAmountText", convertor.getNameWithDecimal(proposal.getSurrenderAmount()));
			paramMap.put("finalAmountNumeric", Utils.getCurrencyFormatString(proposal.getSurrenderAmount()));
			
			String productName = "";
			String productDescription = "";
			if (isPublicLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else if (isShortermEndownment) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("SHORT_TERM_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else if (isStudentLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("STUDENT_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else if (isPublicTermLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_TERM_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else {
				productName = MyanmarLanguae.getMyanmarLanguaeString("GROUP_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			}
			paramMap.put("productDescription", productDescription);
			paramMap.put("productName", productName);

			/* For surrender jasper2 */
			paramMap2.put("product", productName);
			paramMap2.put("insuredPersonName", proposal.getLifePolicy().getInsuredPersonInfo().get(0).getFullName());
			paramMap2.put("policyNo", proposal.getPolicyNo());
			paramMap2.put("sumInsured", Utils.getCurrencyFormatString(proposal.getLifePolicy().getSumInsured()));
			paramMap2.put("activepolicystartdate", Utils.formattedDate(proposal.getLifePolicy().getActivedPolicyStartDate()));
			paramMap2.put("activepolicyenddate", Utils.formattedDate(proposal.getLifePolicy().getActivedPolicyEndDate()));
			paramMap2.put("policyperiod", proposal.getLifePolicy().getPeriodOfYears());
			paramMap2.put("paymentType", proposal.getLifePolicy().getPaymentType().getName());
			paramMap2.put("premium", Utils.getCurrencyFormatString(proposal.getLifePolicy().getInsuredPersonInfo().get(0).getPremium()));
			paramMap2.put("paymentyear", proposal.getPaymentYear());
			paramMap2.put("coveragedate", Utils.formattedDate(proposal.getLifePolicy().getCoverageDate()));
			paramMap2.put("receivePremium", Utils.getCurrencyFormatString(proposal.getReceivedPremium()));
			double si = proposal.getLifePolicy().getSumInsured();
			paramMap2.put("finalpremium", Utils.getCurrencyFormatString(((147 * si) / 1000)));
			paramMap2.put("todayDate", Utils.formattedDate(new Date()));

			InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_SURR_ISSUE_LETTER);

			JasperReport report = JasperCompileManager.compileReport(stream);

			JasperPrint print = JasperFillManager.fillReport(report, paramMap, new JREmptyDataSource());

			jasperPrintList.add(print);

			jasperListPDFExport(jasperPrintList, dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeSurrenderIssue", e);
		}
	}

	/* Life PaidUp Proposal */
	public static void generateLifePaidUpInformForm(LifePaidUpProposal lifePaidUpProposal, ClaimAcceptedInfo claimAcceptedInfo, Date lastPaymentDate, String dirPath, String fileName) {
		LifePolicy lifePolicy = lifePaidUpProposal.getLifePolicy();
		//int paymentMonth;
		//int year;
		//Period paymentPeriod;
		Period paymentPeriod = Utils.getPeriod(lifePolicy.getActivedPolicyStartDate(), lifePolicy.getCoverageDate(), false, true);
		int paymentMonth = paymentPeriod.getMonths();
		int year = paymentPeriod.getYears();

		try {
			Map<String, Object> sanctionParamMap = new HashMap<>();
			Product product = lifePaidUpProposal.getLifePolicy().getPolicyInsuredPersonList().get(0).getProduct();
			boolean isPublicLife;
			boolean isShortermEndownment;
			boolean isStudentLife;
			boolean isPublicTermLife;
			isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
			isPublicLife = KeyFactorChecker.isPublicLife(product);
			isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
			isPublicTermLife = KeyFactorChecker.isPublicTermLife(product.getId());
			String productName = "";
			String productDescription = "";
			if (isPublicLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			} else if (isShortermEndownment) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("SHORT_TERM_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			} else if (isStudentLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("STUDENT_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			} else if (isPublicTermLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_TERM_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			} else {
				productName = MyanmarLanguae.getMyanmarLanguaeString("GROUP_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_LABEL");
			}
			// sanctionParamMap.put("productDescription", productDescription);
			sanctionParamMap.put("product", productDescription);
			sanctionParamMap.put("todayDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			sanctionParamMap.put("currentDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			sanctionParamMap.put("customerName", lifePolicy.getCustomerName());
			sanctionParamMap.put("policyNo", lifePaidUpProposal.getPolicyNo());
			sanctionParamMap.put("proposalNo", lifePaidUpProposal.getProposalNo());
			sanctionParamMap.put("activepolicystartdate", Utils.formattedDate(lifePolicy.getActivedPolicyStartDate()));
			sanctionParamMap.put("activepolicyenddate", Utils.formattedDate(lifePolicy.getActivedPolicyEndDate()));
			sanctionParamMap.put("period", lifePaidUpProposal.getPolicyPeriod());
			sanctionParamMap.put("SI", Utils.getCurrencyFormatString(lifePaidUpProposal.getSumInsured()));
			sanctionParamMap.put("paymentType", lifePaidUpProposal.getLifePolicy().getPaymentType());
			sanctionParamMap.put("paymentYear", lifePaidUpProposal.getPaymentYear());
			sanctionParamMap.put("receivePremium", Utils.formattedCurrency(lifePaidUpProposal.getReceivedPremium()));
			//sanctionParamMap.put("paidupAmount", Utils.formattedCurrency(lifePaidUpProposal.getRealPaidUpAmount()));
			sanctionParamMap.put("paymentType", lifePaidUpProposal.getLifePolicy().getPaymentType().getName());
			sanctionParamMap.put("premium", Utils.getCurrencyFormatString(lifePaidUpProposal.getLifePolicy().getInsuredPersonInfo().get(0).getBasicTermPremium()));
			sanctionParamMap.put("paymentyear", lifePaidUpProposal.getPaymentYear());
			sanctionParamMap.put("paymentMonth", paymentMonth);
			sanctionParamMap.put("paymentYear", year);
			sanctionParamMap.put("dueNo", lifePaidUpProposal.getLifePolicy().getLastPaymentTerm());
			sanctionParamMap.put("lastpaymentDate", Utils.formattedDate(lastPaymentDate));

			double premium = lifePaidUpProposal.getLifePolicy().getInsuredPersonInfo().get(0).getBasicTermPremium();
			//double needpremium = premium * paymentMonth;
			sanctionParamMap.put("paidupAmount", Utils.getCurrencyFormatString(lifePaidUpProposal.getPaidUpAmount()));

			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_PAIDUP_INFORM_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, sanctionParamMap, new JREmptyDataSource());
			List<JasperPrint> jpList = new ArrayList<>();
			// jpList.add(paidUpSactionJP);
			// jpList.add(calculationJP);
			jpList.add(jprint);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jpList);
			FileHandler.forceMakeDirectory(dirPath);
			OutputStream outputStream = new FileOutputStream(dirPath + fileName);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.exportReport();
			outputStream.close();
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate report lifePaidUpInform", e);
		}
	}

	public static void generateLifePaidUpRejectLetter(LifePaidUpProposal lifePaidUpProposal, ClaimAcceptedInfo claimAcceptedInfo, String rejectDirPath, String rejectFileName) {
		LifePolicy lifePolicy = lifePaidUpProposal.getLifePolicy();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", lifePaidUpProposal.getProposalNo());
			paramMap.put("informedDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			paramMap.put("customerName", lifePolicy.getCustomerName());
			paramMap.put("nrc", lifePolicy.getCustomerNRC());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_SURR_REJECT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(rejectDirPath);
			String outputFile = rejectDirPath + rejectFileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate report Life PaidUp Reject Letter", e);
		}
	}

	public static void generateLifePaidUpConfrimForm(LifePaidUpProposal paidupProposal, List<Payment> paymentTrackList,ClaimAcceptedInfo claimAcceptedInfo, String dirPath, String fileName, Date lastPaymentDate) {
		LifePolicy lifePolicy = paidupProposal.getLifePolicy();
		double suminsured = lifePolicy.getSumInsured();
		Date startDate = lifePolicy.getActivedPolicyStartDate();
		Date endDate = lifePolicy.getActivedPolicyEndDate();
		int paidMonth = Utils.monthsBetween(startDate, endDate, false, true);
		String proposalNo = paidupProposal.getProposalNo();
		String customerName = lifePolicy.getCustomerName();
		String customerAddress = lifePolicy.getCustomerAddress();
		String policyNo = paidupProposal.getPolicyNo();
		double paidUpAmount = paidupProposal.getPaidUpAmount();
		double lifePremium = paidupProposal.getReqAmount();
		// double serviceCharges = payment.getServicesCharges();
		double loan = 0.0;
		double loanInterest = 0.0;
		double teeMyaeInterest = 0.0;
		// double totalAmount = payment.getTotalClaimAmount() - (loan +
		// loanInterest + teeMyaeInterest + lifePremium);

		//29042021
		//int year;
		//int paymentMonth;
		//Period paymentPeriod;
		Period paymentPeriod = Utils.getPeriod(lifePolicy.getActivedPolicyStartDate(), lifePolicy.getCoverageDate(), false, true);
		int paymentMonth = paymentPeriod.getMonths();
		int year = paymentPeriod.getYears();
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		// String totalAmountInMMText = convertor.getName(totalAmount);
		// String totalAmountInMM =
		// NumberToNumberConvertor.getMyanmarNumber(totalAmount, true);
		String surrenderAmountInMM = NumberToNumberConvertor.getMyanmarNumber(paidUpAmount, true);
		String loanInMM = NumberToNumberConvertor.getMyanmarNumber(loan, true);
		String loanInterestInMM = NumberToNumberConvertor.getMyanmarNumber(loanInterest, true);
		String lifePremiumInMM = NumberToNumberConvertor.getMyanmarNumber(lifePremium, true);
		// String serviceChargesInMM =
		// NumberToNumberConvertor.getMyanmarNumber(serviceCharges, true);
		String teeMyaeInterestInMM = NumberToNumberConvertor.getMyanmarNumber(teeMyaeInterest, true);

		try {
			List<JasperPrint> jasperPrintList = new ArrayList<JasperPrint>();
			Map<String, Object> paramMap = new HashMap<String, Object>();

			paramMap.put("insuredPerson", customerName);
			paramMap.put("claimType", "Surrender Claim");
			paramMap.put("customerAddress", customerAddress);
			paramMap.put("reportLogo", ApplicationSetting.getReportLogo());
			Currency currency = new Currency();
			currency.setCurrencyCode(CurrencyUtils.getCurrencyCode(null));
			paramMap.put("currencyFormat", CurrencyUtils.getCurrencyFormat(currency));
			paramMap.put("currencyCode", currency.getCurrencyCode());
			paramMap.put("currencySymbol", "KYT");

			/* For surrender jasper2 */
			Product product = paidupProposal.getLifePolicy().getPolicyInsuredPersonList().get(0).getProduct();
			boolean isPublicLife;
			boolean isShortermEndownment;
			boolean isStudentLife;
			boolean isPublicTermLife;
			isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
			isPublicLife = KeyFactorChecker.isPublicLife(product);
			isStudentLife = KeyFactorChecker.isStudentLife(product.getId());
			isPublicTermLife = KeyFactorChecker.isPublicTermLife(product.getId());
			String productName = "";
			String productDescription = "";
			if (isPublicLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else if (isShortermEndownment) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("SHORT_TERM_LIFE_LABEL");
				productName = "Short Term Endowment Life Paid Up Value Calculation";
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else if (isStudentLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("STUDENT_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else if (isPublicTermLife) {
				productName = MyanmarLanguae.getMyanmarLanguaeString("PUBLIC_TERM_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			} else {
				productName = MyanmarLanguae.getMyanmarLanguaeString("GROUP_LIFE_LABEL");
				productDescription = productName + MyanmarLanguae.getMyanmarLanguaeString("LIFE_SURRENDER_PRODUCT_LABEL");
			}

			paramMap.put("product", productName);
			paramMap.put("insuredPersonName", paidupProposal.getLifePolicy().getCustomer().getFullName());
			paramMap.put("policyNo", paidupProposal.getPolicyNo());
			paramMap.put("proposalNo", paidupProposal.getProposalNo());
			paramMap.put("sumInsured", Utils.getCurrencyFormatString(paidupProposal.getLifePolicy().getSumInsured()));
			paramMap.put("activepolicystartdate", Utils.formattedDate(paidupProposal.getLifePolicy().getActivedPolicyStartDate()));
			paramMap.put("activepolicyenddate", Utils.formattedDate(paidupProposal.getLifePolicy().getActivedPolicyEndDate()));
			paramMap.put("policyperiod", paidupProposal.getLifePolicy().getPeriodOfYears());
			paramMap.put("paymentType", paidupProposal.getLifePolicy().getPaymentType().getName());
			paramMap.put("premium", Utils.getCurrencyFormatString(paidupProposal.getLifePolicy().getInsuredPersonInfo().get(0).getBasicTermPremium()));
			paramMap.put("paymentyear", paidupProposal.getPaymentYear());
			paramMap.put("coveragedate", Utils.formattedDate(paidupProposal.getLifePolicy().getCoverageDate()));
			paramMap.put("lastPaymentDate", Utils.formattedDate(lastPaymentDate));
			paramMap.put("receivePremium", Utils.getCurrencyFormatString(paidupProposal.getReceivedPremium()));
			paramMap.put("todayDate", Utils.formattedDate(claimAcceptedInfo.getInformDate()));
			double si = paidupProposal.getLifePolicy().getSumInsured();
			paramMap.put("year", year);
			//int month = 12 - paymentMonth;
			double premium = paidupProposal.getLifePolicy().getInsuredPersonInfo().get(0).getBasicTermPremium();
			double needpremium = premium * paymentMonth;

			if (paidupProposal.getReqAmount() != 0.0) {
				//int month = 12 - paymentMonth;
				//double premium = paidupProposal.getLifePolicy().getInsuredPersonInfo().get(0).getBasicTermPremium();
				//double needpremium = premium * paymentMonth;
				//paramMap.put("finalpremium", Utils.getCurrencyFormatString(paidupProposal.getRealPaidUpAmount()));
				paramMap.put("finalPremium", Utils.getCurrencyFormatString(paidupProposal.getPaidUpAmount()));
				paramMap.put("finalpremium", Utils.getCurrencyFormatString(paidupProposal.getPaidUpAmount() + needpremium));
				paramMap.put("month", paymentMonth);
				paramMap.put("needpremium", Utils.getCurrencyFormatString(needpremium));
				paramMap.put("paymentyear", year);
				paramMap.put("needPeriod", year + 1);
				paramMap.put("dueNo", paidupProposal.getLifePolicy().getLastPaymentTerm());

				InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_Paid_RECEIPT_LETTERformonth);
				JasperReport report1 = JasperCompileManager.compileReport(stream1);
				JasperPrint print1 = JasperFillManager.fillReport(report1, paramMap, new JREmptyDataSource());
				jasperPrintList.add(print1);
			}else {
				paramMap.put("finalpremium", Utils.getCurrencyFormatString(paidupProposal.getPaidUpAmount()));
				InputStream stream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.LIFE_Paid_RECEIPT_LETTER);
				JasperReport report1 = JasperCompileManager.compileReport(stream1);
				JasperPrint print1 = JasperFillManager.fillReport(report1, paramMap, new JREmptyDataSource());
				jasperPrintList.add(print1);
			}

			Map<String, Object> Map2 = new HashMap<String, Object>();
			Map2.put("customerName", paidupProposal.getLifePolicy().getCustomer().getFullName());
			Map2.put("fromterm", 1);
			Map2.put("toterm", paidupProposal.getLifePolicy().getLastPaymentTerm());
			Map2.put("receivePremium", Utils.getCurrencyFormatString(paidupProposal.getReceivedPremium()));
			Map2.put("surrenderAmount", Utils.getCurrencyFormatString(paidupProposal.getPaidUpAmount()));
			Map2.put("currentData", "PaidUp");
			Map2.put("percentage", "");
			Map2.put("policy4Fly", ApplicationSetting.getPolicy4Fly());
			Map2.put("listDataSource", new JRBeanCollectionDataSource(paymentTrackList));
			JasperPrint jprint2 = JasperFactory.generateJasperPrint(Map2, JasperTemplate.Surrender_ATT, new JREmptyDataSource());
			jasperPrintList.add(jprint2);
			jasperListPDFExport(jasperPrintList, dirPath, fileName);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate LifeSurrenderCashReceipt", e);
		}
	}

	/* Student Life */
	public static void generateStudentLifeSanction(LifeProposal lifeProposal, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("customerName", lifeProposal.getCustomer().getFullName());
			paramMap.put("insuredPersonName", insuredPerson.getFullName());
			paramMap.put("age", lifeProposal.getCustomer().getAgeForNextYear());
			paramMap.put("paymentType", lifeProposal.getPaymentType().getName());
			paramMap.put("insuredPersonAge", insuredPerson.getAge());
			paramMap.put("sumInsured", Utils.formattedCurrency(lifeProposal.getSumInsured()));
			paramMap.put("period", lifeProposal.getPeriodOfYears());
			paramMap.put("premiumTerm", lifeProposal.getPremiumTerm());
			paramMap.put("medicalInfo", lifeProposal.getCustomerClsOfHealth() != null ? lifeProposal.getCustomerClsOfHealth().getLabel() : "");
			double totalTermPremim = lifeProposal.getTotalTermPremium();
			double kyat = Math.floor(totalTermPremim);
			double pyar = (totalTermPremim - kyat) * 100;
			DecimalFormat formatter1 = new DecimalFormat("#,###.00");
			String termPremiumKyat = formatter1.format(kyat);
			DecimalFormat formatter2 = new DecimalFormat("00");
			String termPremiumPyar = formatter2.format(pyar);
			double totalPremium = lifeProposal.getTotalPremium();
			paramMap.put("totalPremium", formatter1.format(totalTermPremim));
			paramMap.put("premiumKyat", termPremiumKyat);
			paramMap.put("premiumPyar", termPremiumPyar);
			paramMap.put("reportLogo", "report-template/fni-logo.png");
			paramMap.put("reportAddress", ApplicationSetting.getGGIAddress());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_SANCTION_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Student Life Sanction", e);
		}
	}

	public static void generateStudentLifeAcceptanceLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			ProposalInsuredPerson insuredPerson = lifeProposal.getProposalInsuredPersonList().get(0);
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("childName", insuredPerson.getFullName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("date", org.ace.insurance.common.Utils.getDateFormatString(acceptedInfo.getInformDate()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			paramMap.put("periodYear", lifeProposal.getPeriodOfYears());
			paramMap.put("premiumTerm", lifeProposal.getPremiumTerm());
			paramMap.put("sumInsured", Utils.formattedCurrency(lifeProposal.getApprovedSumInsured()));
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
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_INFORM_ACCEPTED_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Student Life AcceptanceLetter", e);
		}
	}

	public static void generateStudentLifeRejectLetter(LifeProposal lifeProposal, AcceptedInfo acceptedInfo, String dirPath, String fileName) {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (lifeProposal.getCustomer() != null) {
				if (lifeProposal.getCustomer().getFullIdNo() != null) {
					paramMap.put("nrc", lifeProposal.getCustomer().getFullIdNo());
				} else {
					paramMap.put("nrc", "");
				}
			}
			if (lifeProposal.getAgent() != null) {
				paramMap.put("agent", lifeProposal.getAgent().getFullName() + "( " + lifeProposal.getAgent().getLiscenseNo() + " )");
			} else {
				paramMap.put("agent", "( - )");
			}
			paramMap.put("customerName", lifeProposal.getCustomerName());
			paramMap.put("customerAddress", lifeProposal.getCustomerAddress());
			paramMap.put("phoneNo", lifeProposal.getPhoneNo());
			paramMap.put("todayDate", org.ace.insurance.common.Utils.getDateFormatString(new Date()));
			paramMap.put("proposalNo", lifeProposal.getProposalNo());
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(JasperTemplate.STUDENTLIFE_INFORM_REJECT_LETTER);
			JasperReport jreport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jprint = JasperFillManager.fillReport(jreport, paramMap, new JREmptyDataSource());
			FileHandler.forceMakeDirectory(dirPath);
			String outputFile = dirPath + fileName;
			JasperExportManager.exportReportToPdfFile(jprint, outputFile);
		} catch (Exception e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to generate Student Life RejectLetter", e);
		}
	}

	private static double changeAmount(double amount) {
		DecimalFormat df = new DecimalFormat("###,###.00");
		return Double.valueOf(df.format(amount));
	}

}
