package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.PaidupReportCriteria;
import org.ace.insurance.report.life.PaidupReportDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IPaidupReportDAO {
	public List<PaidupReportDTO> find(PaidupReportCriteria padiupCriteria) throws DAOException;

}
