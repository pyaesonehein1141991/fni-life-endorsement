package org.ace.insurance.life.policyExtraAmount.persistence;


	import java.util.List;

	import javax.persistence.NoResultException;
	import javax.persistence.PersistenceException;
	import javax.persistence.Query;

import org.ace.insurance.life.policyExtraAmount.PolicyExtraAmount;
import org.ace.insurance.life.policyExtraAmount.persistence.interfaces.IPolicyExtraAmountDAO;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.java.component.persistence.BasicDAO;
	import org.ace.java.component.persistence.exception.DAOException;
	import org.springframework.stereotype.Repository;
	import org.springframework.transaction.annotation.Propagation;
	import org.springframework.transaction.annotation.Transactional;

	@Repository("PolicyExtraAmountDAO")
	public class PolicyExtraAmountDAO extends BasicDAO implements IPolicyExtraAmountDAO {

		@Transactional(propagation = Propagation.REQUIRED)
		public void insert(PolicyExtraAmount policyExtraAmount) throws DAOException {
			try {
				Query delQuery = em.createQuery("DELETE FROM PolicyExtraAmount a WHERE a.policyNo = :referenceNo ");
				delQuery.setParameter("referenceNo", policyExtraAmount.getPolicyNo());
				delQuery.executeUpdate();
				em.persist(policyExtraAmount);
				em.flush();
			} catch (PersistenceException pe) {
				throw translate("Failed to insert policyExtraAmount", pe);
			}
		}
	

		@Transactional(propagation = Propagation.REQUIRED)
		public void update(PolicyExtraAmount policyExtraAmount) throws DAOException {
			try {
				em.merge(policyExtraAmount);
				em.flush();
			} catch (PersistenceException pe) {
				throw translate("Failed to update policyExtraAmount", pe);
			}
		}

		@Transactional(propagation = Propagation.REQUIRED)
		public void delete(PolicyExtraAmount policyExtraAmount) throws DAOException {
			try {
				policyExtraAmount = em.merge(policyExtraAmount);
				em.remove(policyExtraAmount);
				em.flush();
			} catch (PersistenceException pe) {
				throw translate("Failed to update policyExtraAmount", pe);
			}
		}

	

	


}
