package org.ace.insurance.web.manage.report.travel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.travel.SpecialTravelDailyReceiptReport;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SpecialTravelDailyReceiptReportExcel {
	private String fromDate;
	private String toDate;
	private double totalSiKyat, totalSIUsd, totalPremiumKyat, totalPremiumUsd;

	private XSSFWorkbook wb;

	public SpecialTravelDailyReceiptReportExcel(String fromDate, String toDate, List<SpecialTravelDailyReceiptReport> viewList) {

		this.fromDate = fromDate;
		this.toDate = toDate;

		load(viewList);
	}

	private void load(List<SpecialTravelDailyReceiptReport> viewList) {

		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/travel/SpecialTravel_Daily_Receipt_Report.xlsx");
			wb = new XSSFWorkbook(inp);
			totalSiKyat = 0;
			totalSIUsd = 0;
			totalPremiumKyat = 0;
			totalPremiumUsd = 0;

			for (SpecialTravelDailyReceiptReport view : viewList) {
				totalSiKyat += view.isMMK() ? view.getSuminsured() : 0;
				totalSIUsd += view.isMMK() ? 0 : view.getSuminsured();
				totalPremiumKyat += view.isMMK() ? view.getPremium() : 0;
				totalPremiumUsd += view.isMMK() ? 0 : view.getPremium();
			}
		} catch (

		IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load Travel_And_SpecialTravel_Daily_Receipt_Report.xlsx template", e);
		}
	}

	public void generate(OutputStream op, List<SpecialTravelDailyReceiptReport> viewList, TravelReportCriteria criteria) {
		try {
			Sheet sheet = wb.getSheet("specialTravel");
			Row titleRow = sheet.getRow(1);
			Cell titleCell = titleRow.getCell(0);
			titleCell.setCellValue("Special Travel Insurance Daily Receipt Report from  " + fromDate + "  to  " + toDate);

			int i = 2;
			int index = 0;
			XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
			XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
			XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
			XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

			Row row = null;
			Cell cell = null;

			for (SpecialTravelDailyReceiptReport mmr : viewList) {
				i = i + 1;
				index = index + 1;
				row = sheet.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(mmr.getReceiptDate());
				cell.setCellStyle(dateCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(mmr.getCustomerName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(mmr.getProposalNo());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(4);
				cell.setCellValue(mmr.getReceiptNo());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(5);
				cell.setCellValue(mmr.getAgent());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(6);
				cell.setCellValue(mmr.isMMK() ? mmr.getSuminsured() : 0);
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(mmr.isMMK() ? 0 : mmr.getSuminsured());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(8);
				cell.setCellValue(mmr.isMMK() ? mmr.getPremium() : 0);
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(9);
				cell.setCellValue(mmr.isMMK() ? 0 : mmr.getPremium());
				cell.setCellStyle(currencyCellStyle);

			}
			i = i + 1;
			sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 5));
			row = sheet.createRow(i);

			cell = row.createCell(0);
			cell.setCellValue("Grand Total");
			cell.setCellStyle(textCellStyle);
			ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 5), sheet, wb);

			cell = row.createCell(6);
			cell.setCellValue(totalSiKyat);
			cell.setCellStyle(currencyCellStyle);

			cell = row.createCell(7);
			cell.setCellValue(totalSIUsd);
			cell.setCellStyle(currencyCellStyle);

			cell = row.createCell(8);
			cell.setCellValue(totalPremiumKyat);
			cell.setCellStyle(currencyCellStyle);

			cell = row.createCell(9);
			cell.setCellValue(totalPremiumUsd);
			cell.setCellStyle(currencyCellStyle);

			wb.setPrintArea(0, "$A$1:$J$" + (i + 1));
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
