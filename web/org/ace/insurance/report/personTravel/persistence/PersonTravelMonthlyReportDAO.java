package org.ace.insurance.report.personTravel.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.common.MonthlyReportNewCriteria;
import org.ace.insurance.report.personTravel.persistence.interfaces.IPersonTravelMonthlyReportDAO;
import org.ace.insurance.report.personTravel.view.PersonTravelMonthlyReportView;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("PersonTravelMonthlyReportDAO")
public class PersonTravelMonthlyReportDAO extends BasicDAO implements IPersonTravelMonthlyReportDAO {

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<PersonTravelMonthlyReportView> findPersonTravelMonthlyReport(MonthlyReportNewCriteria criteria) throws DAOException {
		List<PersonTravelMonthlyReportView> resultList = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT p FROM PersonTravelMonthlyReportView p ");
			buffer.append(" WHERE p.id IS NOT NULL  ");

			if (criteria.getBranchId() != null && !criteria.getBranchId().isEmpty())
				buffer.append("AND p.branchId = :branchId ");

			if (criteria.getFromDate() != null)
				buffer.append("AND p.paymentDate >= :startDate ");

			if (criteria.getToDate() != null)
				buffer.append("AND p.paymentDate <= :endDate ");
			
			if (criteria.getProductId() != null && !criteria.getProductId().isEmpty())
				buffer.append("AND p.productId = :productId ");

			buffer.append("ORDER BY p.receiptNo");
			TypedQuery<PersonTravelMonthlyReportView> query = em.createQuery(buffer.toString(), PersonTravelMonthlyReportView.class);

			if (criteria.getBranchId() != null && !criteria.getBranchId().isEmpty())
				query.setParameter("branchId", criteria.getBranchId());

			if (criteria.getFromDate() != null)
				query.setParameter("startDate", Utils.resetStartDate(criteria.getFromDate()));

			if (criteria.getToDate() != null)
				query.setParameter("endDate", Utils.resetEndDate(criteria.getToDate()));
			
			if (criteria.getProductId() != null && !criteria.getProductId().isEmpty())
				query.setParameter("productId", criteria.getProductId());

			resultList = query.getResultList();

		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("Failed to find PersonTravel Montly Income Report ", pe);
		}
		return resultList;
	}

}
