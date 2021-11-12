package org.ace.insurance.life.claim.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.ClaimInitialReporter;
import org.ace.insurance.common.IdType;
import org.ace.insurance.common.ResidentAddress;
import org.ace.insurance.life.claim.LifeClaimNotiCriteria;
import org.ace.insurance.life.claim.LifeClaimNotification;
import org.ace.insurance.life.claim.persistence.interfaces.ILifeClaimNotificationDAO;
import org.ace.insurance.medical.claim.ClaimStatus;
import org.ace.insurance.product.persistence.interfaces.IProductDAO;
import org.ace.insurance.system.common.branch.persistence.interfaces.IBranchDAO;
import org.ace.insurance.system.common.salesPoints.persistence.interfaces.ISalesPointsDAO;
import org.ace.insurance.system.common.township.persistence.interfaces.ITownshipDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("LifeClaimNotificationDAO")
public class LifeClaimNotificationDAO extends BasicDAO implements ILifeClaimNotificationDAO {
	
	@Resource(name = "BranchDAO")
	private IBranchDAO branchDAO;
	
	@Resource(name = "ProductDAO")
	private IProductDAO productDAO;
	
	@Resource(name = "SalesPointsDAO")
	private ISalesPointsDAO salesPointsDAO;
	
	@Resource(name = "TownshipDAO")
	private ITownshipDAO townshipDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public void insert(LifeClaimNotification notification) throws DAOException {
		try {
			em.persist(notification);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to insert LifeClaimNotification", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(LifeClaimNotification notification) throws DAOException {
		try {
			notification = em.merge(notification);
			em.remove(notification);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update ClaimInsuredPerson", pe);
		}

	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void update(LifeClaimNotification notification) throws DAOException {
		try {
			em.merge(notification);
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifeClaimNotification", pe);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeClaimNotification> findLifeClaimNotification() throws DAOException {
		List<LifeClaimNotification> result = null;
		try {
			Query q = em.createQuery("SELECT n FROM LifeClaimNotification n");
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifeClaimNotification", pe);
		}

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<LifeClaimNotification> findLifeClaimNotificationByCriteria(LifeClaimNotiCriteria criteria) throws DAOException {
		List<LifeClaimNotification> result = null;
		try {
			StringBuffer buffer = new StringBuffer();

			buffer.append("SELECT r FROM LifeClaimNotification r WHERE r.id IS NOT NULL");
			if (!criteria.getNotificationNo().isEmpty()) {
				buffer.append(" AND r.notificationNo LIKE :notificationNo");
			}
			if (criteria.getPolicyNo() != null) {
				buffer.append(" AND r.lifePolicyNo = :policyNo");
			}
			if (criteria.getStartDate() != null) {
				buffer.append(" AND r.reportedDate >= :startDate");
			}
			if (criteria.getEndDate() != null) {
				buffer.append(" AND r.reportedDate <= :endDate");
			}
			buffer.append(" AND r.claimStatus !=:claimStatus");
			Query q = em.createQuery(buffer.toString());

			if (!criteria.getNotificationNo().isEmpty()) {
				q.setParameter("notificationNo", "%" + criteria.getNotificationNo() + "%");
			}
			if (criteria.getPolicyNo() != null) {
				q.setParameter("policyNo", criteria.getPolicyNo());
			}
			if (criteria.getStartDate() != null) {
				q.setParameter("startDate", criteria.getStartDate());
			}
			if (criteria.getEndDate() != null) {
				q.setParameter("endDate", criteria.getEndDate());
			}
			q.setParameter("claimStatus", ClaimStatus.CLAIM_APPLIED);
			result = q.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to update LifeClaimNotification", pe);
		}

		return result;
	}

	@Override
	public LifeClaimNotification findLifeClaimNotificationByNotiNumber(String notificationNo) {
		
		LifeClaimNotification result = new LifeClaimNotification();
		List<Object[]> objectList = new ArrayList<>();
		try {
			Query query = em.createNativeQuery("SELECT * FROM LIFECLAIMNOTIFICATION t WHERE t.notificationNo = ?");
			query.setParameter(1, notificationNo);
			objectList = query.getResultList();
			
			for (Object[] object : objectList) {
				LifeClaimNotification lifeClaimNotification = new LifeClaimNotification();
				lifeClaimNotification.setId(StringUtils.contains("null", String.valueOf(object[0])) ? null : String.valueOf(object[0]));
				lifeClaimNotification.setClaimStatus(StringUtils.contains("null", String.valueOf(object[1])) ? null : ClaimStatus.valueOf(String.valueOf(object[1])));
				lifeClaimNotification.setLifePolicyNo(StringUtils.contains("null", String.valueOf(object[2])) ? null : String.valueOf(object[2]));
				lifeClaimNotification.setNotificationNo(StringUtils.contains("null", String.valueOf(object[3])) ? null : String.valueOf(object[3]));
				lifeClaimNotification.setOccuranceDate(StringUtils.contains("null", String.valueOf(object[4])) ? null :(Date) object[4]);
				lifeClaimNotification.setReportedDate(StringUtils.contains("null", String.valueOf(object[5])) ? null :(Date) object[5]);
				lifeClaimNotification.setVersion(StringUtils.contains("null", String.valueOf(object[6])) ? null :(Integer) object[6]);
				lifeClaimNotification.setBranch(StringUtils.contains("null", String.valueOf(object[19])) ? null : branchDAO.findById(String.valueOf(object[19])));
				lifeClaimNotification.setProduct(StringUtils.contains("null", String.valueOf(object[21])) ? null : productDAO.findById(String.valueOf(object[21])));
				lifeClaimNotification.setSalePoint(StringUtils.contains("null", String.valueOf(object[22])) ? null : salesPointsDAO.findById(String.valueOf(object[22])));
				
				ClaimInitialReporter reporter = new ClaimInitialReporter();
				reporter.setFatherName(StringUtils.contains("null", String.valueOf(object[7])) ? null : String.valueOf(object[7]));
				reporter.setFullIdNo(StringUtils.contains("null", String.valueOf(object[8])) ? null : String.valueOf(object[8]));
				reporter.setIdNo(StringUtils.contains("null", String.valueOf(object[9])) ? null : String.valueOf(object[9]));
				reporter.setIdType(StringUtils.contains("null", String.valueOf(object[10])) ? null : IdType.valueOf(String.valueOf(object[10])));
				reporter.setName(StringUtils.contains("null", String.valueOf(object[11])) ? null : String.valueOf(object[11]));
				reporter.setPhone(StringUtils.contains("null", String.valueOf(object[12])) ? null : String.valueOf(object[12]));
				
				ResidentAddress residentAddress = new ResidentAddress();
				residentAddress.setResidentAddress(StringUtils.contains("null", String.valueOf(object[13])) ? null : String.valueOf(object[13]));
				residentAddress.setTownship(StringUtils.contains("null", String.valueOf(object[14])) ? null : townshipDAO.findById(String.valueOf(object[14])));
				reporter.setResidentAddress(residentAddress);
				
				lifeClaimNotification.setClaimInitialReporter(reporter);
				
				result = lifeClaimNotification;
			}
			
			em.flush();
			
		} catch (NoResultException pe) {
			return null;
		} catch (PersistenceException pe) {
			throw translate("Failed to find LifeClaimNotification", pe);
		}
		return result;
	}

}
