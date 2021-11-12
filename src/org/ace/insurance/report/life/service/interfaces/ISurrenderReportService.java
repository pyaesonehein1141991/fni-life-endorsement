package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.SurrenderReportCriteria;
import org.ace.insurance.report.life.SurrenderReportDTO;

public interface ISurrenderReportService {

	public List<SurrenderReportDTO> findsurrenderReport(SurrenderReportCriteria paidupReportCriteria);

	// public void generatemktforLifeReport(List<MKTforLifeReportDTO>
	// reportList, String dirPath, String fileName, String branch);
}
