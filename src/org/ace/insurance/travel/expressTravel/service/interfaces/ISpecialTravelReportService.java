package org.ace.insurance.travel.expressTravel.service.interfaces;

import java.util.List;

import org.ace.insurance.travel.SpecialTravelDailyReceiptReport;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.java.component.SystemException;

public interface ISpecialTravelReportService {
	public List<SpecialTravelDailyReceiptReport> findSpecialTravelByCriteria(TravelReportCriteria criteria) throws SystemException;
}
