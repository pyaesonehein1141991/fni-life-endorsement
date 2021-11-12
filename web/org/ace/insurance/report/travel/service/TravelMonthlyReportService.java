package org.ace.insurance.report.travel.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.report.travel.persistence.interfaces.ITravelMonthlyReportDAO;
import org.ace.insurance.report.travel.service.interfaces.ITravelMonthlyReportService;
import org.ace.insurance.report.travel.view.TravelMonthlyReportView;
import org.ace.insurance.web.manage.report.common.MonthlyReportNewCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("TravelMonthlyReportService")
public class TravelMonthlyReportService implements ITravelMonthlyReportService {

	@Resource(name = "TravelMonthlyReportDAO")
	private ITravelMonthlyReportDAO travelMonthlyReportDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<TravelMonthlyReportView> findTravelMonthlyReport(MonthlyReportNewCriteria criteria) throws SystemException {
		List<TravelMonthlyReportView> result = null;
		try {
			result = travelMonthlyReportDAO.findTravelMonthlyReport(criteria);
		} catch (DAOException de) {
			throw new SystemException(de.getErrorCode(), "Faield to find TravelMontlyReports by criteria.", de);
		}
		return result;
	}

}
