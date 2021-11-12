package org.ace.insurance.travel.expressTravel.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.travel.TravelDailyReceiptReport;
import org.ace.insurance.travel.expressTravel.persistence.interfaces.ITravelReportDAO;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelReportService;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "TravelReportService")
public class TravelReportService implements ITravelReportService {

	@Resource(name = "TravelReportDAO")
	private ITravelReportDAO travelDailyIncomeReportDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<TravelDailyReceiptReport> findByCriteria(TravelReportCriteria criteria) {
		List<TravelDailyReceiptReport> result = null;
		try {
			result = travelDailyIncomeReportDAO.findByCriteria(criteria);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find Travel Daily Report by criteria.", e);
		}
		return result;
	}

}
