package org.ace.insurance.travel.expressTravel.service;

import java.util.List;

import javax.annotation.Resource;

import org.ace.insurance.travel.SpecialTravelDailyReceiptReport;
import org.ace.insurance.travel.expressTravel.persistence.interfaces.ISpecialTravelReportDAO;
import org.ace.insurance.travel.expressTravel.service.interfaces.ISpecialTravelReportService;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service(value = "SpecialTravelReportService")
public class SpecialTravelReportService implements ISpecialTravelReportService{
	@Resource(name = "SpecialTravelReportDAO")
	private ISpecialTravelReportDAO travelDailyIncomeReportDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public List<SpecialTravelDailyReceiptReport> findSpecialTravelByCriteria(TravelReportCriteria criteria) throws SystemException {
		List<SpecialTravelDailyReceiptReport> result = null;
		try {
			result = travelDailyIncomeReportDAO.findSpecialTravelByCriteria(criteria);

		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find SpecialTravel Daily Report by criteria.", e);
		}
		return result;

	}
}
