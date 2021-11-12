package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.report.life.PaidupReportCriteria;
import org.ace.insurance.report.life.PaidupReportDTO;
import org.ace.insurance.report.life.persistence.interfaces.IPaidupReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("PaidupReportDAO")
public class PaidupReportDAO extends BasicDAO implements IPaidupReportDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<PaidupReportDTO> find(PaidupReportCriteria paidupCriteria) throws DAOException {
		List<PaidupReportDTO> result = new ArrayList<PaidupReportDTO>();

		// LocalDate localStartDate = LocalDate.of(criteria.getYear(),
		// criteria.getMonth() + 1, 1);
		// LocalDate localEndDate =
		// localStartDate.withDayOfMonth(localStartDate.lengthOfMonth());
		// Date startDate =
		// Date.from(localStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		// Date endDate =
		// Date.from(localEndDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		Date startDate = paidupCriteria.getStartDate();
		Date endDate = paidupCriteria.getEndDate();

		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + PaidupReportDTO.class.getName());
			buffer.append(
					"(c.insruedPersonName,c.policyNo,c.fromDateToDate,c.paymentType,c.sumInsured,c.basictermPremium,c.realPaidupAmount,c.agentName,c.policyTerm,c.age,c.submittedDate,c.salepoint)");
			buffer.append(" FROM PaidupReportView c ");
			buffer.append(" WHERE 1=1 ");
			buffer.append("AND c.submittedDate >= :startDate ");
			buffer.append("AND c.submittedDate<=:endDate ");
			if (paidupCriteria.getPolicyNo() != null && !paidupCriteria.getPolicyNo().isEmpty()) {
				buffer.append(" And c.policyNo= :policyNo");
			}
			/*
			 * if (!mktforlifeReportCriteria.getProductIdList().isEmpty()) {
			 * buffer.append(" AND c.productId IN :productIdList"); }
			 */
			Query query = em.createQuery(buffer.toString());

			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			if (paidupCriteria.getPolicyNo() != null) {
				query.setParameter("policyNo", paidupCriteria.getPolicyNo());
			}
			/*
			 * if (!mktforlifeReportCriteria.getProductIdList().isEmpty()) {
			 * query.setParameter("productIdList",
			 * mktforlifeReportCriteria.getProductIdList()); }
			 */
			result = query.getResultList();

		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ceoReport by apeReport.", pe);
		}
		return result;
	}

}
