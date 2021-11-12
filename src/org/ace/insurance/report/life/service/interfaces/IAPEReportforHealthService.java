package org.ace.insurance.report.life.service.interfaces;

import java.util.List;


import org.ace.insurance.report.life.APEReportforHealthCriteria;
import org.ace.insurance.report.life.APEReportforHealthDTO;

public interface IAPEReportforHealthService {
	
	public List<APEReportforHealthDTO> findapeforHealth(APEReportforHealthCriteria apeReportforHealthCriteria);


	public void generateapeReportforHealth(List<APEReportforHealthDTO> reportList, String dirPath, String fileName, String branch);
}
