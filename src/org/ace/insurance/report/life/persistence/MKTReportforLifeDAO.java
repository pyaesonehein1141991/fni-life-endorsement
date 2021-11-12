package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.life.MKTforLifeReportCriteria;
import org.ace.insurance.report.life.MKTforLifeReportDTO;
import org.ace.insurance.report.life.persistence.interfaces.IMKTReportforLifeDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("MKTReportforLifeDAO")
public class MKTReportforLifeDAO extends BasicDAO implements IMKTReportforLifeDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<MKTforLifeReportDTO> find(MKTforLifeReportCriteria mktforlifeReportCriteria) throws DAOException {
		List<MKTforLifeReportDTO> result = new ArrayList<MKTforLifeReportDTO>();

		// LocalDate localStartDate = LocalDate.of(criteria.getYear(),
		// criteria.getMonth() + 1, 1);
		// LocalDate localEndDate =
		// localStartDate.withDayOfMonth(localStartDate.lengthOfMonth());
		// Date startDate =
		// Date.from(localStartDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		// Date endDate =
		// Date.from(localEndDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());

		//Date startDate = mktforlifeReportCriteria.getStartDate();
		//Date endDate = mktforlifeReportCriteria.getEndDate();

		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + MKTforLifeReportDTO.class.getName());
			buffer.append("(c.id,c.receipt,c.policyNo,c.startDate,c.endDate,c.sumInsured,c.productName,c.amount,c.ape,c.due,c.news,c.paymentType,c.period,c.salepoint,c.saleChannel,c.customerName,c.agentName,c.liscenseno,c.remark)");
			buffer.append(" FROM MKTReportforLifeView c ");
			buffer.append(" WHERE 1=1 ");
			buffer.append("AND c.receipt >= :startDate ");
			buffer.append("AND c.receipt <=:endDate ");
			/*
			 * if (!mktforlifeReportCriteria.getProductIdList().isEmpty()) {
			 * buffer.append(" AND c.productId IN :productIdList"); }
			 */
			Query query = em.createQuery(buffer.toString());

			if (mktforlifeReportCriteria.getStartDate() != null) {
				mktforlifeReportCriteria.setStartDate(Utils.resetStartDate(mktforlifeReportCriteria.getStartDate()));
				query.setParameter("startDate", mktforlifeReportCriteria.getStartDate());
			}
			if (mktforlifeReportCriteria.getEndDate() != null) {
				mktforlifeReportCriteria.setEndDate(Utils.resetEndDate(mktforlifeReportCriteria.getEndDate()));
				query.setParameter("endDate", mktforlifeReportCriteria.getEndDate());
			}

			if (mktforlifeReportCriteria.getPolicyNo() != null) {
				query.setParameter("policyNo", mktforlifeReportCriteria.getPolicyNo());
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
