package org.ace.insurance.web.manage.report.travel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.travel.view.TravelMonthlyReportView;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.common.myanmarLanguae.MyanmarLanguae;
import org.ace.insurance.web.manage.report.common.MonthlyReportNewCriteria;
import org.ace.java.component.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TravelMonthlyReportExcel {
	private XSSFWorkbook wb;

	public TravelMonthlyReportExcel() {
		load();
	}

	private void load() {
		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/travel/SpecialTravel_Monthly_Report.xlsx");
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load SpecialTravel_Monthly_Report.xlsx tempalte", e);
		}
	}

	public void generate(OutputStream op, List<TravelMonthlyReportView> viewList, MonthlyReportNewCriteria criteria) {

		try {
			Sheet sheet = wb.getSheet("SpecialTravel");
			Row row = sheet.getRow(1);
			Cell cell = row.getCell(0);
			cell.setCellValue(Utils.getDateFormatString(criteria.getFromDate()) + " " + MyanmarLanguae.getMyanmarLanguaeString("SPECIALTRAVEL_MYANMAR_HEADER_LABEL_001")
					+ Utils.getDateFormatString(criteria.getToDate()) + " " + MyanmarLanguae.getMyanmarLanguaeString("SPECIALTRAVEL_MYANMAR_HEADER_LABEL_002") + " ("
					+ criteria.getBranchName() + ")");
			int i = 3;
			int index = 0;
			XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
			XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
			XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
			XSSFCellStyle cellStyle = ExcelUtils.getAlignCenterStyle(wb);
			String formula = null;
			for (TravelMonthlyReportView ptmr : viewList) {
				i = i + 1;
				index = index + 1;
				row = sheet.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(Utils.getDateFormatString(ptmr.getPaymentDate()));
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(ptmr.getExpressName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(ptmr.getVehicleNo());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(4);
				cell.setCellValue(ptmr.getTravelPath());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(ptmr.getTerm());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(6);
				cell.setCellValue(ptmr.getNoOfPass());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(ptmr.getTotalUnit());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(8);
				cell.setCellValue(ptmr.getTotalSI());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(9);
				cell.setCellValue(ptmr.getPremium());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(10);
				cell.setCellValue(ptmr.getCommission());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(11);
				cell.setCellValue(ptmr.getTotalPremium());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(12);
				cell.setCellValue(ptmr.getReceiptNoAndDate());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(13);
				cell.setCellValue(ptmr.getAgentName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(14);
				cell.setCellStyle(textCellStyle);
			}

			i = i + 1;
			sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 5));
			row = sheet.createRow(i);
			cell = row.createCell(0);
			cell.setCellValue(" \u1005\u102F\u1005\u102F\u1015\u1031\u102B\u1004\u103A\u1038");
			cell.setCellStyle(cellStyle);
			ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 5), sheet, wb);

			cell = row.createCell(6);
			cell.setCellStyle(currencyCellStyle);
			formula = "SUM(G5:G" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(formula);

			cell = row.createCell(7);
			cell.setCellStyle(currencyCellStyle);
			formula = "SUM(H5:H" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(formula);

			cell = row.createCell(8);
			cell.setCellStyle(currencyCellStyle);
			formula = "SUM(I5:I" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(formula);

			cell = row.createCell(9);
			cell.setCellStyle(currencyCellStyle);
			formula = "SUM(J5:J" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(formula);

			cell = row.createCell(10);
			cell.setCellStyle(currencyCellStyle);
			formula = "SUM(K5:K" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(formula);

			cell = row.createCell(11);
			cell.setCellStyle(currencyCellStyle);
			formula = "SUM(L5:L" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(formula);

			cell = row.createCell(12);
			cell.setCellStyle(textCellStyle);

			cell = row.createCell(13);
			cell.setCellStyle(textCellStyle);

			wb.setPrintArea(0, 0, 14, 0, i);
			wb.write(op);
			op.flush();
			op.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
