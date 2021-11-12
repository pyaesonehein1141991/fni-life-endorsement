package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.report.life.CeoReportCriteria;
import org.ace.insurance.report.life.CeoReportDTO;
import org.ace.insurance.report.life.persistence.interfaces.IceoReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("CeoReportDAO")
public class CeoReportDAO extends BasicDAO implements IceoReportDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<CeoReportDTO> find(CeoReportCriteria criteria) throws DAOException {
		List<CeoReportDTO> result = new ArrayList<CeoReportDTO>();

		// LocalDate localStartDate = LocalDate.of(criteria.getYear(),
		// criteria.getMonth() + 1, 1);
		// LocalDate localEndDate =
		// localStartDate.withDayOfMonth(localStartDate.lengthOfMonth());
		// Date startDate =
		// Date.from(localStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		// Date endDate =
		// Date.from(localEndDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		// startDate = DateUtils.resetStartDate(startDate);
		// endDate = DateUtils.resetEndDate(endDate);

		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + CeoReportDTO.class.getName());
			buffer.append(
					"(c.id,c.policyNo,c.term,c.paymentType,c.productId,c.startDate,c.endDate,c.sumInsured,c.paymentYear,c.productName,c.salePointName,c.salePointId,c.saleChannelType,c.insuredpersonName,c.january,c.february,c.march,c.april,c.may,c.june,c.july,c.august,c.september,c.october,c.november,c.december)");
			buffer.append(" FROM CeoReportView c ");
			buffer.append(" WHERE 1=1 ");
			// buffer.append("AND c.paymentDate >= :startDate ");
			// buffer.append("AND c.paymentDate <=:endDate ");

			if (criteria.getPeriodYear() != 0) {
				buffer.append("AND c.term = :term");
			}

			if (criteria.getSalePointName() != null && !criteria.getSalePointId().isEmpty()) {
				buffer.append(" AND c.salePointId = :salePointId ");
			}
			if (criteria.getSaleChannelType() != null) {
				buffer.append("AND c.saleChannelType = :saleChannelType ");
			}
			if (!criteria.getProductIdList().isEmpty()) {
				buffer.append(" AND c.productId IN :productIdList");
			}
			// buffer.append(" order by c.paymentYear asc,c.policyNo asc");
			Query query = em.createQuery(buffer.toString());

			// query.setParameter("startDate", startDate);
			// query.setParameter("endDate", endDate);

			if (criteria.getPeriodYear() != 0) {
				query.setParameter("term", criteria.getPeriodYear());
			}
			if (criteria.getSalePointName() != null && !criteria.getSalePointId().isEmpty()) {
				query.setParameter("salePointId", criteria.getSalePointId());
			}
			if (criteria.getSaleChannelType() != null) {
				query.setParameter("saleChannelType", criteria.getSaleChannelType());
			}
			if (!criteria.getProductIdList().isEmpty()) {
				query.setParameter("productIdList", criteria.getProductIdList());
			}

			result = query.getResultList();

		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ceoReport by ceoReport.", pe);
		}
		return result;
	}

}
