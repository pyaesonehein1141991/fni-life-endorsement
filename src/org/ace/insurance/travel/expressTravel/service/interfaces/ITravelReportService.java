package org.ace.insurance.travel.expressTravel.service.interfaces;
import java.util.List;

import org.ace.insurance.travel.TravelDailyReceiptReport;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.java.component.SystemException;

public interface ITravelReportService {
	public List<TravelDailyReceiptReport> findByCriteria(TravelReportCriteria criteria) throws SystemException;
	

}
