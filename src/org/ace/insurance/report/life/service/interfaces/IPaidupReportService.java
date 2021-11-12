package org.ace.insurance.report.life.service.interfaces;

import java.util.List;

import org.ace.insurance.report.life.MKTforLifeReportDTO;
import org.ace.insurance.report.life.PaidupReportCriteria;
import org.ace.insurance.report.life.PaidupReportDTO;

public interface IPaidupReportService {

	public List<PaidupReportDTO> findpaidupReport(PaidupReportCriteria paidupReportCriteria);

	public void generatemktforLifeReport(List<MKTforLifeReportDTO> reportList, String dirPath, String fileName, String branch);
}
