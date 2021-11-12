package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.CeoReportCriteria;
import org.ace.insurance.report.life.CeoReportDTO;
import org.ace.insurance.report.life.LifeDailyPremiumIncomeReportDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IceoReportDAO {
	public List<CeoReportDTO> find(CeoReportCriteria ceoReportCriteria) throws DAOException;

	
}
