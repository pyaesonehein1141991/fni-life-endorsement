package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.report.life.APEReportforHealthCriteria;
import org.ace.insurance.report.life.APEReportforHealthDTO;
import org.ace.insurance.report.life.persistence.interfaces.IAPEReportforHealthDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("APEReportforHealthDAO")
public class APEReportforHealthDAO extends BasicDAO implements IAPEReportforHealthDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<APEReportforHealthDTO> find(APEReportforHealthCriteria criteria) throws DAOException {
		List<APEReportforHealthDTO> result = new ArrayList<APEReportforHealthDTO>();

		// LocalDate localStartDate = LocalDate.of(criteria.getYear(),
		// criteria.getMonth() + 1, 1);
		// LocalDate localEndDate =
		// localStartDate.withDayOfMonth(localStartDate.lengthOfMonth());
		// Date startDate =
		// Date.from(localStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		// Date endDate =
		// Date.from(localEndDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		Date startDate = criteria.getStartDate();
		Date endDate = criteria.getEndDate();

		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + APEReportforHealthDTO.class.getName());
			buffer.append(
					"(c.id,c.receipt,c.policyNo,c.productId,c.startDate,c.endDate,c.sumInsured,c.productName,c.amount,c.ape,c.due,c.news,c.paymentType,c.period,c.salepoint,c.totalpremium,c.saleChannel,c.customerName)");
			buffer.append(" FROM APEReportforHealthView c ");
			buffer.append(" WHERE 1=1 ");
			buffer.append("AND c.receipt >= :startDate ");
			buffer.append("AND c.receipt <=:endDate ");
			if (!criteria.getProductIdList().isEmpty()) {
				buffer.append(" AND c.productId IN :productIdList");
			}

			Query query = em.createQuery(buffer.toString());

			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);
			if (!criteria.getProductIdList().isEmpty()) {
				query.setParameter("productIdList", criteria.getProductIdList());
			}

			result = query.getResultList();

		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ceoReport by apeReport.", pe);
		}
		return result;
	}

}
