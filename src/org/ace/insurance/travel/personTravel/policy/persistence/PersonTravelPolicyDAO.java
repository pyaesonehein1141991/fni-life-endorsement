package org.ace.insurance.travel.personTravel.policy.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ReceiptNoCriteria;
import org.ace.insurance.common.Utils;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.report.travel.personTravel.PersonTravelMonthlyIncomeReport;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.persistence.interfaces.IPersonTravelPolicyDAO;
import org.ace.insurance.travel.personTravel.proposal.TPL001;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.web.manage.report.travel.personTravel.criteria.PersonTravelCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("PersonTravelPolicyDAO")
public class PersonTravelPolicyDAO extends BasicDAO implements IPersonTravelPolicyDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(PersonTravelPolicy personTravelPolicy) throws DAOException {
		try {
			em.persist(personTravelPolicy);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to insert personTravelPolicy", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(PersonTravelPolicy personTravelPolicy) throws DAOException {
		try {
			em.merge(personTravelPolicy);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to update LifePolicy", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy findPolicyById(String id) throws DAOException {
		PersonTravelPolicy policy = null;
		try {
			policy = em.find(PersonTravelPolicy.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to insert personTravelPolicy", pe);
		}
		return policy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelPolicy findPolicyByProposalId(String proposalId) throws DAOException {
		PersonTravelPolicy policy = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT p FROM PersonTravelPolicy p WHERE p.personTravelProposal.id=:proposalId");
			Query query = em.createQuery(buffer.toString());
			query.setParameter("proposalId", proposalId);
			policy = (PersonTravelPolicy) query.getSingleResult();
			em.flush();
		} catch (Exception pe) {
			return null;
			// throw translate("failed to Find PersonTravelPolicy", pe);
		}
		return policy;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public String findPolicyIdByProposalId(String proposalId) throws DAOException {
		String policyId = null;
		try {
			Query q = em.createQuery("SELECT p.id FROM PersonTravelPolicy p WHERE p.personTravelProposal.id=:proposalId");
			q.setParameter("proposalId", proposalId);
			policyId = (String) q.getSingleResult();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("failed to Find PersonTravelPolicy Id", pe);
		}
		return policyId;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Payment> findByReceiptNoCriteria(ReceiptNoCriteria criteria, int max) throws DAOException {
		List<Payment> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT p FROM PersonTravelPolicy pt, Payment p, TLF t WHERE ");
			if (criteria.getReceiptNoCriteria() != null) {
				switch (criteria.getReceiptNoCriteria()) {
					case RECEIPTNO: {
						query.append(" p.receiptNo like :receiptNo AND pt.id = p.referenceNo AND p.isPO = TRUE ");
						query.append("AND pt.id = t.referenceNo AND t.clearing = TRUE AND t.paid = FALSE ");
						break;
					}
				}
			}
			query.append("Order By p.receiptNo DESC ");
			Query q = em.createQuery(query.toString());
			q.setMaxResults(max);
			if (criteria.getReceiptNoCriteria() != null) {
				switch (criteria.getReceiptNoCriteria()) {
					case RECEIPTNO: {
						q.setParameter("receiptNo", "%" + criteria.getCriteriaValue() + "%");
						break;
					}
				}
			}
			result = q.getResultList();
			em.flush();

		} catch (PersistenceException pe) {
			throw translate("Failed to find Policy", pe);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelPolicy> findPaymentOrderByReceiptNo(String receiptNo) throws DAOException {
		List<PersonTravelPolicy> result = null;
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT DISTINCT pt FROM PersonTravelPolicy pt, Payment p, TLF t WHERE pt.id = p.referenceNo ");
			query.append("AND pt.id = t.referenceNo AND p.isPO = TRUE AND t.clearing = TRUE AND t.paid = FALSE ");
			if (!StringUtils.isBlank(receiptNo)) {
				query.append("AND p.receiptNo = :receiptNo ");
			}
			Query q = em.createQuery(query.toString());
			if (!StringUtils.isBlank(receiptNo)) {
				q.setParameter("receiptNo", receiptNo);
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravelPolicy PaymentOrder", pe);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelMonthlyIncomeReport> findPersonTravelMonthlyIncome(PersonTravelCriteria criteria) throws DAOException {
		List<PersonTravelMonthlyIncomeReport> result = new ArrayList<PersonTravelMonthlyIncomeReport>();
		try {
			StringBuffer query = new StringBuffer("SELECT p FROM PersonTravelMonthlyIncomeReport p ");
			query.append(" WHERE p.id IS NOT NULL AND p.paymentDate>=:startDate AND p.paymentDate<=:endDate");
			if (criteria.getProductId() != null) {
				query.append(" AND p.productId=:productId");
			}
			if (criteria.getBranchId() != null) {
				query.append(" AND p.branchId=:branchId");
			}
			if (criteria.getAgentId() != null) {
				query.append(" AND p.agentId=:agentId");
			}
			if (criteria.getVehicleNo() != null && !criteria.getVehicleNo().isEmpty()) {
				query.append(" AND p.vehicleNo LIKE :vehicleNo");
			}
			if (criteria.getProposalNo() != null && !criteria.getProposalNo().isEmpty()) {
				query.append(" AND p.proposalNo LIKE :proposalNo");
			}
			if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
				query.append(" AND p.policyNo LIKE :policyNo");
			}
			Query q = em.createQuery(query.toString());
			q.setParameter("startDate", Utils.getStartDate(criteria.getYear(), criteria.getMonth()));
			q.setParameter("endDate", Utils.getEndDate(criteria.getYear(), criteria.getMonth()));
			if (criteria.getAgentId() != null) {
				q.setParameter("agentId", criteria.getAgentId());
			}
			if (criteria.getProductId() != null) {
				q.setParameter("productId", criteria.getProductId());
			}
			if (criteria.getBranchId() != null) {
				q.setParameter("branchId", criteria.getBranchId());
			}
			if (criteria.getVehicleNo() != null && !criteria.getVehicleNo().isEmpty()) {
				q.setParameter("vehicleNo", "%" + criteria.getVehicleNo() + "%");
			}
			if (criteria.getProposalNo() != null && !criteria.getProposalNo().isEmpty()) {
				q.setParameter("proposalNo", "%" + criteria.getProposalNo() + "%");
			}
			if (criteria.getPolicyNo() != null && !criteria.getPolicyNo().isEmpty()) {
				q.setParameter("policyNo", "%" + criteria.getPolicyNo() + "%");
			}
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravel Monthly Income Report By Criteria", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TPL001> findByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException {
		List<Object[]> objectList = new ArrayList<Object[]>();
		Map<String, TPL001> resultMap = new HashMap<String, TPL001>();
		String id;
		String proposalNo;
		String policyNo;
		String customer;
		String organization;
		SaleChannelType saleChannelType;
		String agent;
		String branch;
		double premium;
		double totalUnit;
		double sumInsured;
		int noOfPassenger;
		Date depatureDate;
		Date arrivalDate;

		try {

			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT p.id,p.personTravelProposal.proposalNo, p.policyNo,");
			buffer.append(" CONCAT(TRIM(c.initialId),' ', TRIM(c.name.firstName), ' ', TRIM(c.name.middleName), ' ', TRIM(c.name.lastName)), ");
			buffer.append(" o.name,	p.saleChannelType,");
			buffer.append(" CONCAT(TRIM(a.initialId),' ', TRIM(a.name.firstName), ' ', TRIM(a.name.middleName), ' ', TRIM(a.name.lastName)), ");
			buffer.append(" b.name, info.premium, info.totalUnit, info.sumInsured, info.noOfPassenger, info.departureDate, info.arrivalDate ");
			buffer.append(" From PersonTravelPolicy p ");
			buffer.append("LEFT OUTER JOIN p.customer c ");
			buffer.append("LEFT OUTER JOIN p.organization o ");
			buffer.append("LEFT OUTER JOIN p.agent a ");
			buffer.append("LEFT OUTER JOIN p.branch b ");
			buffer.append("LEFT OUTER JOIN p.personTravelPolicyInfo info");
			buffer.append(" where p.policyNo is not null ");
			if (criteria.getStartDate() != null) {
				buffer.append("AND info.departureDate >= :deparDateFrom ");
			}
			if (criteria.getEndDate() != null) {
				buffer.append("AND info.departureDate  <= :deparDateTo ");
			}
			if (criteria.getProduct() != null) {
				buffer.append("AND p.product.id = :productId ");
			}
			if (!StringUtils.isEmpty(criteria.getProposalNo())) {
				if (!StringUtils.isEmpty(criteria.getProposalNo().trim())) {
					buffer.append(" AND p.personTravelProposal.proposalNo LIKE :proposalNo ");
				}
			}
			if (!StringUtils.isEmpty(criteria.getPolicyNo())) {
				if (!StringUtils.isEmpty(criteria.getPolicyNo().trim())) {
					buffer.append(" AND p.policyNo LIKE :policyNo ");
				}
			}
			if (criteria.getCustomer() != null) {
				buffer.append(" AND p.customer.id = :customerId ");
			}
			if (criteria.getBranch() != null) {
				buffer.append(" AND p.branch.id = :branchId ");
			}
			if (criteria.getSaleChannelType() != null) {
				buffer.append(" AND p.saleChannelType = :saleChannelType ");
			}
			if (criteria.getAgent() != null) {
				buffer.append(" AND p.agent.id = :agentId ");
			}
			Query query = em.createQuery(buffer.toString());
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("deparDateFrom", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("deparDateTo", criteria.getEndDate());
			}
			if (criteria.getProduct() != null) {
				query.setParameter("productId", criteria.getProduct().getId());
			}
			if (!StringUtils.isEmpty(criteria.getProposalNo())) {
				if (!StringUtils.isEmpty(criteria.getProposalNo().trim())) {
					query.setParameter("proposalNo", "%" + criteria.getProposalNo().trim() + "%");
				}
			}
			if (!StringUtils.isEmpty(criteria.getPolicyNo())) {
				if (!StringUtils.isEmpty(criteria.getPolicyNo().trim())) {
					query.setParameter("policyNo", "%" + criteria.getPolicyNo().trim() + "%");
				}
			}
			if (criteria.getCustomer() != null) {
				query.setParameter("customerId", criteria.getCustomer().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			}
			if (criteria.getSaleChannelType() != null) {
				query.setParameter("saleChannelType", criteria.getSaleChannelType());
			}
			if (criteria.getAgent() != null) {
				query.setParameter("agentId", criteria.getAgent().getId());
			}

			objectList = query.getResultList();
			for (Object obj : objectList) {
				Object[] objArray = (Object[]) obj;
				id = (String) objArray[0];
				proposalNo = (String) objArray[1];
				policyNo = (String) objArray[2];

				customer = (String) objArray[3];

				organization = (String) objArray[4];
				if (organization != null) {
					customer = organization;
				}
				saleChannelType = (SaleChannelType) objArray[5];
				agent = (String) objArray[6];
				branch = (String) objArray[7];
				premium = (Double) objArray[8];
				totalUnit = (Double) objArray[9];
				sumInsured = (Double) objArray[10];
				noOfPassenger = (int) objArray[11];
				depatureDate = (Date) objArray[12];
				arrivalDate = (Date) objArray[13];

				resultMap.put(id,
						new TPL001(id, proposalNo, policyNo, customer, saleChannelType, agent, branch, premium, totalUnit, sumInsured, noOfPassenger, depatureDate, arrivalDate));

			}

			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravelPolicyInfo By Criteria", pe);
		}
		return new ArrayList<TPL001>(resultMap.values());

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TPL001> findPolicyTPL001BypolicyId(String id) {
		List<TPL001> result = null;
		try {
			String queryString = "SELECT New " + TPL001.class.getName() + "(p.policyNo, p.) FROM PersonTravelPolicy p WHERE p.personTravelpolicy.id = :id";
			Query q = em.createQuery(queryString);
			q.setParameter("id", id);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PolicyTPL001 By policyId", pe);
		}
		return result;
	}

}
