package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.APEReportCriteria;
import org.ace.insurance.report.life.APEReportDTO;
import org.ace.insurance.report.life.MKTforLifeReportCriteria;
import org.ace.insurance.report.life.MKTforLifeReportDTO;

public interface IMKTReportforLifeService {
	
	public List<MKTforLifeReportDTO> findmktforlife(MKTforLifeReportCriteria mktforlifeReportCriteria);


	public void generatemktforLifeReport(List<MKTforLifeReportDTO> reportList, String dirPath, String fileName, String branch);
}
