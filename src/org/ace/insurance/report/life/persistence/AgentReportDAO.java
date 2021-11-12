package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.report.life.AgentReportCriteria;
import org.ace.insurance.report.life.AgentReportDTO;
import org.ace.insurance.report.life.persistence.interfaces.IagentReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("AgentReportDAO")
public class AgentReportDAO extends BasicDAO implements IagentReportDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<AgentReportDTO> find(AgentReportCriteria criteria) throws DAOException {
		List<AgentReportDTO> result = new ArrayList<AgentReportDTO>();

		/*
		 * LocalDate localStartDate = LocalDate.of(criteria.getYear(),
		 * criteria.getMonth() + 1, 1); LocalDate localEndDate =
		 * localStartDate.withDayOfMonth(localStartDate.lengthOfMonth()); Date
		 * startDate =
		 * Date.from(localStartDate.atStartOfDay().atZone(ZoneId.systemDefault()
		 * ).toInstant()); Date endDate =
		 * Date.from(localEndDate.atStartOfDay().atZone(ZoneId.systemDefault()).
		 * toInstant());
		 * 
		 * startDate = DateUtils.resetStartDate(startDate); endDate =
		 * DateUtils.resetEndDate(endDate);
		 */

		Date startDate = criteria.getStartDate();
		Date endDate = criteria.getEndDate();

		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + AgentReportDTO.class.getName());
			buffer.append(
					"(c.agentName,c.liscenseno,c.phoneno,c.residentaddress,c.policyno,c.payablereceiptno,c.outstandingdate,c.sanctionNo,c.sanctionDate,c.invoiceNo,c.invoiceDate,c.voucherno,c.paymentDate,c.commission,c.insuranceType,c.remark)");
			buffer.append(" FROM AgentReportView c ");
			buffer.append(" WHERE 1=1 ");
			buffer.append("AND c.paymentDate >= :startDate ");
			buffer.append("AND c.paymentDate <=:endDate ");

			Query query = em.createQuery(buffer.toString());

			query.setParameter("startDate", startDate);
			query.setParameter("endDate", endDate);

			result = query.getResultList();

		} catch (NoResultException ne) {
			return new ArrayList<>();
		} catch (PersistenceException pe) {
			throw translate("Failed to find all of ceoReport by ceoReport.", pe);
		}
		return result;
	}

}
