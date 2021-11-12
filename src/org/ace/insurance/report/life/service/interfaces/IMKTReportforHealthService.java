package org.ace.insurance.report.life.service.interfaces;

import java.util.List;


import org.ace.insurance.report.life.MKTforHealthReportCriteria;
import org.ace.insurance.report.life.MKTforHealthReportDTO;


public interface IMKTReportforHealthService {
	
	public List<MKTforHealthReportDTO> findmktforhealth(MKTforHealthReportCriteria mktforlifeReportCriteria);


	public void generatemktforHealthReport(List<MKTforHealthReportDTO> reportList, String dirPath, String fileName, String branch);
}
