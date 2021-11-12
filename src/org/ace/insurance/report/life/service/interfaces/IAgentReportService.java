package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.AgentReportCriteria;
import org.ace.insurance.report.life.AgentReportDTO;

public interface IAgentReportService {
	
	public List<AgentReportDTO> findagent(AgentReportCriteria ceoReportCriteria);


	public void generateagentReport(List<AgentReportDTO> reportList, String dirPath, String fileName, String branch);
}
