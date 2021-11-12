package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.CeoReportCriteria;
import org.ace.insurance.report.life.CeoReportDTO;

public interface ICeoReportService {
	
	public List<CeoReportDTO> findeco(CeoReportCriteria ceoReportCriteria);


	public void generateceoReport(List<CeoReportDTO> reportList, String dirPath, String fileName, String branch);
}
