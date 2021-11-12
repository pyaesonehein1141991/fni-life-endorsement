package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.APEReportforHealthCriteria;
import org.ace.insurance.report.life.APEReportforHealthDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IAPEReportforHealthDAO {
	public List<APEReportforHealthDTO> find(APEReportforHealthCriteria apeReportCriteria) throws DAOException;

	
}
