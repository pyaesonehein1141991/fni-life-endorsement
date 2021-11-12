package org.ace.insurance.report.life.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.Utils;
import org.ace.insurance.report.life.MKTforHealthReportCriteria;
import org.ace.insurance.report.life.MKTforHealthReportDTO;
import org.ace.insurance.report.life.persistence.interfaces.IMKTReportforHealthDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;

@Repository("MKTReportforHealthDAO")
public class MKTReportforHealthDAO extends BasicDAO implements IMKTReportforHealthDAO {

	@SuppressWarnings("unchecked")
	@Override
	public List<MKTforHealthReportDTO> find(MKTforHealthReportCriteria mktforhealthReportCriteria) throws DAOException {
		List<MKTforHealthReportDTO> result = new ArrayList<MKTforHealthReportDTO>();


		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT NEW " + MKTforHealthReportDTO.class.getName());
			buffer.append("(c.id,c.receipt,c.policyNo,c.startDate,c.endDate,c.sumInsured,c.productName,c.amount,c.ape,c.due,c.news,c.paymentType,c.period,c.salepoint,c.saleChannel,c.customerName,c.agentName,c.liscenseno)");
			buffer.append(" FROM MKTReportforHealthView c ");
			buffer.append(" WHERE 1=1 ");
			if(mktforhealthReportCriteria.getStartDate()!=null) {
				mktforhealthReportCriteria.setStartDate(Utils.resetStartDate(mktforhealthReportCriteria.getStartDate()));
				buffer.append("AND c.receipt >= :startDate ");
			}
			if(mktforhealthReportCriteria.getEndDate()!=null) {
				mktforhealthReportCriteria.setEndDate(Utils.resetEndDate(mktforhealthReportCriteria.getEndDate()));
				buffer.append("AND c.receipt <=:endDate ");
			}
			
			
			if (mktforhealthReportCriteria.getPolicyNo() != null && !mktforhealthReportCriteria.getPolicyNo().isEmpty()) {
				buffer.append(" And c.policyNo= :policyNo");
			}
			

			Query query = em.createQuery(buffer.toString());

			query.setParameter("startDate", mktforhealthReportCriteria.getStartDate());
			query.setParameter("endDate", mktforhealthReportCriteria.getEndDate());
			if (mktforhealthReportCriteria.getPolicyNo() != null) {
				query.setParameter("policyNo", mktforhealthReportCriteria.getPolicyNo());
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
