package org.ace.insurance.life.endorsement.persistence;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.life.endorsement.LifeEndorseBeneficiary;
import org.ace.insurance.life.endorsement.LifeEndorseChange;
import org.ace.insurance.life.endorsement.LifeEndorseInfo;
import org.ace.insurance.life.endorsement.LifeEndorseInsuredPerson;
import org.ace.insurance.life.endorsement.persistence.interfaces.ILifeEndorsementDAO;
import org.ace.insurance.life.policyExtraAmount.PolicyExtraAmount;
import org.ace.insurance.life.proposal.InsuredPersonAttachment;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.life.proposal.LifeProposalAttachment;
import org.ace.insurance.life.proposal.LifeSurvey;
import org.ace.insurance.life.proposal.ProposalInsuredPerson;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("LifeEndorsementDAO")
public class LifeEndorsementDAO extends BasicDAO implements ILifeEndorsementDAO {

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo insert(LifeEndorseInfo lifeEndorseInfo) throws DAOException {
		try {
			em.persist(lifeEndorseInfo);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeEndorseInfo", pe);
		}
		return lifeEndorseInfo;
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteLifeEndorseInfo(LifeEndorseInfo lifeEndorseInfo) throws DAOException {
		try {
			lifeEndorseInfo = em.merge(lifeEndorseInfo);
			em.remove(lifeEndorseInfo);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeEndorseInfo", pe);
		}
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateEndorsePolicyReferenceNo(String oldPolicyId, String newPolicyId) throws DAOException {
		try {
			Query query = em.createQuery("UPDATE LifeEndorseInfo l SET l.endorsePolicyReferenceNo = :newPolicyId WHERE l.sourcePolicyReferenceNo = :oldPolicyId");
			query.setParameter("newPolicyId", newPolicyId);
			query.setParameter("oldPolicyId", oldPolicyId);
			query.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update endorsePolicyReferenceNo in LifeEndorseInfo table", pe);
		}
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo findLastLifeEndorseInfoByPolicyId(String policyId) throws DAOException {
		LifeEndorseInfo lifeEndorseInfo = null;
		try {
			String queryString = "SELECT l FROM LifeEndorseInfo l where l.endorsementDate = :lastEndorseDate and l.sourcePolicyReferenceNo = :id";
			String subQueryString = "SELECT MAX(l.endorsementDate) FROM LifeEndorseInfo l where l.sourcePolicyReferenceNo = :id";
			Query query = em.createQuery(subQueryString);
			query.setParameter("id", policyId);
			Date lastDate = (Date) query.getSingleResult();

			query = em.createQuery(queryString);
			query.setParameter("lastEndorseDate", lastDate);
			query.setParameter("id", policyId);
			lifeEndorseInfo = (LifeEndorseInfo) query.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find last LifeEndorseInfo", pe);
		}
		return lifeEndorseInfo;
	}
	
	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo findLastLifeEndorseInfoByPolicyNO(String policyId) throws DAOException {
		LifeEndorseInfo lifeEndorseInfo = null;
		try {
			String queryString = "SELECT l FROM LifeEndorseInfo l where l.endorsementDate = :lastEndorseDate and l.lifePolicyNo = :id";
			String subQueryString = "SELECT MAX(l.endorsementDate) FROM LifeEndorseInfo l where l.lifePolicyNo = :id";
			Query query = em.createQuery(subQueryString);
			query.setParameter("id", policyId);
			Date lastDate = (Date) query.getSingleResult();

			query = em.createQuery(queryString);
			query.setParameter("lastEndorseDate", lastDate);
			query.setParameter("id", policyId);
			lifeEndorseInfo = (LifeEndorseInfo) query.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find last LifeEndorseInfo", pe);
		}
		return lifeEndorseInfo;
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo findBySourcePolicyReferenceNo(String policyId) throws DAOException {
		LifeEndorseInfo lifeEndorseInfo = null;
		try {
			Query query = em.createNamedQuery("LifeEndorseInfo.findBySourcePolicyReferenceNo");
			query.setParameter("sourcePolicyReferenceNo", policyId);
			lifeEndorseInfo = (LifeEndorseInfo) query.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo", pe);
		}
		return lifeEndorseInfo;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public PolicyExtraAmount findpolicyExtaAmount(String proposalNo) throws DAOException {
		PolicyExtraAmount policyExtraAmount = null;
		try {
			Query query = em.createNamedQuery("LifeEndorseInfo.findpolicyExtaAmount");
			query.setParameter("proposalNo", proposalNo);
			policyExtraAmount = (PolicyExtraAmount) query.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo", pe);
		}
		return policyExtraAmount;
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInfo findByEndorsePolicyReferenceNo(String policyId) throws DAOException {
		LifeEndorseInfo lifeEndorseInfo = null;
		try {
			Query query = em.createNamedQuery("LifeEndorseInfo.findByEndorsePolicyReferenceNo");
			query.setParameter("endorsePolicyReferenceNo", policyId);
			lifeEndorseInfo = (LifeEndorseInfo) query.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo by EndorsePolicyReferenceNo", pe);
		}
		return lifeEndorseInfo;
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public LifeProposal insert(LifeProposal lifeProposal) throws DAOException {
		try {
			em.persist(lifeProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeProposal", pe);
		}
		return lifeProposal;
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public void insertSurvey(LifeSurvey lifeSurvey) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM LifeSurvey l WHERE l.lifeProposal.id = :lifeProposalId");
			delQuery.setParameter("lifeProposalId", lifeSurvey.getLifeProposal().getId());
			delQuery.executeUpdate();
			em.persist(lifeSurvey);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Survey", pe);
		}
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(LifeProposal lifeProposal) throws DAOException {
		try {
			em.merge(lifeProposal);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifeProposal", pe);
		}

	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addAttachment(LifeProposal lifeProposal) throws DAOException {
		try {
			Query delQuery = em.createQuery("DELETE FROM LifeProposalAttachment l WHERE l.lifeProposal.id = :lifeProposalId");
			delQuery.setParameter("lifeProposalId", lifeProposal.getId());
			delQuery.executeUpdate();
			for (LifeProposalAttachment att : lifeProposal.getAttachmentList()) {
				em.persist(att);
			}
			for (ProposalInsuredPerson pin : lifeProposal.getProposalInsuredPersonList()) {
				Query query = em.createQuery("DELETE FROM InsuredPersonAttachment l WHERE l.proposalInsuredPerson.id = :proposalInsuredPersonId");
				query.setParameter("proposalInsuredPersonId", pin.getId());
				query.executeUpdate();
			}
			for (ProposalInsuredPerson pin : lifeProposal.getProposalInsuredPersonList()) {
				for (InsuredPersonAttachment att : pin.getAttachmentList()) {
					em.persist(att);
				}
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert Attachment", pe);
		}
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuredPersonApprovalInfo(List<ProposalInsuredPerson> proposalInsuredPersonList) throws DAOException {
		try {
			for (ProposalInsuredPerson proposalInsuredPerson : proposalInsuredPersonList) {
				em.merge(proposalInsuredPerson);
			}
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to approved InsuredPerson Approbal Info", pe);
		}
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateInsuPersonMedicalStatus(List<ProposalInsuredPerson> proposalInsuredPersonList) throws DAOException {
		try {
			for (ProposalInsuredPerson insuPerson : proposalInsuredPersonList) {
				String queryString = "UPDATE ProposalInsuredPerson p SET p.clsOfHealth = :clsOfHealth WHERE p.id = :insuPersonId";
				Query query = em.createQuery(queryString);
				query.setParameter("clsOfHealth", insuPerson.getClsOfHealth());
				query.setParameter("insuPersonId", insuPerson.getId());
				query.executeUpdate();
			}
		} catch (PersistenceException pe) {
			throw translate("Failed to approved InsuredPerson Approbal Info", pe);
		}
	}

	/** used in Endorsement Service */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateCompleteStatus(boolean status, String proposalId) throws DAOException {
		try {
			Query q = em.createNamedQuery("LifeProposal.updateCompleteStatus");
			q.setParameter("complete", status);
			q.setParameter("id", proposalId);
			q.executeUpdate();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update complete status", pe);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Object[]> getListByNativeWithPolicyNo(String policyNo) throws DAOException {
		String sql = "select e.ENDORSEMENTDAET, lpo.POLICYNO, lpo.CUSTOMERID, lp.PROPOSALNO, lp.STARTDATE, lp.ENDDATE from LIFEENDORSEINFO e "
				+ "left join LIFEPOLICY lpo on lpo.POLICYNO = e.LIFEPOLICYNO " + "left join LIFEPROPOSAL lp on lp.ID = lpo.PROPOSALID " + "where e.LIFEPOLICYNO = '" + policyNo
				+ "'";
		Query query = em.createNativeQuery(sql);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public List<Object[]> getListByNativeWithCustomerId(String customerId) throws DAOException {
		String sql = "select p.IDNO, p.FIRSTNAME, p.MIDDLENAME, p.LASTNAME, p.RESIDENTADDRESS, ts.NAME from LIFEPROPOSAL_INSUREDPERSON_LINK p "
				+ "left join TOWNSHIP ts on ts.ID = p.RESIDENTTOWNSHIPID " + "where p.CUSTOMERID = '" + customerId + "'";
		Query query = em.createNativeQuery(sql);
		return query.getResultList();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseInsuredPerson findInsuredPersonByEndorsementInfoId(String policyId) throws DAOException {
		LifeEndorseInsuredPerson lifeEndorseInsuredPerson = null;
		try {
			Query query = em.createQuery("SELECT le FROM LifeEndorseInsuredPerson le WHERE le.lifeEndorseInfo.id = :endorseInfoId");
			query.setParameter("endorseInfoId", policyId);
			lifeEndorseInsuredPerson = (LifeEndorseInsuredPerson) query.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo", pe);
		}
		return lifeEndorseInsuredPerson;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeEndorseInsuredPerson> findInsuredPerson(String policyId) throws DAOException {
		List<LifeEndorseInsuredPerson> lifeEndorseInsuredPerson = null;
		try {
			Query query = em.createQuery("SELECT le FROM LifeEndorseInsuredPerson le WHERE le.lifeEndorseInfo.id = :endorseInfoId");
			query.setParameter("endorseInfoId", policyId);
			lifeEndorseInsuredPerson = (List<LifeEndorseInsuredPerson>) query.getResultList();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo", pe);
		}
		return lifeEndorseInsuredPerson;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<LifeEndorseChange> findEndorseChangbyInsuredPersonId(List<String> policyIdList) throws DAOException {
		List<LifeEndorseChange> lifeEndorseChange = null;
		try {
			Query query = em.createQuery("SELECT le FROM LifeEndorseChange le WHERE le.lifeEndorseInsuredPerson.id In :lifeendorseInsuredPersonId");
			query.setParameter("lifeendorseInsuredPersonId", policyIdList);
			lifeEndorseChange = (List<LifeEndorseChange>) query.getResultList();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo", pe);
		}
		return lifeEndorseChange;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<LifeEndorseChange> findEndorseChangOnebyOneInsuredPersonId(String policyIdList) throws DAOException {
		List<LifeEndorseChange> lifeEndorseChange = null;
		try {
			Query query = em.createQuery("SELECT le FROM LifeEndorseChange le WHERE le.lifeEndorseInsuredPerson.id In :lifeendorseInsuredPersonId");
			query.setParameter("lifeendorseInsuredPersonId", policyIdList);
			lifeEndorseChange = (List<LifeEndorseChange>) query.getResultList();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo", pe);
		}
		return lifeEndorseChange;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public LifeEndorseChange findEndorseChangOnebyInsuredPersonId(String policyId) throws DAOException {
		LifeEndorseChange lifeEndorseChange = null;
		try {
			Query query = em.createQuery("SELECT le FROM LifeEndorseChange le WHERE le.lifeEndorseInsuredPerson.id = :lifeendorseInsuredPersonId");
			query.setParameter("lifeendorseInsuredPersonId", policyId);
			lifeEndorseChange = (LifeEndorseChange) query.setMaxResults(1).getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo", pe);
		}
		return lifeEndorseChange;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public ProposalInsuredPerson findInsuredPersonId(String policyId) throws DAOException {
		ProposalInsuredPerson proposalInsuredPerson = null;
		try {
			Query query = em.createNamedQuery("ProposalInsuredPerson.findById");
			query.setParameter("id", policyId);
			proposalInsuredPerson = (ProposalInsuredPerson) query.getSingleResult();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo", pe);
		}
		return proposalInsuredPerson;
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<LifeEndorseBeneficiary> findlifeEndorseBeneficiaryById(String id) throws DAOException {
		List<LifeEndorseBeneficiary> lifeEndorseBeneficiary = null;
		try {			
			Query query = em.createQuery("SELECT le FROM LifeEndorseBeneficiary le WHERE le.insuredPersonCodeNo = :insuredPersonCodeNo");
			query.setParameter("insuredPersonCodeNo", id);
			lifeEndorseBeneficiary = (List<LifeEndorseBeneficiary>) query.getResultList();
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeEndorseInfo", pe);
		}
		return lifeEndorseBeneficiary;
	}
	
	
}
