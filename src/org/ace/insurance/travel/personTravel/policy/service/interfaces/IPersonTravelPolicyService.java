package org.ace.insurance.travel.personTravel.policy.service.interfaces;

import java.util.List;

import org.ace.insurance.common.ReceiptNoCriteria;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.travel.personTravel.PersonTravelMonthlyIncomeReport;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.proposal.TPL001;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.report.travel.personTravel.criteria.PersonTravelCriteria;
import org.ace.java.component.SystemException;

public interface IPersonTravelPolicyService {

	public void addNewPersonTravelPolicy(PersonTravelPolicy personTravelPolicy) throws SystemException;

	public PersonTravelPolicy findPersonTravelPolicyById(String id) throws SystemException;

	public PersonTravelPolicy findPersonTravelPolicyByProposalId(String proposalId) throws SystemException;

	public PersonTravelPolicy activatePersonTravelPolicy(String lifeProposalId) throws SystemException;

	public List<Payment> findPersonTravelPolicyByReceiptNoCriteria(ReceiptNoCriteria criteria, int max) throws SystemException;

	public List<PersonTravelPolicy> findPersonTravelPolicyByPOByReceiptNo(String receiptNo);

	public List<PersonTravelMonthlyIncomeReport> findPersonTravelMonthlyIncome(PersonTravelCriteria criteria) throws SystemException;

	public String findPolicyIdByProposalId(String proposalId) throws SystemException;

	public void updatePersonTravelPolicy(PersonTravelPolicy personTravelPolicy);

	public List<TPL001> findByEnquiryCriteria(EnquiryCriteria criteria);

}
