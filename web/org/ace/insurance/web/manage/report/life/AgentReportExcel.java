package org.ace.insurance.web.manage.report.life;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.life.AgentReportCriteria;
import org.ace.insurance.report.life.AgentReportDTO;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AgentReportExcel {

	private XSSFWorkbook wb;
	// private FormulaEvaluator evaluator ;

	public AgentReportExcel() {
		load();
	}

	private void load() {
		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/life/AgentReport.xlsx");
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load AgentReport.xlsx tempalte", e);
		}
	}

	public void generate(OutputStream op, List<AgentReportDTO> agentlist, AgentReportCriteria criteria) {
		try {
			Sheet sheet1 = wb.getSheet("AgentReport");

			XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
			XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
			XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
			XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
			XSSFCellStyle centerCellStyle = ExcelUtils.getAlignCenterStyle(wb);

			Row row;
			Cell cell;
			String year = "";
			String month = "";

			row = sheet1.getRow(2);
			cell = row.createCell(0);

			cell.setCellValue(Utils.getDateFormatString(criteria.getStartDate()) + " To" + Utils.getDateFormatString(criteria.getEndDate()));
			cell.setCellStyle(centerCellStyle);

			int i = 3;
			int index = 0;

			for (AgentReportDTO agentDto : agentlist) {

				i = i + 1;
				index = index + 1;
				row = sheet1.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(agentDto.getAgentName());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(agentDto.getLiscenseno());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(agentDto.getPhoneno());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(4);
				cell.setCellValue(agentDto.getResidentaddress());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(agentDto.getPolicyno());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(6);
				cell.setCellValue((agentDto.getPayablereceiptno()));
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(Utils.getDateFormatString((agentDto.getOutstandingdate())));
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(8);
				cell.setCellValue(agentDto.getSanctionNo());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(9);
				cell.setCellValue(Utils.getDateFormatString((agentDto.getSanctionDate())));
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(10);
				cell.setCellValue(agentDto.getInvoiceNo());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(11);
				cell.setCellValue(Utils.getDateFormatString(agentDto.getInvoiceDate()));
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(12);
				cell.setCellValue(agentDto.getVoucherno());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(13);
				cell.setCellValue(Utils.getDateFormatString(agentDto.getPaymentDate()));
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(14);
				cell.setCellValue(agentDto.getCommission());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(15);
				cell.setCellValue(agentDto.getInsuranceType());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(16);
				cell.setCellValue(agentDto.getRemark());
				cell.setCellStyle(textCellStyle);

			}

			i = i + 1;
			sheet1.addMergedRegion(new CellRangeAddress(i, i, 0, 16));
			row = sheet1.createRow(i);
			cell = row.createCell(0);
			// ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new
			// CellRangeAddress(i, i, 0, 6), sheet1, wb);

			cell = row.createCell(0);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(1);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(2);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(3);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(4);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");
			cell = row.createCell(5);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(6);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(7);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(8);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(9);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(10);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(11);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(12);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(13);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(14);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(15);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(16);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			wb.setPrintArea(0, 0, 16, 0, i);

			wb.write(op);
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}