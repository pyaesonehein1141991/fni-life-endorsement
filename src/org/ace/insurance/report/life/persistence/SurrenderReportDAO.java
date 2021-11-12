package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.report.life.SurrenderReportCriteria;
import org.ace.insurance.report.life.SurrenderReportDTO;
import org.ace.insurance.report.life.persistence.interfaces.ISurrenderReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("SurrenderReportDAO")
public class SurrenderReportDAO extends BasicDAO implements ISurrenderReportDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<SurrenderReportDTO> find(SurrenderReportCriteria surrenderCriteria) throws DAOException {
		List<SurrenderReportDTO> result = new ArrayList<SurrenderReportDTO>();

		// LocalDate localStartDate = LocalDate.of(criteria.getYear(),
		// criteria.getMonth() + 1, 1);
		// LocalDate localEndDate =
		// localStartDate.withDayOfMonth(localStartDate.lengthOfMonth());
		// Date startDate =
		// Date.from(localStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		// Date endDate =
		// Date.from(localEndDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		Date startDate = surrenderCriteria.getStartDate();
		Date endDate = surrenderCriteria.getEndDate();

		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + SurrenderReportDTO.class.getName());
			buffer.append(
					"(c.insruedPersonName,c.policyNo,c.fromDateToDate,c.paymentType,c.sumInsured,c.basictermPremium,c.surrenderAmount,c.agentName,c.policyTerm,c.age,c.submittedDate,c.salepoint,c.dueNo,c.receiptNo)");
			buffer.append(" FROM SurrenderReportView c ");
			buffer.append(" WHERE 1=1 ");
			buffer.append("AND c.submittedDate >= :startDate ");
			buffer.append("AND c.submittedDate<=:endDate ");
			if (surrenderCriteria.getPolicyNo() != null && !surrenderCriteria.getPolicyNo().isEmpty()) {
				buffer.append(" And c.policyNo= :policyNo");
			}
			/*
			 * if (!mktforlifeReportCriteria.getProductIdList().isEmpty()) {
			 * buffer.append(" AND c.productId IN :productIdList"); }
			 */
			Query query = em.createQuery(buffer.toString());

			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			if (surrenderCriteria.getPolicyNo() != null) {
				query.setParameter("policyNo", surrenderCriteria.getPolicyNo());
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
