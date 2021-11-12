package org.ace.insurance.report.travel.service.interfaces;

import java.util.List;

import org.ace.insurance.report.travel.view.TravelMonthlyReportView;
import org.ace.insurance.web.manage.report.common.MonthlyReportNewCriteria;

public interface ITravelMonthlyReportService {

	public List<TravelMonthlyReportView> findTravelMonthlyReport(MonthlyReportNewCriteria criteria);

}
