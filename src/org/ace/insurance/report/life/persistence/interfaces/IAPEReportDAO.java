package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.APEReportCriteria;
import org.ace.insurance.report.life.APEReportDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IAPEReportDAO {
	public List<APEReportDTO> find(APEReportCriteria apeReportCriteria) throws DAOException;

	
}
