package org.ace.insurance.report.travel.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.travel.view.TravelMonthlyReportView;
import org.ace.insurance.web.manage.report.common.MonthlyReportNewCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface ITravelMonthlyReportDAO {

	public List<TravelMonthlyReportView> findTravelMonthlyReport(MonthlyReportNewCriteria criteria) throws DAOException;

}
