package org.ace.insurance.report.life.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.ace.insurance.report.life.CeoReportCriteria;
import org.ace.insurance.report.life.CeoReportDTO;
import org.ace.insurance.report.life.persistence.interfaces.IceoReportDAO;
import org.ace.insurance.report.life.service.interfaces.ICeoReportService;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.component.persistence.exception.DAOException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

@Service(value = "CeoReportService")
public class CeoReportService implements ICeoReportService {
	
	@Resource(name = "CeoReportDAO")
	private IceoReportDAO ceoReportDAO;

	@Transactional(propagation = Propagation.REQUIRED)
	public  List<CeoReportDTO> findeco(CeoReportCriteria ceoReportCriteria) {
		List<CeoReportDTO> result = null;
		try {
			result = ceoReportDAO.find(ceoReportCriteria);
		} catch (DAOException e) {
			throw new SystemException(e.getErrorCode(), "Faield to find ceoReport by criteria.", e);
		}
		return result;
	}

	
	@Transactional(propagation = Propagation.REQUIRED)
	public void generateceoReport(List<CeoReportDTO> reportList, String dirPath, String fileName, String branch) {
		try {
			List jasperPrintList = new ArrayList();
			Map<String, Object> params = new HashMap<String, Object>();
			double totalAmount = 0.0;
			double totalStampFee = 0.0;
			for (CeoReportDTO report : reportList) {
				// totalAmount = totalAmount + report.getAmount();
				// totalStampFee = totalStampFee + report.getStampFee();
			}

			params.put("grandTotalAmount", totalAmount);
			params.put("grandTotalStampFee", totalStampFee);
			params.put("branch", branch);
			params.put("LifeDailyIncomeList", new JRBeanCollectionDataSource(reportList));
			InputStream policyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream("report-template/life/ceoReport.jrxml");
			JasperReport policyJR = JasperCompileManager.compileReport(policyIS);
			JasperPrint policyJP = JasperFillManager.fillReport(policyJR, params, new JREmptyDataSource());
			jasperPrintList.add(policyJP);
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintList);
			FileHandler.forceMakeDirectory(dirPath);
			exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, new FileOutputStream(dirPath + fileName));
			exporter.exportReport();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private double getTotalAmount(List<CeoReportDTO> reportList) {
		double totalAmount = 0.0;
		for (CeoReportDTO ceoReportDTO : reportList) {
			// totalAmount = totalAmount + lifeDailyIncomeReport.getAmount();
		}
		return totalAmount;
	}

	
	
}
