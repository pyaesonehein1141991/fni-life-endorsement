package org.ace.insurance.travel.personTravel.policy.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.common.ReceiptNoCriteria;
import org.ace.insurance.common.SystemConstants;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.travel.personTravel.PersonTravelMonthlyIncomeReport;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.persistence.interfaces.IPersonTravelPolicyDAO;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.TPL001;
import org.ace.insurance.travel.personTravel.proposal.persistence.interfaces.IPersonTravelProposalDAO;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.report.travel.personTravel.criteria.PersonTravelCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.ace.java.component.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "PersonTravelPolicyService")
public class PersonTravelPolicyService extends BaseService implements IPersonTravelPolicyService {

	@Resource(name = "PersonTravelPolicyDAO")
	private IPersonTravelPolicyDAO personTravelPolicyDAO;

	@Resource(name = "PersonTravelProposalDAO")
	private IPersonTravelProposalDAO personTravelProposalDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public void addNewPersonTravelPolicy(PersonTravelPolicy personTravelPolicy) throws SystemException {
		try {
			personTravelPolicyDAO.insert(personTravelPolicy);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to add a new PersonTravelPolicy", e);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy findPersonTravelPolicyById(String id) throws SystemException {
		PersonTravelPolicy policy = null;
		try {
			policy = personTravelPolicyDAO.findPolicyById(id);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelPolicy by ID : " + id, e);
		}
		return policy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy findPersonTravelPolicyByProposalId(String proposalId) throws SystemException {
		PersonTravelPolicy policy = null;
		try {
			policy = personTravelPolicyDAO.findPolicyByProposalId(proposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelPolicy by Proposal ID : " + proposalId, e);
		}
		return policy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy activatePersonTravelPolicy(String ptProposalId) throws SystemException {
		PersonTravelPolicy personTravelPolicy = null;
		String policyNo;
		try {
			personTravelPolicy = personTravelPolicyDAO.findPolicyByProposalId(ptProposalId);
			if (personTravelPolicy != null) {
				if (personTravelPolicy.getPolicyNo() == null) {
					policyNo = customIDGenerator.getCustomNextId(SystemConstants.PERSON_TRAVEL_POLICY_NO, null);
				} else {
					policyNo = personTravelPolicy.getPolicyNo();
				}
				personTravelPolicy.setPolicyNo(policyNo);
				personTravelPolicyDAO.update(personTravelPolicy);
			}
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update a PersonTravelPolicy", e);
		}
		return personTravelPolicy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findPersonTravelPolicyByReceiptNoCriteria(ReceiptNoCriteria criteria, int max) throws SystemException {
		List<Payment> result = null;
		try {
			result = personTravelPolicyDAO.findByReceiptNoCriteria(criteria, max);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a PersonTravelPolicy (ID : " + e);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelPolicy> findPersonTravelPolicyByPOByReceiptNo(String receiptNo) {
		List<PersonTravelPolicy> result = null;
		try {
			result = personTravelPolicyDAO.findPaymentOrderByReceiptNo(receiptNo);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelPolicy payment order by Receipt No ");
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelMonthlyIncomeReport> findPersonTravelMonthlyIncome(PersonTravelCriteria criteria) throws SystemException {
		List<PersonTravelMonthlyIncomeReport> result = null;
		try {
			result = personTravelPolicyDAO.findPersonTravelMonthlyIncome(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravel Monthly Income Report by Criteria ");
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findPolicyIdByProposalId(String proposalId) throws SystemException {
		String policyId = null;
		try {
			policyId = personTravelPolicyDAO.findPolicyIdByProposalId(proposalId);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find PersonTravelPolicy ID by Proposal ID : " + proposalId, e);
		}
		return policyId;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePersonTravelPolicy(PersonTravelPolicy personTravelPolicy) {
		// TODO Auto-generated method stub
		try {
			personTravelPolicyDAO.update(personTravelPolicy);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to update PersonTravelPolicy", e);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<TPL001> findByEnquiryCriteria(EnquiryCriteria criteria) {
		List<TPL001> result = null;
		try {
			result = personTravelPolicyDAO.findByEnquiryCriteria(criteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find a PersonTravelProposal (ID : " + e);
		}
		return result;
	}

}
