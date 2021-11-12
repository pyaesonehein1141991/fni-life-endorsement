package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.APEReportCriteria;
import org.ace.insurance.report.life.APEReportDTO;

public interface IAPEReportService {
	
	public List<APEReportDTO> findape(APEReportCriteria apeReportCriteria);


	public void generateapeReport(List<APEReportDTO> reportList, String dirPath, String fileName, String branch);
}
