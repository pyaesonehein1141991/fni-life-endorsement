package org.ace.insurance.report.personTravel.services;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.report.common.MonthlyReportNewCriteria;
import org.ace.insurance.report.personTravel.persistence.interfaces.IPersonTravelMonthlyReportDAO;
import org.ace.insurance.report.personTravel.services.interfaces.IPersonTravelMonthlyReportService;
import org.ace.insurance.report.personTravel.view.PersonTravelMonthlyReportView;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("PersonTravelMonthlyReportService")
public class PersonTravelMonthlyReportService implements IPersonTravelMonthlyReportService {

	@Resource(name = "PersonTravelMonthlyReportDAO")
	private IPersonTravelMonthlyReportDAO personTravelMonthlyReportDAO;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<PersonTravelMonthlyReportView> findPersonTravelMonthlyReport(MonthlyReportNewCriteria criteria) throws SystemException {
		List<PersonTravelMonthlyReportView> result = null;
		try {
			result = personTravelMonthlyReportDAO.findPersonTravelMonthlyReport(criteria);
		} catch (DAOException de) {
			throw new SystemException(de.getErrorCode(), "Faield to find PersonTravelMontlyReports by criteria.", de);
		}
		return result;
	}

}
