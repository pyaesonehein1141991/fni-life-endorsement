package org.ace.insurance.life.surrender.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.claimaccept.service.interfaces.IClaimAcceptedInfoService;
import org.ace.insurance.claimproduct.service.interfaces.IClaimProductService;
import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.InsuredPersonBeneficiaries;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.insurance.life.proposal.persistence.interfaces.ILifeProposalDAO;
import org.ace.insurance.life.surrender.LifeSurrenderKeyFactor;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.life.surrender.persistence.interfaces.ILifeSurrenderProposalDAO;
import org.ace.insurance.life.surrender.service.interfaces.ILifeSurrenderProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.persistence.interfacs.IPaymentDAO;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/***************************************************************************************
 * @author PPA-00136
 * @Date 2016-03-03
 * @Version 1.0
 * @Purpose This class serves as the Data Access Service For Life Surrender
 *          Proposal
 * 
 ***************************************************************************************/
@Service("LifeSurrenderProposalService")
public class LifeSurrenderProposalService extends BaseService implements ILifeSurrenderProposalService {

	@Resource(name = "LifeSurrenderProposalDAO")
	private ILifeSurrenderProposalDAO lifeSurrenderProposalDAO;

	@Resource(name = "WorkFlowService")
	private IWorkFlowService workFlowDTOService;

	@Resource(name = "PaymentDAO")
	private IPaymentDAO paymentDAO;

	@Resource(name = "ClaimAcceptedInfoService")
	private IClaimAcceptedInfoService claimAcceptedInfoService;

	@Resource(name = "PaymentService")
	private IPaymentService paymentService;

	@Resource(name = "ClaimProductService")
	private IClaimProductService claimProductService;

	@Resource(name = "LifePolicyService")
	private ILifePolicyService lifePolicyService;

	@Resource(name = "LifeProposalDAO")
	private ILifeProposalDAO lifeProposalDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public LifeSurrenderProposal addNewLifeSurrenderProposal(LifeSurrenderProposal proposal, WorkFlowDTO workFlowDTO) {
		try {
			//update LifeProposalType
			LifePolicy lifePolicy = lifePolicyService.findLifePolicyByPolicyNo(proposal.getPolicyNo());
			LifeProposal lifeproposal = lifeProposalDAO.findById(lifePolicy.getLifeProposal().getId());
			lifeproposal.setProposalType(ProposalType.SURRENDER);
			lifeProposalDAO.update(lifeproposal);

			Map<KeyFactor, String> keyfatorValueMap = new HashMap<KeyFactor, String>();
			String proposalNo = null;
			proposalNo = customIDGenerator.getNextId(SystemConstants.LIFE_SURRENDER_PROPOSAL_NO, null);
			proposal.setProposalNo(proposalNo);
			for (LifeSurrenderKeyFactor lsKeyFactor : proposal.getLifeSurrenderKeyFactors()) {
				keyfatorValueMap.put(lsKeyFactor.getKeyFactor(), lsKeyFactor.getValue());
			}

			Double surrenderAmount = claimProductService.findClaimProductRate(keyfatorValueMap, proposal.getClaimProduct(), proposal.getSumInsured());
			if (surrenderAmount != null && surrenderAmount > 0) {

				proposal.setSurrenderAmount(surrenderAmount - proposal.getLifePremium());
				proposal = lifeSurrenderProposalDAO.insert(proposal);
				lifePolicyService.updateSurrenderAndPaidUpStatus(true, lifePolicy);
				workFlowDTO.setReferenceNo(proposal.getId());
				workFlowDTOService.addNewWorkFlow(workFlowDTO);
			} else {
				throw new SystemException(ErrorCode.NO_PREMIUM_RATE, keyfatorValueMap, "There is no claim rate.");
			}

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeSurrenderProposal", e);
		}

		return proposal;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public LifeSurrenderProposal updateLifeSurrenderProposal(LifeSurrenderProposal lifeSurrenderProposal, WorkFlowDTO workFlowDTO) {
		try {
			Map<KeyFactor, String> keyfatorValueMap = new HashMap<KeyFactor, String>();
			for (LifeSurrenderKeyFactor lsKeyFactor : lifeSurrenderProposal.getLifeSurrenderKeyFactors()) {
				keyfatorValueMap.put(lsKeyFactor.getKeyFactor(), lsKeyFactor.getValue());
			}
			Double surrenderAmount = claimProductService.findClaimProductRate(keyfatorValueMap, lifeSurrenderProposal.getClaimProduct(), lifeSurrenderProposal.getSumInsured());
			if (surrenderAmount != null && surrenderAmount > 0) {
				lifeSurrenderProposal.setSurrenderAmount(surrenderAmount);
				lifeSurrenderProposal.setApproved(false);
				lifeSurrenderProposalDAO.update(lifeSurrenderProposal);
				workFlowDTO.setReferenceNo(lifeSurrenderProposal.getId());
				workFlowDTOService.updateWorkFlow(workFlowDTO);
			} else {
				throw new SystemException(ErrorCode.NO_PREMIUM_RATE, keyfatorValueMap, "There is no claim rate.");
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new LifeSurrenderProposal", e);
		}
		return lifeSurrenderProposal;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void updateLifeSurrenderProposal(LifeSurrenderProposal lifeSurrenderProposal) {
		try {
			lifeSurrenderProposalDAO.update(lifeSurrenderProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a LifeSurrenderProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteLifeSurrenderProposal(LifeSurrenderProposal lifeSurrenderProposal) {
		try {
			lifeSurrenderProposalDAO.delete(lifeSurrenderProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete a LifeSurrenderProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public List<LifeSurrenderProposal> findAllLifeSurrenderProposal() {
		List<LifeSurrenderProposal> result = null;
		try {
			result = lifeSurrenderProposalDAO.findAll();
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find all of LifeSurrenderProposal)", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public LifeSurrenderProposal findLifeSurrenderProposalById(String id) {
		LifeSurrenderProposal result = null;
		try {
			result = lifeSurrenderProposalDAO.findById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a LifeSurrenderProposal (ID : " + id + ")", e);
		}
		return result;

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeSurrenderProposal findByProposalNo(String proposalNo) {
		LifeSurrenderProposal result = null;
		try {
			result = lifeSurrenderProposalDAO.findByProposalNo(proposalNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a LifeSurrenderProposal", e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void informLifeSurrender(LifeSurrenderProposal surrenderProposal, WorkFlowDTO workflowDTO, ClaimAcceptedInfo claimAcceptedInfo) {
		try {
			workflowDTO.setReferenceNo(surrenderProposal.getId());
			workFlowDTOService.updateWorkFlow(workflowDTO);
			if (claimAcceptedInfo != null) {
				claimAcceptedInfoService.addNewClaimAcceptedInfo(claimAcceptedInfo);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to inform Life Surrender", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void approveLifeSurrenderProposal(LifeSurrenderProposal surrenderProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			lifeSurrenderProposalDAO.update(surrenderProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to approve a LifeSurrenderProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void rejectLifeSurrenderProposal(LifeSurrenderProposal surrenderProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to reject a LifeSurrenderProposal", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> confirmLifeSurrenderProposal(LifeSurrenderProposal surrenderProposal, WorkFlowDTO workFlowDTO, PaymentDTO paymentDto, Branch branch, PolicyStatus status) {
		List<Payment> paymentList = new ArrayList<Payment>();
		try {
			Product product = surrenderProposal.getLifePolicy().getInsuredPersonInfo().get(0).getProduct();
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			Payment payment = new Payment();
			payment.setConfirmDate(new Date());
			payment.setPaymentChannel(paymentDto.getPaymentChannel());
			payment.setReferenceNo(surrenderProposal.getId());
			payment.setBank(paymentDto.getBank());
			payment.setAccountBank(paymentDto.getAccountBank());
			payment.setChequeNo(paymentDto.getChequeNo());
			payment.setPoNo(paymentDto.getPoNo());
			payment.setReferenceType(PolicyReferenceType.LIFE_SURRENDER_CLAIM);
			payment.setClaimAmount(surrenderProposal.getSurrenderAmount());
			payment.setServicesCharges(paymentDto.getServicesCharges());
			payment.setPolicyNo(surrenderProposal.getPolicyNo());
			payment.setCurrency(product.getCurrency());
			payment.setSalesPoints(surrenderProposal.getLifePolicy().getSalesPoints());
			payment.setRate(1.0);
			paymentList.add(payment);
			// paymentService.prePaymentAndTlf(paymentList, surrenderProposal);
			lifePolicyService.updatePolicyStatusById(surrenderProposal.getLifePolicy().getId(), status);
			for (Payment p : paymentList) {
				p = paymentDAO.insert(p);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to confirm LifeSurrenderProposal", e);
		}
		return paymentList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void payLifeSurrender(List<Payment> paymentList, WorkFlowDTO workFlowDTO, Branch branch, String status,LifePolicy lifePolicy,LifeSurrenderProposal lifeSurrenderProposal) {
		try {
			paymentService.activatePaymentAndTLFForSurrender(paymentList, null, branch,lifePolicy,lifeSurrenderProposal);
			workFlowDTOService.updateWorkFlow(workFlowDTO);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield Life Claim's payment process", e);
		}
	}

	public void calculateSurrenderAmount(LifeSurrenderProposal lifeProposal) {

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void issueLifeSurrenderProposal(LifeSurrenderProposal proposal) {
		try {
			workFlowDTOService.deleteWorkFlowByRefNo(proposal.getId());
			// lifeSurrenderProposalDAO.updateCompleteStatus(true,
			// proposal.getId());
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to issue a LifeProposal.", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeSurrenderProposal findByLifePolicyNo(String lifePolicyNo) {
		LifeSurrenderProposal result = null;
		try {
			result = lifeSurrenderProposalDAO.findByLifePolicyNo(lifePolicyNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Failed to find a LifeSurrenderProposal", e);
		}
		return result;
	}

	@Override
	public void deletePayment(LifeSurrenderProposal surrenderProposal, WorkFlowDTO workFlowDTO) {
		try {
			workFlowDTOService.updateWorkFlow(workFlowDTO);
			//lifeProposalDAO.update(lifePolicy.getLifeProposal());
			//lifePolicyService.deleteLifePolicy(lifePolicy);
			LifePolicy lifePolicy = lifePolicyService.findLifePolicyById(surrenderProposal.getLifePolicy().getId());
			lifePolicy.setPolicyStatus(null);
			lifePolicyService.updateLifePolicyIssueDate(lifePolicy);
			List<Payment> paymentList = paymentService.findPaymentListByReferenceNoForSurrender(surrenderProposal.getId());
			paymentService.deletePayments(paymentList);
			lifeSurrenderProposalDAO.update(surrenderProposal);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to delete payment.", e);
		}

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByPolicyNoWithNotNullReceiptNo(String policyNo) {
			List<Payment> paymentTrackList = null;
			try {
				paymentTrackList = lifeSurrenderProposalDAO.findByPolicyNoWithNotNullReceiptNo(policyNo);
			} catch (DAOException e) {
				throw new SystemException(e.getErrorCode(), "Faield to find payment track for policyNo=" + policyNo, e);
			}
			return paymentTrackList;
		}
	
}
