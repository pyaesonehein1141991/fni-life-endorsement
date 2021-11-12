package org.ace.insurance.travel.expressTravel.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.travel.TravelDailyReceiptReport;
import org.ace.insurance.travel.expressTravel.persistence.interfaces.ITravelReportDAO;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("TravelReportDAO")
public class TravelReportDAO extends BasicDAO implements ITravelReportDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelDailyReceiptReport> findByCriteria(TravelReportCriteria travelDailyCriteria) throws DAOException {
		List<TravelDailyReceiptReport> result = new ArrayList<TravelDailyReceiptReport>();
		try {
			StringBuffer queryString = new StringBuffer();
			queryString.append("SELECT m from TravelDailyReceiptReport m ");
			queryString.append(" Where 1=1 ");
			if (travelDailyCriteria.getFromDate() != null) {
				queryString.append(" AND m.receiptDate >= :startDate");
			}
			if (travelDailyCriteria.getToDate() != null) {
				queryString.append(" AND m.receiptDate <= :endDate");
			}
			if (travelDailyCriteria.getBranch() != null) {
				queryString.append(" AND m.branchname = :branch");
			}
//			if (travelDailyCriteria.getPaymentBranch() != null) {
//				queryString.append(" AND m.paymentBranchName = :paymentBranch");
//			}

			queryString.append(" order by m.receiptNo");

			Query query = em.createQuery(queryString.toString());
			if (travelDailyCriteria.getFromDate() != null) {
				travelDailyCriteria.setFromDate(Utils.resetStartDate(travelDailyCriteria.getFromDate()));
				query.setParameter("startDate", travelDailyCriteria.getFromDate());
			}
			if (travelDailyCriteria.getToDate() != null) {
				travelDailyCriteria.setToDate(Utils.resetEndDate(travelDailyCriteria.getToDate()));
				query.setParameter("endDate", travelDailyCriteria.getToDate());
			}

			if (travelDailyCriteria.getBranch() != null) {
				query.setParameter("branch", travelDailyCriteria.getBranch().getName());
			}
			if (travelDailyCriteria.getPaymentBranch() != null) {
				query.setParameter("paymentBranch", travelDailyCriteria.getPaymentBranch().getName());
			}
			result = query.getResultList();
			em.flush();
		} catch (PersistenceException pe) {
			throw translate("Failed to find Travel Daily Report by criteria.", pe);

		}
		return result;
	}

}
