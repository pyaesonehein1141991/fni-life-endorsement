package org.ace.insurance.report.travel.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.travel.persistence.interfaces.ITravelMonthlyReportDAO;
import org.ace.insurance.report.travel.view.TravelMonthlyReportView;
import org.ace.insurance.web.manage.report.common.MonthlyReportNewCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("TravelMonthlyReportDAO")
public class TravelMonthlyReportDAO extends BasicDAO implements ITravelMonthlyReportDAO {

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<TravelMonthlyReportView> findTravelMonthlyReport(MonthlyReportNewCriteria criteria) throws DAOException {
		List<TravelMonthlyReportView> resultList = new ArrayList<>();
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT t FROM TravelMonthlyReportView t WHERE t.id IS NOT NULL ");
			if (criteria.getBranchId() != null && !criteria.getBranchId().isEmpty())
				buffer.append("AND t.branchId = :branchId ");

			if (criteria.getFromDate() != null)
				buffer.append("AND t.paymentDate >= :startDate ");

			if (criteria.getToDate() != null)
				buffer.append("AND t.paymentDate <= :endDate ");

			buffer.append("ORDER BY t.receiptNo");
			TypedQuery<TravelMonthlyReportView> query = em.createQuery(buffer.toString(), TravelMonthlyReportView.class);
			if (criteria.getBranchId() != null && !criteria.getBranchId().isEmpty())
				query.setParameter("branchId", criteria.getBranchId());

			if (criteria.getFromDate() != null)
				query.setParameter("startDate", Utils.resetStartDate(criteria.getFromDate()));

			if (criteria.getToDate() != null)
				query.setParameter("endDate", Utils.resetEndDate(criteria.getToDate()));

			resultList = query.getResultList();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Travel Montly Income Report ", pe);
		}
		return resultList;
	}

}
