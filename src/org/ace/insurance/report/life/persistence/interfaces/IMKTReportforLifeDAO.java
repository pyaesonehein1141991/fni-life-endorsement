package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.APEReportCriteria;
import org.ace.insurance.report.life.APEReportDTO;
import org.ace.insurance.report.life.MKTforLifeReportCriteria;
import org.ace.insurance.report.life.MKTforLifeReportDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IMKTReportforLifeDAO {
	public List<MKTforLifeReportDTO> find(MKTforLifeReportCriteria mktforlifeReportCriteria) throws DAOException;

	
}
