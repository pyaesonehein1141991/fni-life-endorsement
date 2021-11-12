package org.ace.insurance.web.common.document.travel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.Utils;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicyInfo;
import org.ace.insurance.travel.personTravel.policy.PolicyPersonTravelVehicle;
import org.ace.insurance.travel.personTravel.policy.PolicyTraveller;
import org.ace.insurance.travel.personTravel.policy.PolicyTravellerBeneficiary;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.ProposalPersonTravelVehicle;
import org.ace.insurance.web.common.document.JasperFactory;
import org.ace.insurance.web.common.document.JasperTemplate;
import org.ace.insurance.web.common.myanmarLanguae.MyanmarLanguae;
import org.ace.insurance.web.common.ntw.mym.AbstractMynNumConvertor;
import org.ace.insurance.web.common.ntw.mym.DefaultConvertor;
import org.ace.java.web.ApplicationSetting;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperPrint;

public class TravelDocumentBuilder extends JasperFactory {

	/** Person Travel Invoice Letter */
	public static List<JasperPrint> generatePersonTravelPaymentInvoice(PersonTravelProposal personTravelProposal, Payment payment) {
		Date invoiceDate = payment.getConfirmDate();
		String invoice = payment.getInvoiceNo();
		String proposalNo = personTravelProposal.getProposalNo();
		String startDate = Utils.formattedDate(personTravelProposal.getSubmittedDate());
		String customerName = personTravelProposal.getCustomerName();
		String address = personTravelProposal.getCustomerAddress();
		String agentRegNo = personTravelProposal.getAgentLiscenseNo();
		String agentName = personTravelProposal.getAgentName();
		double premium = personTravelProposal.getPersonTravelInfo().getTotalPremium();
		double totalPremium = payment.getTotalAmount();
		double discount = payment.getSpecialDiscount();
		double serviceCharges = payment.getServicesCharges();
		String travelPath = personTravelProposal.getPersonTravelInfo().getTravelPath();
		Date departureDate = personTravelProposal.getPersonTravelInfo().getDepartureDate();
		Date arrivalDate = personTravelProposal.getPersonTravelInfo().getArrivalDate();
		double unit = personTravelProposal.getPersonTravelInfo().getTotalUnit();
		double sumInsured = personTravelProposal.getPersonTravelInfo().getSumInsured();

		String ABROAD_LABEL = MyanmarLanguae.getMyanmarLanguaeString("ABROAD_LABEL");
		String CIVIL_LABEL = MyanmarLanguae.getMyanmarLanguaeString("CIVIL_LABEL");

		Map<String, Object> paramMap = new HashMap<>();
		int c = 0;
		String vehicleNo = "";
		for (ProposalPersonTravelVehicle vehicle : personTravelProposal.getPersonTravelInfo().getProposalPersonTravelVehicleList()) {
			vehicleNo += c > 0 ? ", " : "";
			vehicleNo += vehicle.getRegistrationNo();
			c++;
		}
		paramMap.put("usageOfVehicle1", vehicleNo);
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("invoiceNo", invoice);
		String typeOfInsurance = "";
		if (personTravelProposal.getProduct().getProductContent().getName().equals("FOREIGN TRAVEL INSURANCE")) {
			typeOfInsurance = "Travel Insurance (abroad) ".concat(ABROAD_LABEL);
			paramMap.put("section", typeOfInsurance);
		} else if (personTravelProposal.getProduct().getProductContent().getName().equals("LOCAL TRAVEL INSURANCE")) {
			typeOfInsurance = "Travel Insurance (civil) ".concat(CIVIL_LABEL);
			paramMap.put("section", typeOfInsurance);
		} else if (personTravelProposal.getProduct().getProductContent().getName().equals("UNDER 100 MILES TRAVEL INSURANCE")) {
			typeOfInsurance = "Travel Insurance (under 100 Miles)";
			paramMap.put("section", typeOfInsurance);
		} else if (personTravelProposal.getProduct().getProductContent().getName().equals("TRAVEL INSURACE")) {
			typeOfInsurance = "Travel Insurance";
			paramMap.put("section", typeOfInsurance);
		}
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("fromDate", startDate);
		paramMap.put("custName", customerName);
		paramMap.put("address", address);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("SumInsu", sumInsured);
		paramMap.put("premium", premium);
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("discount", discount);
		paramMap.put("serviceCharges", serviceCharges);
		paramMap.put("travelPath", travelPath);
		paramMap.put("departureDate", departureDate);
		paramMap.put("arrivalDate", arrivalDate);
		paramMap.put("unit", unit);
		paramMap.put("invoice4Fly", ApplicationSetting.getPolicy4Fly());
		paramMap.put("branchAddress", personTravelProposal.getBranch().getAddress());
		/*
		 * paramMap.put("branchPhNo", "Office : " +
		 * personTravelProposal.getBranch().getContentInfo().getPhone() + ", " +
		 * "Call Center : " +
		 * personTravelProposal.getBranch().getContentInfo().getMobile());
		 * paramMap.put("branchFax", "Fax : " +
		 * personTravelProposal.getBranch().getContentInfo().getFax());
		 * paramMap.put("branchEmail", "E-mail : " +
		 * personTravelProposal.getBranch().getContentInfo().getEmail());
		 * paramMap.put("branchWebSite", "Website : " +
		 * personTravelProposal.getBranch().getWebsiteUrl());
		 */
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PERSONTRAVEL_PAYMENT_INVOICE, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** Person Travel Receipt Letter */
	public static <T> List<JasperPrint> generatePensonTravelReceipt(PersonTravelProposal personTravelProposal, Payment payment) {
		String proposalNo = personTravelProposal.getProposalNo();
		String receiptNo = payment.getReceiptNo();
		Date receiptDate = payment.getPaymentDate();
		String departureDate = Utils.formattedDate(personTravelProposal.getPersonTravelInfo().getDepartureDate());
		String arrivalDate = Utils.formattedDate(personTravelProposal.getPersonTravelInfo().getArrivalDate());
		int day=Utils.daysBetween(personTravelProposal.getPersonTravelInfo().getDepartureDate(), personTravelProposal.getPersonTravelInfo().getArrivalDate(), false, true);
		String customerName = personTravelProposal.getCustomerName();
		String customerAddress = personTravelProposal.getCustomerAddress();
		String travelPath = personTravelProposal.getPersonTravelInfo().getTravelPath();
		double sI = personTravelProposal.getPersonTravelInfo().getSumInsured();
		String premium = Utils.formattedCurrency(personTravelProposal.getPersonTravelInfo().getTotalPremium());
		String totalamount = Utils.formattedCurrency(payment.getTotalAmount());
		String agent = personTravelProposal.getAgentInfo();
		String discount = Utils.formattedCurrency(payment.getSpecialDiscount());
		String serviceCharges = Utils.formattedCurrency(payment.getServicesCharges());
		int noOfUnit=personTravelProposal.getTotalUnit();

		Map<String, Object> paramMap = new HashMap<>();
		int c = 0;
		String vehicleNo = "";
		for (ProposalPersonTravelVehicle vehicle : personTravelProposal.getPersonTravelInfo().getProposalPersonTravelVehicleList()) {
			vehicleNo += c > 0 ? ", " : "";
			vehicleNo += vehicle.getRegistrationNo();
			c++;
		}
		paramMap.put("usageOfVehicle1", vehicleNo);
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("receiptNo", receiptNo);
		paramMap.put("receiptDate", receiptDate);
		paramMap.put("departureDate", departureDate);
		paramMap.put("arrivalDate", arrivalDate);
		paramMap.put("customerName", customerName);
		paramMap.put("address", customerAddress);
		paramMap.put("travelPath", travelPath);
		paramMap.put("totalSi", Utils.formattedCurrency(sI));
		paramMap.put("premium", premium);
		paramMap.put("agent", agent);
		paramMap.put("discount", discount);
		paramMap.put("serviceCharges", serviceCharges);
		paramMap.put("totalamount", totalamount);
		paramMap.put("adjustAmount", 0.00);
		paramMap.put("receipt4Fly", ApplicationSetting.getReceipt4Fly());
		paramMap.put("branchAddress", personTravelProposal.getBranch().getAddress());
		paramMap.put("day", day);
		paramMap.put("noOfUnit", noOfUnit);
		/*
		 * paramMap.put("branchTel",
		 * personTravelProposal.getBranch().getContentInfo().getPhoneOrMoblieNo(
		 * )); paramMap.put("branchEmail",
		 * personTravelProposal.getBranch().getContentInfo().getEmail());
		 * paramMap.put("siteUrl",
		 * personTravelProposal.getBranch().getWebsiteUrl());
		 */
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		paramMap.put("premiumInWord", convertor.getNameWithDecimal(payment.getTotalAmount()));
		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.PERSONTRAVEL_CASH_RECEPT, new JREmptyDataSource());
		return Arrays.asList(jprint, jprint);
	}

	/** Person Travel Policy Issue Letter */
	public static List<JasperPrint> generatePersonTravelPolicyIssue(PersonTravelPolicy personTravelPolicy) {
		Map<String, Object> params = new HashMap<String, Object>();
		PersonTravelPolicyInfo travelInfo = personTravelPolicy.getPersonTravelPolicyInfo();
		PolicyTraveller policyTraveller = null;
		PolicyTravellerBeneficiary beneficiarie = null;
		if (!travelInfo.getPolicyTravellerList().isEmpty()) {
			policyTraveller = travelInfo.getPolicyTravellerList().get(0);
			beneficiarie = policyTraveller.getPolicyTravellerBeneficiaryList().get(0);
		}
		params.put("policyNo", personTravelPolicy.getPolicyNo());
		params.put("agentName", personTravelPolicy.getAgentName());
		params.put("agentCode", personTravelPolicy.getAgentLiscneseNo());
		String customerName = policyTraveller != null ? policyTraveller.getName() : personTravelPolicy.getCustomerName();
		String idNo = policyTraveller != null ? policyTraveller.getFullIdNo() : personTravelPolicy.getCustomerFullIdNo();
		String fatherName = policyTraveller != null ? policyTraveller.getFatherName() : personTravelPolicy.getCustomerFatherName();
		String address = policyTraveller != null ? policyTraveller.getFullAddress() : personTravelPolicy.getCustomerAddress();
		String phoneNo = policyTraveller != null ? policyTraveller.getPhone() : personTravelPolicy.getCustomerPhno();
		params.put("date", new Date());
		params.put("travellerName", customerName);
		params.put("idNo", idNo);
		params.put("fatherName", fatherName);
		params.put("address", address);
		params.put("phoneNo", phoneNo);
		params.put("unit", travelInfo.getTotalUnit());
		params.put("premium", travelInfo.getPremium());
		params.put("travelPlace", travelInfo.getTravelPath());
		params.put("departureDay", travelInfo.getDepartureDate());
		params.put("arrivalDay", travelInfo.getArrivalDate());
		int c = 0;
		String vehicleNo = "";
		for (PolicyPersonTravelVehicle vehicle : travelInfo.getPolicyPersonTravelVehicleList()) {
			vehicleNo += c > 1 ? ", " : "";
			vehicleNo += vehicle.getUsageOfVehicle().getLabel() + " (" + vehicle.getRegistrationNo() + ")";
			c++;
		}
		params.put("usageOfVehicle", vehicleNo);
		if (beneficiarie != null) {
			params.put("name", beneficiarie.getFullName());
			params.put("fullIdNo", beneficiarie.getFullIdNo());
			params.put("beneficiarieaddress", beneficiarie.getResidentAddress().getFullResidentAddress());
			params.put("phone", "-");
			params.put("relationship", beneficiarie.getRelationship().getName());
			params.put("beneFather", "-");
		}
		params.put("invoice4Fly", ApplicationSetting.getLetterHead4Fly());
		params.put("branchAddress", personTravelPolicy.getBranch().getAddress());
		/*
		 * params.put("branchPhNo", "Office : " +
		 * personTravelPolicy.getBranch().getContentInfo().getPhone() + ", " +
		 * "Call Center : " +
		 * personTravelPolicy.getBranch().getContentInfo().getMobile());
		 * params.put("branchFax", "Fax : " +
		 * personTravelPolicy.getBranch().getContentInfo().getFax());
		 * params.put("branchEmail", "E-mail : " +
		 * personTravelPolicy.getBranch().getContentInfo().getEmail());
		 * params.put("branchWebSite", "Website : " +
		 * personTravelPolicy.getBranch().getWebsiteUrl());
		 */

		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		JasperPrint jprint = JasperFactory.generateJasperPrint(params, JasperTemplate.PERSONTRAVEL_POLICY_ISSUE, new JREmptyDataSource());
		jprintList.add(jprint);
		JasperPrint termAndCPrint = JasperFactory.generateJasperPrint(params, JasperTemplate.TRAVEL_TERM_CONDITION, new JREmptyDataSource());
		jprintList.add(termAndCPrint);
		return jprintList;
	}

	/** Special Travel Invoice Letter */
	public static List<JasperPrint> generateTravelPaymentInvoice(TravelProposal travelProposal, PaymentDTO payment) {
		int noOfUnit = travelProposal.getTotalUnit();
		String typeOfInsurance = "Special Travel Insurance";
		String proposalNo = travelProposal.getProposalNo();
		String customerName = travelProposal.getCustomerName();
		String address = travelProposal.getCustomerAddress();
		String agentRegNo = travelProposal.getAgentLiscenseNo();
		String agentName = travelProposal.getAgentName();
		String invoice = payment.getInvoiceNo();
		Date invoiceDate = payment.getConfirmDate();
		double totolSumInsured = travelProposal.getTotalSumInsured();
		double totalPremium = payment.getTotalAmount();
		double discount = travelProposal.getTotalDiscountAmount();
		double netPremium = payment.getPersonTravelNetPremium();

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("noOfUnits", noOfUnit);
		paramMap.put("typeOfInsurance", typeOfInsurance);
		paramMap.put("invoiceDate", invoiceDate);
		paramMap.put("invoiceNo", invoice);
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("departureDate", travelProposal.getFromDate());
		if (!travelProposal.getExpressList().isEmpty()) {
			if (travelProposal.getExpressList().get(0).getTourList().size() > 0) {
				paramMap.put("vehicleNo", travelProposal.getRegistrationNoList());
				paramMap.put("destination", travelProposal.getTourNameList());
			}
		}

		paramMap.put("custName", customerName);
		paramMap.put("address", address);
		paramMap.put("agentRegNo", agentRegNo);
		paramMap.put("agentName", agentName);
		paramMap.put("netPremium", netPremium);
		paramMap.put("adjPlus", 0.00);
		paramMap.put("adjMinus", 0.00);
		paramMap.put("discount", discount);
		paramMap.put("totalPremium", totalPremium);
		paramMap.put("totalSumInsured", totolSumInsured);

		// paramMap.put("invoice4Fly", ApplicationSetting.getLetterHead4Fly());
		paramMap.put("branchAddress", travelProposal.getBranch().getAddress());
		// paramMap.put("branchPhNo",
		// "Office : " + travelProposal.getBranch().getContentInfo().getPhone()
		// + ", " + "Call Center : " +
		// travelProposal.getBranch().getContentInfo().getMobile());
		// paramMap.put("branchFax", "Fax : " +
		// travelProposal.getBranch().getContentInfo().getFax());
		// paramMap.put("branchEmail", "E-mail : " +
		// travelProposal.getBranch().getContentInfo().getEmail());
		// paramMap.put("branchWebSite", "Website : " +
		// travelProposal.getBranch().getWebsiteUrl());

		JasperPrint jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.LIFE_PAYMENT_INVOICE_FOR_SpecialTravel, new JREmptyDataSource());
		return Arrays.asList(jprint);
	}

	/** Special Travel Cash Receipt Letter **/
	public static <T> List<JasperPrint> generateSpecialTravelReceipt(TravelProposal travelProposal, Payment payment) {
		TravelExpress express = travelProposal.getExpressList().get(0);
		String agent = travelProposal.getAgentInfo();

		String proposalNo = travelProposal.getProposalNo();
		String receiptNo = payment.getReceiptNo();
		Date receiptDate = payment.getPaymentDate();
		String premium = Utils.formattedCurrency(payment.getTotalPremium());
		String fromDate = Utils.formattedDate(travelProposal.getFromDate());
		String customer = travelProposal.getCustomerName();
		String address = express.getExpress().getFullAddress();
		double totalSI = travelProposal.getTotalSumInsured();
		String travelPath = null;
		if (!express.getTourList().isEmpty()) {
			travelPath = express.getTourList().get(0).getOccurrence().getDescription();
		}
		String serviceCharges = Utils.formattedCurrency(payment.getServicesCharges());

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("customerName", customer);
		paramMap.put("address", address);
		paramMap.put("agent", agent);
		paramMap.put("totalSi", Utils.formattedCurrency(totalSI));
		paramMap.put("proposalNo", proposalNo);
		paramMap.put("receiptNo", receiptNo);
		paramMap.put("receiptDate", receiptDate);
		paramMap.put("premium", premium);
		paramMap.put("discount", Utils.formattedCurrency(travelProposal.getTotalDiscountAmount()));
		paramMap.put("totalamount", Utils.formattedCurrency(payment.getTotalAmount()));
		paramMap.put("receipt4Fly", ApplicationSetting.getReceipt4Fly());
		paramMap.put("branchAddress", travelProposal.getBranch().getAddress());
		// paramMap.put("branchTel",
		// travelProposal.getBranch().getContentInfo().getPhoneOrMoblieNo());
		// paramMap.put("branchEmail",
		// travelProposal.getBranch().getContentInfo().getEmail());
		// paramMap.put("siteUrl", travelProposal.getBranch().getWebsiteUrl());
		paramMap.put("departureDate", fromDate);
		paramMap.put("adjustAmount", "00.0");
		paramMap.put("travelPath", travelPath);
		paramMap.put("serviceCharges", serviceCharges);
		paramMap.put("noOfUnit",(int)express.getNoOfUnit());
		AbstractMynNumConvertor convertor = new DefaultConvertor();
		paramMap.put("premiumInWord", convertor.getNameWithDecimal(payment.getTotalAmount()));

		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		for (int i = 0; i <= 1; i++) {
			paramMap.put("isCopy", i == 1 ? true : false);
			JasperPrint jprint = new JasperPrint();
			jprint = JasperFactory.generateJasperPrint(paramMap, JasperTemplate.TRAVEL_CASH_RECEPT, new JREmptyDataSource());
			jprintList.add(jprint);
		}
		return jprintList;
	}

	/** Special Travel Issue Letter */
	public static List<JasperPrint> generateSpecialTravelCertificate(TravelProposal travelProposal) {
		int unit = travelProposal.getTotalUnit();
		double totolSumInsured = travelProposal.getTotalSumInsured();
		String agentName = travelProposal.getAgentName();
		String agentCode = travelProposal.getAgentLiscenseNo();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("agentName", agentName);
		params.put("agentCode", agentCode);
		params.put("policyNo", travelProposal.getPolicyNo());
		params.put("date", new Date());
		params.put("travellerName", travelProposal.getCustomerName());
		params.put("idNo", "-");
		params.put("fatherName", "-");
		params.put("address", travelProposal.getCustomerAddress());
		String phoneNo = travelProposal.getPhoneNo();
		params.put("phoneNo", (phoneNo != null && !phoneNo.isEmpty()) ? phoneNo : "-");
		params.put("unit", unit);
		params.put("sumInsured", Utils.formattedCurrency(totolSumInsured));
		params.put("premium", travelProposal.getPremium());
		params.put("travelPlace", travelProposal.getTourName());
		params.put("departureDay", travelProposal.getFromDate());
		params.put("arrivalDay", travelProposal.getToDate());
		params.put("usageOfVehicle", travelProposal.getExpressName());
		params.put("name", "-");
		params.put("fullIdNo", "-");
		params.put("fatherName", "-");
		params.put("beneficiarieaddress", "-");
		params.put("phone", "-");
		params.put("relationship", "-");
		// params.put("invoice4Fly", ApplicationSetting.getLetterHead4Fly());
		// params.put("branchAddress", travelProposal.getBranch().getAddress());
		// params.put("branchPhNo",
		// "Office : " + travelProposal.getBranch().getContentInfo().getPhone()
		// + ", " + "Call Center : " +
		// travelProposal.getBranch().getContentInfo().getMobile());
		// params.put("branchFax", "Fax : " +
		// travelProposal.getBranch().getContentInfo().getFax());
		// params.put("branchEmail", "E-mail : " +
		// travelProposal.getBranch().getContentInfo().getEmail());
		// params.put("branchWebSite", "Website : " +
		// travelProposal.getBranch().getWebsiteUrl());
		List<JasperPrint> jprintList = new ArrayList<JasperPrint>();
		JasperPrint jprint = new JasperPrint();
		jprint = JasperFactory.generateJasperPrint(params, JasperTemplate.SPECIAL_TRAVEL_CERTIFICATE, new JREmptyDataSource());
		jprintList.add(jprint);
		JasperPrint termAndCPrint = JasperFactory.generateJasperPrint(params, JasperTemplate.SPECIAL_TRAVEL_TERM_CONDITION, new JREmptyDataSource());
		jprintList.add(termAndCPrint);
		return jprintList;
	}
}
