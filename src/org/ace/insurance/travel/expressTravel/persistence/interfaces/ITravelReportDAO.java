package org.ace.insurance.travel.expressTravel.persistence.interfaces;

import java.util.List;
import org.ace.insurance.travel.TravelDailyReceiptReport;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.java.component.persistence.exception.DAOException;

public interface ITravelReportDAO {
	public List<TravelDailyReceiptReport> findByCriteria(TravelReportCriteria travelDailyCriteria) throws DAOException;
	
}
