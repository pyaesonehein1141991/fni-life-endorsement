package org.ace.insurance.travel.personTravel.proposal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.common.AgentCommissionEntryType;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProposalStatus;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.SetUpIDConfig;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.payment.AgentCommission;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.TlfData;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IAgentCommissionService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.payment.service.interfaces.ITlfDataProcessor;
import org.ace.insurance.payment.service.interfaces.ITlfProcessor;
import org.ace.insurance.product.PremiumCalData;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IPremiumCalculatorService;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.city.persistence.interfaces.ICityDAO;
import org.ace.insurance.system.common.country.persistence.interfaces.ICountryDAO;
import org.ace.insurance.system.common.customer.persistence.interfaces.ICustomerDAO;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.system.common.organization.persistence.interfaces.IOrganizationDAO;
import org.ace.insurance.system.common.township.persistence.interfaces.ITownshipDAO;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.PTPL001;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalInfo;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalKeyfactorValue;
import org.ace.insurance.travel.personTravel.proposal.ProposalTraveller;
import org.ace.insurance.travel.personTravel.proposal.persistence.interfaces.IPersonTravelProposalDAO;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service(value = "PersonTravelProposalService")
public class PersonTravelProposalService extends BaseService implements IPersonTravelProposalService{
	
	@Resource(name = "PersonTravelProposalDAO")
	IPersonTravelProposalDAO personTravelProposalDAO;

	@Resource(name = "ProductService")
	private IProductService productService;

	@Resource(name = "WorkFlowService")
	public IWorkFlowService workFlowService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PersonTravelPolicyService")
	private IPersonTravelPolicyService personTravelPolicyService;

	@Resource(name = "CustomerDAO")
	private ICustomerDAO customerDAO;

	@Resource(name = "OrganizationDAO")
	private IOrganizationDAO organizationDAO;

	@Resource(name = "TownshipDAO")
	private ITownshipDAO townshipDAO;

	@Resource(name = "CityDAO")
	private ICityDAO cityDAO;

	@Resource(name = "CountryDAO")
	private ICountryDAO countryDAO;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "PremiumCalculatorService")
	private IPremiumCalculatorService premiumCalculatorService;

	@Resource(name = "TlfDataProcessor")
	private ITlfDataProcessor tlfDataProcessor;

	@Resource(name = "TlfProcessor")
	private ITlfProcessor tlfProcessor;

	@Resource(name = "AgentCommissionService")
	private IAgentCommissionService agentCommissionService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO) {
		try {

			String proposalNo = customIDGenerator.getCustomNextId(SystemConstants.PERSON_TRAVEL_PROPOSAL_NO, null);
			personTravelProposal.setProposalNo(proposalNo);
			boolean isUnder100Travel = SetUpIDConfig.isUnder100MileTravelInsurance(personTravelProposal.getProduct());
			double premiumRate = 0.00;
			double premium = 0.00;
			for (ProposalTraveller proposalTraveller : personTravelProposal.getPersonTravelInfo().getProposalTravellerList()) {
				premiumRate = personTravelProposal.getPersonTravelInfo().getPremiumRate();
				if (isUnder100Travel) {
					double unit = personTravelProposal.getPersonTravelInfo().getTotalUnit() / personTravelProposal.getPersonTravelInfo().getNoOfPassenger();
					premium = premiumRate * unit;
					proposalTraveller.setUnit(unit);
				} else {
					premium = premiumRate * proposalTraveller.getUnit();
				}
				proposalTraveller.setPremium(premium);
				proposalTraveller.setBasicTermPremium(premium);
			}
			personTravelProposalDAO.insert(personTravelProposal);
			workFlowDTO.setReferenceNo(personTravelProposal.getId());
			workFlowService.addNewWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new PersonTravelProposal", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO) {
		try {
			if (workFlowDTO != null)
				workFlowDTOService.updateWorkFlow(workFlowDTO);
			for (ProposalTraveller proposalTraveller : personTravelProposal.getPersonTravelInfo().getProposalTravellerList()) {
				double premiumRate = personTravelProposal.getPersonTravelInfo().getPremiumRate();
				double premium = premiumRate * proposalTraveller.getUnit();
				proposalTraveller.setPremium(premium);
				proposalTraveller.setBasicTermPremium(premium);
			}
			personTravelProposalDAO.update(personTravelProposal);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update PersonTravelProposal", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePersonTravelProposal(PersonTravelProposal personTravelProposal) {
		try {
			personTravelProposalDAO.delete(personTravelProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete PersonTravelProposal", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelProposal> findAllPersonTravelProposal() {
		List<PersonTravelProposal> result = null;
		try {
			result = personTravelProposalDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of PersonTravelProposal", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelProposal findPersonTravelProposalById(String id) {
		PersonTravelProposal result = null;
		try {
			result = personTravelProposalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelProposal by Id (id = " + id + ")", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelProposal calculatePremium(PersonTravelProposal personTravelProposal) {
		try {
			PersonTravelProposalInfo travelProposalInfo = personTravelProposal.getPersonTravelInfo();
			Map<KeyFactor, String> keyfatorValueMap = new HashMap<KeyFactor, String>();
			for (PersonTravelProposalKeyfactorValue ptkfv : travelProposalInfo.getTravelProposalKeyfactorValueList()) {
				keyfatorValueMap.put(ptkfv.getKeyfactor(), ptkfv.getValue());
			}
			Product product = personTravelProposal.getProduct();
			double premiumRate = premiumCalculatorService.findPremiumRate(keyfatorValueMap, product);
			double premium = premiumCalculatorService.calulatePremium(premiumRate, product, new PremiumCalData(null, null, null, travelProposalInfo.getTotalUnit()));
			if (SetUpIDConfig.isUnder100MileTravelInsurance(product)) {
				premium = premium * travelProposalInfo.getNoOfPassenger();
				travelProposalInfo.setTotalUnit(travelProposalInfo.getNoOfPassenger());
			}
			travelProposalInfo.setSumInsured(travelProposalInfo.getTotalUnit() * product.getSumInsuredPerUnit());
			travelProposalInfo.setPremiumRate(premiumRate);
			travelProposalInfo.setPremium(Utils.round2Decimal(premium));
			// if Travel's payment type is Lumpsum
			double termPremium = premium;
			personTravelProposal.getPersonTravelInfo().setBasicTermPremium(Utils.round2Decimal(termPremium));
			personTravelProposal.getPersonTravelInfo().setPaymentTerm(1);
			// calculateHomePremium(personTravelProposal);
		} catch (SystemException e) {
			throw e;
		}
		return personTravelProposal;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, List<Payment> paymentList, Branch userBranch) {
		try {
			/* Currency Rate */
			double rate = 1.0;
			PersonTravelPolicy policy = personTravelPolicyService.activatePersonTravelPolicy(personTravelProposal.getId());
			String policyNo = policy.getPolicyNo();
			Map<String, String> policyNoList = new HashMap<String, String>();
			policyNoList.put(policy.getId(), policyNo);
			paymentService.activatePayment(paymentList, policyNo, rate);
			workFlowDTOService.updateWorkFlow(workFlowDTO);

			/* get Receipt No. */
			String receiptNo = null;
			if (paymentList != null && !paymentList.isEmpty()) {
				receiptNo = paymentList.get(0).getReceiptNo();
			}
			List<AgentCommission> agentCommissionList = new ArrayList<AgentCommission>();
			/* get agent commission of each policy */
			if (personTravelProposal.getAgent() != null) {
				Product product = personTravelProposal.getProduct();
				double netPremium = paymentList.get(0).getNetAgentComPremium();
				int lifeCount = 0;
				double percent = product.getFirstCommission();
				if (percent > 0) {
					double commission =  Utils.getPercentOf(percent, netPremium);
					agentCommissionList.add(new AgentCommission(personTravelProposal.getId(),  PolicyReferenceType.TRAVEL_POLICY, policyNo,personTravelProposal.getCustomer(),personTravelProposal.getOrganization(),
							personTravelProposal.getBranch(), personTravelProposal.getAgent(), commission, new Date(), receiptNo, netPremium, commission, AgentCommissionEntryType.UNDERWRITING, rate,
							(rate * commission), product.getCurrency(), (rate * netPremium)));
					agentCommissionService.addNewAgentCommisssion(agentCommissionList);
				
				}
			}

			/* TLF */
			List<TlfData> dataList = new ArrayList<>();
			/* Retrieve Payment from list by policy Id */
			 Payment payment = paymentList.stream().filter(p -> policy.getId().equals(p.getReferenceNo())).findAny().orElse(null);
			/* Load Account Code */
			TlfData tlfData = tlfDataProcessor.getInstance(PolicyReferenceType.TRAVEL_POLICY, personTravelProposal, payment, agentCommissionList, false);
			dataList.add(tlfData);
			tlfProcessor.handleTlfProcess(dataList,userBranch);

			/* update ActivePolicy Count in CustomerTable */
			if (personTravelProposal.getCustomer() != null) {
				int activePolicyCount = personTravelProposal.getCustomer().getActivePolicy();
				customerDAO.updateActivePolicy(activePolicyCount, personTravelProposal.getCustomerId());
				if (personTravelProposal.getCustomer().getActivedDate() == null) {
					customerDAO.updateActivedPolicyDate(new Date(), personTravelProposal.getCustomerId());
				}
			}
			if (personTravelProposal.getOrganization() != null) {
				int activePolicyCount = personTravelProposal.getOrganization().getActivePolicy();
				organizationDAO.updateActivePolicy(activePolicyCount, personTravelProposal.getCustomerId());
				if (personTravelProposal.getOrganization().getActivedDate() == null) {
					organizationDAO.updateActivedPolicyDate(new Date(), personTravelProposal.getCustomerId());
				}
			}

			/* workflow */
			personTravelProposalDAO.updateStatus(RequestStatus.FINISHED.name(), personTravelProposal.getId());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a PersonTravelProposal ID : " + personTravelProposal.getId(), e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO) throws SystemException {
		try {
			workFlowDTOService.addWorkFlowHistory(workFlowDTO);
			workFlowService.deleteWorkFlowByRefNo(personTravelProposal.getId());
			personTravelProposalDAO.updateProposalStatus(ProposalStatus.DENY, personTravelProposal.getId());

			// workFlowService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to reject Person Travel Proposal", e);
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PTPL001> findPersonTravelDTOByCriteria(EnquiryCriteria criteria) {
		List<PTPL001> result = null;
		try {
			result = personTravelProposalDAO.findByEnquiryCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  PersonTravelProposal by Criteria ", e);
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmPersonTravelProposal(PersonTravelProposal personTravelProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO) throws SystemException {
		List<Payment> paymentList = new ArrayList<Payment>();
		try {

			PersonTravelPolicy policy = personTravelPolicyService.findPersonTravelPolicyByProposalId(personTravelProposal.getId());
			if (policy != null) {
				throw new SystemException(ErrorCode.PROPOSAL_ALREADY_CONFIRMED, " Proposal is already confirmed.");
			}
			// create PersonTravelPolicy
			PersonTravelPolicy personTravelPolicy = new PersonTravelPolicy(personTravelProposal);

			double rate = 1.0;
			Payment payment = new Payment();
			payment.setRate(rate);
			String invoiceNo = customIDGenerator.getCustomNextId(SystemConstants.PERSON_TRAVEL_INVOICE_NO, null);
			payment.setInvoiceNo(invoiceNo);
			payment.setBank(paymentDTO.getBank());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(PolicyReferenceType.TRAVEL_POLICY);
			payment.setStampFees(paymentDTO.getStampFees());
			payment.setServicesCharges(paymentDTO.getServicesCharges());
			payment.setSpecialDiscount(paymentDTO.getDiscountAmount());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setBasicPremium(personTravelProposal.getPersonTravelInfo().getPremium());
			payment.setAmount(payment.getNetPremium());
			payment.setCurrency(personTravelProposal.getProduct().getCurrency());
			payment.setSalesPoints(personTravelProposal.getSalesPoints());
			//payment.setBranch(personTravelProposal.getBranch());
			personTravelProposalDAO.update(personTravelProposal);
			personTravelPolicyService.addNewPersonTravelPolicy(personTravelPolicy);
			payment.setReferenceNo(personTravelPolicy.getId());
			paymentList.add(payment);
			for (Payment p : paymentList) {
				p = paymentDAO.insert(p);
			}
			workFlowService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm Person Travel Proposal)", e);
		}
		return paymentList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void issuePersonTravelProposal(PersonTravelProposal personTravelProposal) {
		try {
			workFlowDTOService.deleteWorkFlowByRefNo(personTravelProposal.getId());
			personTravelProposalDAO.updateCompleteStatus(true, personTravelProposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to issue a Person Travel Proposal.", e);
		}
	}

	@Override
	public void deletePayment(PersonTravelPolicy personTravelPolicy, WorkFlowDTO workFlowDTO) {
		// TODO Auto-generated method stub
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			personTravelProposalDAO.update(personTravelPolicy.getPersonTravelProposal());
			List<Payment> paymentList = paymentService.findByPolicy(personTravelPolicy.getId());
			paymentService.deletePayments(paymentList);
			personTravelProposalDAO.deletePayment(personTravelPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a MarineHullPolicy", e);
		}
	}
	
}
