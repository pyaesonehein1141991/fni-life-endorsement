package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.SurrenderReportCriteria;
import org.ace.insurance.report.life.SurrenderReportDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface ISurrenderReportDAO {
	public List<SurrenderReportDTO> find(SurrenderReportCriteria surrenderCriteria) throws DAOException;

}
