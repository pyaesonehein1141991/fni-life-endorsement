package org.ace.insurance.travel.personTravel.proposal.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ProposalStatus;
import org.ace.insurance.common.Utils;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.proposal.PTPL001;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.persistence.interfaces.IPersonTravelProposalDAO;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository(value = "PersonTravelProposalDAO")
public class PersonTravelProposalDAO extends BasicDAO implements IPersonTravelProposalDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(PersonTravelProposal personTravelProposal) throws DAOException {
		try {
			em.persist(personTravelProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert PersonTravelInfo", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(PersonTravelProposal personTravelProposal) throws DAOException {
		try {
			em.merge(personTravelProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update PersonTravelInfo", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(PersonTravelProposal personTravelProposal) throws DAOException {
		try {
			personTravelProposal = em.merge(personTravelProposal);
			em.remove(personTravelProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to delete PersonTravelProposal", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<PersonTravelProposal> findAll() throws DAOException {
		List<PersonTravelProposal> resultList = null;
		try {
			Query q = em.createNamedQuery("PersonTravelProposal.findAll");
			resultList = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find All PersonTravelProposal", pe);
		}
		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public PersonTravelProposal findById(String id) throws DAOException {
		PersonTravelProposal result = null;
		try {
			result = em.find(PersonTravelProposal.class, id);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravelProposal By Id", pe);
		}
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStatus(String status, String id) throws DAOException {
		// TODO Auto-generated method stub

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<PTPL001> findByEnquiryCriteria(EnquiryCriteria criteria) throws DAOException {
		List<PTPL001> results = new ArrayList<PTPL001>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(
					"SELECT p.id,p.proposalNo, p.agent,p.customer,p.organization,p.branch.name,p.personTravelInfo.premium,p.personTravelInfo.totalUnit,p.personTravelInfo.noOfPassenger,p.paymentType.name,p.submittedDate");
			buffer.append(" FROM PersonTravelProposal p WHERE p.proposalNo IS NOT NULL");
			if (criteria.getStartDate() != null) {
				buffer.append(" AND p.submittedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				buffer.append(" AND p.submittedDate <= :endDate");
			}
			if (criteria.getCustomer() != null) {
				buffer.append(" AND p.customer.id = :customerId");
			}
			if (criteria.getOrganization() != null) {
				buffer.append(" AND p.organization.id = :organizationId");
			}
			if (criteria.getBranch() != null) {
				buffer.append(" AND p.branch.id = :branchId");
			} else if (!criteria.getAccessibleBranchIdList().isEmpty()) {
				buffer.append(" AND p.branch.id IN :accessibleBranchIdList");
			}
			if (criteria.getProduct() != null) {
				buffer.append(" AND p.product.id = :productId");
			}
			if (!criteria.getProposalNo().isEmpty()) {
				buffer.append(" AND p.proposalNo LIKE :proposalNo");
			}
			Query query = em.createQuery(buffer.toString());
			/* Executed query */
			if (criteria.getStartDate() != null) {
				criteria.setStartDate(Utils.resetStartDate(criteria.getStartDate()));
				query.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				criteria.setEndDate(Utils.resetEndDate(criteria.getEndDate()));
				query.setParameter("endDate", criteria.getEndDate());
			}
			if (criteria.getCustomer() != null) {
				query.setParameter("customerId", criteria.getCustomer().getId());
			}
			if (criteria.getOrganization() != null) {
				query.setParameter("organizationId", criteria.getOrganization().getId());
			}
			if (criteria.getBranch() != null) {
				query.setParameter("branchId", criteria.getBranch().getId());
			} else if (!criteria.getAccessibleBranchIdList().isEmpty()) {
				query.setParameter("accessibleBranchIdList", criteria.getAccessibleBranchIdList());
			}
			if (criteria.getProduct() != null) {
				query.setParameter("productId", criteria.getProduct().getId());
			}
			if (!criteria.getProposalNo().isEmpty()) {
				query.setParameter("proposalNo", "%" + criteria.getProposalNo() + "%");
			}
			@SuppressWarnings("unchecked")
			List<Object[]> objectList = query.getResultList();
			String id;
			String proposalNo;
			String salePersonName = null;
			String customerName;
			String branch;
			Double premium;
			double totalUnit;
			int noOfPassenger;
			String paymentType;
			Date submittedDate;
			for (Object[] objArr : objectList) {
				id = (String) objArr[0];
				proposalNo = (String) objArr[1];
				// if (objArr[2] == null) {
				// Agent a = (Agent) objArr[2];
				// salePersonName = a.getFullName();
				// }
				if (objArr[4] == null) {
					Customer c = (Customer) objArr[3];
					customerName = c.getFullName();
				} else {
					Organization org = (Organization) objArr[4];
					customerName = org.getName();
				}
				branch = (String) objArr[5];
				premium = (Double) objArr[6];
				totalUnit = (double) objArr[7];
				noOfPassenger = (int) objArr[8];
				paymentType = (String) objArr[9];
				submittedDate = (Date) objArr[10];
				results.add(new PTPL001(id, proposalNo, salePersonName, customerName, branch, premium, totalUnit, noOfPassenger, paymentType, submittedDate));
			}
			em.flush();
		} catch (

		PersistenceException pe) {
			throw translate("Failed to find PersonTravelProposal By Criteria", pe);
		}
		return results;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException {
		try {
			Query q = em.createNamedQuery("PersonTravelProposal.updateCompleteStatus");
			q.setParameter("complete", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update complete status", pe);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateProposalStatus(ProposalStatus status, String proposalId) throws DAOException {
		try {
			Query q = em.createQuery("UPDATE PersonTravelProposal c SET c.proposalStatus = :status WHERE c.id = :id");
			q.setParameter("status", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update Proposal status", pe);
		}

		// @Transactional(propagation = Propagation.REQUIRED)
		// public ProposalTraveller findByProposalTravellerId(String id) {
		// ProposalTraveller result = null;
		// try {
		// result = em.find(ProposalTraveller.class, id);
		// em.flush();
		// } catch (PersistenceException pe) {
		// throw translate("Failed to find ProposalTraveller By Id", pe);
		// }
		// return result;
		// }
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deletePayment(PersonTravelPolicy personTravelPolicy) {
		// TODO Auto-generated method stub
		try {
			personTravelPolicy = em.merge(personTravelPolicy);
			em.remove(personTravelPolicy);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update MarineHullPolicy", pe);
		}

	}

}