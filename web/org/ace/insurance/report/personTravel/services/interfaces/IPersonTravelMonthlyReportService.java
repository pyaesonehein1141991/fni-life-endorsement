package org.ace.insurance.report.personTravel.services.interfaces;

import java.util.List;

import org.ace.insurance.report.common.MonthlyReportNewCriteria;
import org.ace.insurance.report.personTravel.view.PersonTravelMonthlyReportView;

public interface IPersonTravelMonthlyReportService {

	public List<PersonTravelMonthlyReportView> findPersonTravelMonthlyReport(MonthlyReportNewCriteria criteria);

}
