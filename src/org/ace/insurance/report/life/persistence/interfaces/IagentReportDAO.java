package org.ace.insurance.report.life.persistence.interfaces;

import java.util.List;

import org.ace.insurance.report.life.AgentReportCriteria;
import org.ace.insurance.report.life.AgentReportDTO;
import org.ace.java.component.persistence.exception.DAOException;

public interface IagentReportDAO {
	public List<AgentReportDTO> find(AgentReportCriteria agentReportCriteria) throws DAOException;

	
}
