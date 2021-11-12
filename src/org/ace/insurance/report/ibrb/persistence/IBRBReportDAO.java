package org.ace.insurance.report.ibrb.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.report.common.ReportCriteria;
import org.ace.insurance.report.ibrb.CriticalIBRBMonthlyReport;
import org.ace.insurance.report.ibrb.HealthIBRBMonthlyReport;
import org.ace.insurance.report.ibrb.MicroHealthIBRBMonthlyReport;
import org.ace.insurance.report.ibrb.persistence.interfaces.IIBRBReportDAO;
import org.ace.java.component.persistence.BasicDAO;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("IBRBReportDAO")
public class IBRBReportDAO extends BasicDAO implements IIBRBReportDAO {

	@Transactional(propagation = Propagation.REQUIRED)
	public List<HealthIBRBMonthlyReport> findHealthIBRBMonthlyReports(ReportCriteria criteria) throws DAOException {
		List<HealthIBRBMonthlyReport> resultList = new ArrayList<HealthIBRBMonthlyReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append(
					"SELECT NEW org.ace.insurance.report.ibrb.HealthIBRBMonthlyReport(h.id, h.insuredPersonName, h.branchId, h.salePointId, h.activedPolicyStartDate, h.paymentDate, h.receiptNo,"
							+ "h.policyNo, h.gender, h.age, h.occupation, h.residentAddress, h.province, h.township, h.paymentType, h.customerType,"
							+ "h.totalPremium, h.benefInfo, h.hisInfo, h.hisDis, h.basicUnit, h.addOn1Unit, h.addOn2Unit, h.agentCommission, h.agentName,"
							+ "h.liscenseno, h.fromTermtoTerm, h.fromDatetoDate, h.salesPointName, h.saleChannelType) FROM HealthIBRBMonthlyReport h");
			// query.append(" WHERE h.policyNo = 'FNI-HO/HI/PL/00000569/9-2020'
			// AND h.paymentDate >= :startDate AND h.paymentDate <= :endDate");
			query.append(" WHERE h.policyNo IS NOT NULL AND h.paymentDate >= :startDate AND h.paymentDate <= :endDate");

			if (criteria.getBranchId() != null) {
				query.append(" AND h.branchId = :branchId");
			}

			if (criteria.getSaleChannelType() != null) {
				query.append(" AND h.saleChannelType = :saleChannelType");
			}
			if (criteria.getSalePointId() != null) {
				query.append(" AND h.salePointId = :salePointId");
			}
			query.append(" ORDER BY h.policyNo,h.receiptNo");
			Query q = em.createQuery(query.toString(), HealthIBRBMonthlyReport.class);

			q.setParameter("startDate", DateUtils.resetStartDate(criteria.getStartDate()));
			// Utils.getStartDate(criteria.getYear(),
			// criteria.getMonth())
			q.setParameter("endDate", DateUtils.resetEndDate(criteria.getEndDate()));
			// Utils.getEndDate(criteria.getYear(),
			// criteria.getMonth())

			if (criteria.getBranchId() != null) {
				q.setParameter("branchId", criteria.getBranchId());
			}
			if (criteria.getSaleChannelType() != null) {
				q.setParameter("saleChannelType", criteria.getSaleChannelType());
			}
			if (criteria.getSalePointId() != null) {
				q.setParameter("salePointId", criteria.getSalePointId());
			}
			resultList = q.getResultList();

		} catch (PersistenceException pe) {
			throw translate("Failed to find HealthIBRBMonthlyReport", pe);
		}
		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<CriticalIBRBMonthlyReport> findCriticalIBRBMonthlyReports(ReportCriteria criteria) throws DAOException {
		List<CriticalIBRBMonthlyReport> resultList = new ArrayList<CriticalIBRBMonthlyReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT h FROM CriticalIBRBMonthlyReport h");
			query.append(" WHERE h.policyNo IS NOT NULL AND h.paymentDate >= :startDate AND h.paymentDate <= :endDate");

			if (criteria.getBranchId() != null) {
				query.append(" AND h.branchId = :branchId");
			}
			if (criteria.getSalePointId() != null) {
				query.append(" AND h.salePointId = :salePointId");
			}
			if (criteria.getSaleChannelType() != null) {
				query.append(" AND h.saleChannelType = :saleChannelType");
			}
			query.append(" ORDER BY h.policyNo");
			Query q = em.createQuery(query.toString());
			q.setParameter("startDate", criteria.getStartDate()); // Utils.getStartDate(criteria.getYear(),
																	// criteria.getMonth())
			q.setParameter("endDate", criteria.getEndDate());// Utils.getEndDate(criteria.getYear(),
																// criteria.getMonth())

			if (criteria.getBranchId() != null) {
				q.setParameter("branchId", criteria.getBranchId());
			}
			if (criteria.getSalePointId() != null) {
				q.setParameter("salePointId", criteria.getSalePointId());
			}
			if (criteria.getSaleChannelType() != null) {
				q.setParameter("saleChannelType", criteria.getSaleChannelType());
			}
			resultList = q.getResultList();

		} catch (PersistenceException pe) {
			throw translate("Failed to find CriticalIBRBMonthlyReport", pe);
		}
		return resultList;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public List<MicroHealthIBRBMonthlyReport> findMicroHealthIBRBMonthlyReports(ReportCriteria criteria) throws DAOException {
		List<MicroHealthIBRBMonthlyReport> resultList = new ArrayList<MicroHealthIBRBMonthlyReport>();
		try {
			StringBuffer query = new StringBuffer();
			query.append("SELECT h FROM MicroHealthIBRBMonthlyReport h");
			query.append(" WHERE h.policyNo IS NOT NULL AND h.paymentDate >= :startDate AND h.paymentDate <= :endDate");

			if (criteria.getBranchId() != null) {
				query.append(" AND h.branchId = :branchId");
			}
			if (criteria.getSalePointId() != null) {
				query.append(" AND h.salePointId = :salePointId");
			}
			if (criteria.getSaleChannelType() != null) {
				query.append(" AND h.saleChannelType = :saleChannelType");
			}
			query.append(" ORDER BY h.policyNo");
			Query q = em.createQuery(query.toString());
			q.setParameter("startDate", criteria.getStartDate()); // Utils.getStartDate(criteria.getYear(),
																	// criteria.getMonth())
			q.setParameter("endDate", criteria.getEndDate());// Utils.getEndDate(criteria.getYear(),
																// criteria.getMonth())

			if (criteria.getBranchId() != null) {
				q.setParameter("branchId", criteria.getBranchId());
			}
			if (criteria.getSalePointId() != null) {
				q.setParameter("salePointId", criteria.getSalePointId());
			}
			if (criteria.getSaleChannelType() != null) {
				q.setParameter("saleChannelType", criteria.getSaleChannelType());
			}
			resultList = q.getResultList();

		} catch (PersistenceException pe) {
			throw translate("Failed to find MicroHealthIBRBMonthlyReport", pe);
		}
		return resultList;
	}

}
