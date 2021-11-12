package org.ace.insurance.report.personTravel.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.common.MonthlyReportNewCriteria;
import org.ace.insurance.report.personTravel.view.PersonTravelMonthlyReportView;
import org.ace.java.component.persistence.exception.DAOException;

public interface IPersonTravelMonthlyReportDAO {

	public List<PersonTravelMonthlyReportView> findPersonTravelMonthlyReport(MonthlyReportNewCriteria criteria) throws DAOException;

}
