package org.ace.insurance.travel.expressTravel.service;

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
import org.ace.insurance.common.ReferenceType;
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
import org.ace.insurance.product.Product;
//import org.ace.insurance.report.travel.TravelDailyIncomeReport;
//import org.ace.insurance.report.travel.TravelMonthlyIncomeReport;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.travel.expressTravel.TPL001;
import org.ace.insurance.travel.expressTravel.Tour;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.persistence.interfaces.ITravelProposalDAO;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
//import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.idgen.service.interfaces.ICustomIDGenerator;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "TravelProposalService")
public class TravelProposalService extends BaseService implements ITravelProposalService {

	@Resource(name = "TravelProposalDAO")
	private ITravelProposalDAO travelProposalDAO;

	@Resource(name = "CustomIDGenerator")
	private ICustomIDGenerator customIDGenerator;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "TlfDataProcessor")
	private ITlfDataProcessor tlfDataProcessor;

	@Resource(name = "TlfProcessor")
	private ITlfProcessor tlfProcessor;

	@Resource(name = "AgentCommissionService")
	private IAgentCommissionService agentCommissionService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO) {
		try {

			setProposalNo(travelProposal);
			travelProposalDAO.insert(travelProposal);
			workFlowDTO.setReferenceNo(travelProposal.getId());
			workFlowDTO.setReferenceType(ReferenceType.SPECIAL_TRAVEL);
			workFlowDTOService.addNewWorkFlow(workFlowDTO);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new TravelProposal", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO) {
		try {
			travelProposalDAO.update(travelProposal);
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a TravelProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteTravelProposal(TravelProposal travelProposal) {
		try {
			travelProposalDAO.delete(travelProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a TravelProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public TravelProposal findTravelProposalById(String id) {
		TravelProposal result = null;
		try {
			result = travelProposalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a TravelProposal (ID : " + id + ")", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelProposal> findAllTravelProposal() {
		List<TravelProposal> result = null;
		try {
			result = travelProposalDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of TravelProposal)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TPL001> findTravelProposalByEnquiryCriteria(EnquiryCriteria criteria) {
		List<TPL001> result = null;
		try {
			result = travelProposalDAO.findTravelProposalByEnquiryCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of TravelProposal)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDTO) {
		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			paymentList = paymentService.findByPolicy(travelProposal.getId());

			if (paymentList.size() > 0 && paymentList != null) {
				throw new SystemException(ErrorCode.PROPOSAL_ALREADY_CONFIRMED, " Proposal is already confirmed.");
			}

			Payment payment = new Payment();
			String invoiceNo = customIDGenerator.getCustomNextId(SystemConstants.SPECIAL_TRAVEL_INVOICE_NO, null);
			payment.setInvoiceNo(invoiceNo);
			payment.setBank(paymentDTO.getBank());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setReferenceType(PolicyReferenceType.SPECIAL_TRAVEL_PROPOSAL);
			payment.setStampFees(paymentDTO.getStampFees());
			payment.setServicesCharges(paymentDTO.getServicesCharges());
			payment.setSpecialDiscount(paymentDTO.getDiscountAmount());
			payment.setConfirmDate(new Date());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setReferenceNo(travelProposal.getId());
			payment.setBasicPremium(travelProposal.getTotalNetPremium());
			payment.setCurrency(travelProposal.getExpressList().get(0).getProduct().getCurrency());
			payment.setSalesPoints(travelProposal.getSalesPoints());
			payment.setAmount(payment.getNetPremium());
			paymentList.add(payment);

			for (Payment p : paymentList) {
				p = paymentDAO.insert(p);
			}

			travelProposalDAO.update(travelProposal);
			workFlowDTOService.updateWorkFlow(workFlowDTO);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to co confirm TravelProposal)", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectTravelProposal(TravelProposal travelProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.addWorkFlowHistory(workFlowDTO);
			workFlowDTOService.deleteWorkFlowByRefNo(travelProposal.getId());
			travelProposalDAO.updateProposalStatus(travelProposal.getId());

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to reject a TravelProposal", e);
		}
	}

	private void setProposalNo(TravelProposal travelProposal) {
		String proposalNo = null;
		proposalNo = customIDGenerator.getCustomNextId(SystemConstants.SPECIAL_TRAVEL_PROPOSAL_NO, null);
		travelProposal.setProposalNo(proposalNo);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelExpress> findExpressList() {
		List<TravelExpress> resultList = null;
		try {
			resultList = travelProposalDAO.findExpressList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Express)", e);
		}
		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateExpress(TravelExpress travelExpress) {
		try {
			travelProposalDAO.updateExpress(travelExpress);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update Travel Express", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void paymentTravelProposal(TravelProposal proposal, WorkFlowDTO workflow, List<Payment> paymentList, Branch userBranch) {
		try {
			double rate = 1.0;
			String policyNo = customIDGenerator.getCustomNextId(SystemConstants.SPECIAL_TRAVEL_POLICY_NO, null);
			proposal.setPolicyNo(policyNo);

			Map<String, String> policyNoList = new HashMap<String, String>();
			policyNoList.put(proposal.getId(), policyNo);
			travelProposalDAO.update(proposal);
			paymentService.activatePayment(paymentList, policyNo, rate);
			workFlowDTOService.updateWorkFlow(workflow);

			/* Agent Commission */
			List<AgentCommission> agentCommissionList = new ArrayList<AgentCommission>();
			if (proposal.getAgent() != null) {
				Payment payment = null;
				/* Retrieve payment from list by policy Id */
				payment = paymentList.stream().filter(p -> proposal.getId().equals(p.getReferenceNo())).findAny().orElse(null);
				String receiptNo = payment.getReceiptNo();
				double netPremium = payment.getNetAgentComPremium();
				Product product = proposal.getExpressList().get(0).getProduct();
				
				int lifeCount = 0;
				double percent = product.getFirstCommission();
				if (percent > 0) {
					double commission =  Utils.getPercentOf(percent, netPremium);
					agentCommissionList.add(new AgentCommission(proposal.getId(),  PolicyReferenceType.SPECIAL_TRAVEL_PROPOSAL, policyNo,proposal.getCustomer(),proposal.getOrganization(),
							 proposal.getBranch(), proposal.getAgent(), commission, new Date(), receiptNo, netPremium, commission, AgentCommissionEntryType.UNDERWRITING, rate,
							(rate * commission), product.getCurrency(), (rate * netPremium)));
					agentCommissionService.addNewAgentCommisssion(agentCommissionList);
				}
			}

			/* TLF */
			List<TlfData> dataList = new ArrayList<>();
			TlfData tlfData = null;
			Payment payment = null;
			/* Retrieve Payment from list by policy Id */
			payment = paymentList.stream().filter(p -> proposal.getId().equals(p.getReferenceNo())).findAny().orElse(null);
			/* Load Account Code */
			tlfData = tlfDataProcessor.getInstance(PolicyReferenceType.SPECIAL_TRAVEL_PROPOSAL, proposal, payment, agentCommissionList, false);
			//tlfData.setFromDate(proposal.getFromDate());
			//tlfData.setToDate(proposal.getToDate());
			dataList.add(tlfData);
			tlfProcessor.handleTlfProcess(dataList,userBranch);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to payment a TravelProposal ID : " + proposal.getId(), e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateTour(Tour tour) {
		try {
			travelProposalDAO.updateTour(tour);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new Tour", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Tour> findTourList() {
		List<Tour> resultList = null;
		try {
			resultList = travelProposalDAO.findTourList();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of Tour)", e);
		}
		return resultList;
	}

	// Report
//	@Transactional(propagation = Propagation.REQUIRED)
//	public List<TravelDailyIncomeReport> findExpressDetailByProposalSubmittedDate(TravelReportCriteria criteria) {
//		List<TravelDailyIncomeReport> resultList = null;
//		try {
//			resultList = travelProposalDAO.findExpressDetailByTravelReportCritera(criteria);
//		} catch (DAOException e) {
//			throw new SystemException(e.getErrorCode(), "Faield to find ExpressDetail by TravelProposalCriteria.)", e);
//		}
//		return resultList;
//	}
//
//	@Transactional(propagation = Propagation.REQUIRED)
//	public List<TravelMonthlyIncomeReport> findTravelMonthlyIncome(TravelReportCriteria criteria) {
//		List<TravelMonthlyIncomeReport> resultList = null;
//		try {
//			resultList = travelProposalDAO.findTravelMonthlyIncome(criteria);
//		} catch (DAOException e) {
//			throw new SystemException(e.getErrorCode(), "Faield to find TravelMonthlyIncomeReport by TravelProposalCriteria.)", e);
//		}
//		return resultList;
//	}

	@Transactional(propagation = Propagation.REQUIRED)
	public double findPremiumRateByProductID(String productId) {
		double result = 0;
		try {
			result = travelProposalDAO.findPremiumRateByProductId(productId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "faield to find PremiumRate");
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void issueTravelProposal(TravelProposal travelProposal) {
		try {
			workFlowDTOService.deleteWorkFlowByRefNo(travelProposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to issue a TravelProposal (ID : " + travelProposal.getId() + ")", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelProposal> findPOByProposalId(String receiptNo) {
		List<TravelProposal> result = null;
		try {
			result = travelProposalDAO.findPaymentOrderByProposalId(receiptNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find  Travel Proposal payment order by Receipt No ");
		}
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePayment(TravelProposal travelPolicy, WorkFlowDTO workFlowDTO) {
		// TODO Auto-generated method stub
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			travelProposalDAO.update(travelPolicy);
			List<Payment> paymentList = paymentService.findByPolicy(travelPolicy.getId());
			paymentService.deletePayments(paymentList);
			// travelProposalDAO.deletePayment(travelPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a OversersCargoPolicy", e);
		}
	}

}
