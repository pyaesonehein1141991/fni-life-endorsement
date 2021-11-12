package org.ace.insurance.web.manage.report.travel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.travel.TravelDailyReceiptReport;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TravelDailyReceiptReportExcel {
	private String fromDate;
	private String toDate;
	private double totalSi, totalPremium, totalSiUSD, totalPremiumUSD, totalStampFees;

	private XSSFWorkbook wb;

	public TravelDailyReceiptReportExcel(String fromDate, String toDate, List<TravelDailyReceiptReport> viewList) {
		this.fromDate = fromDate;
		this.toDate = toDate;
		load(viewList);
	}

	private void load(List<TravelDailyReceiptReport> viewList) {

		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/travel/Travel_Daily_Receipt_Report.xlsx");
			wb = new XSSFWorkbook(inp);
			totalSi = 0;
			totalPremium = 0;
			totalSiUSD = 0;
			totalPremiumUSD = 0;
			totalStampFees = 0;

			for (TravelDailyReceiptReport view : viewList) {
				if (view.isMMK()) {
					totalSi += view.getSuminsured();
					totalPremium += view.getPremium();
				} else {
					totalSiUSD += view.getSuminsured();
					totalPremiumUSD += view.getPremium();
				}
				totalStampFees += view.getStampFees();
			}
		} catch (

		IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load Travel_And_SpecialTravel_Daily_Receipt_Report.xlsx template", e);
		}
	}

	public void generate(OutputStream op, List<TravelDailyReceiptReport> viewList, TravelReportCriteria criteria) {
		try {
			Sheet sheet = wb.getSheet("travel");
			Row titleRow = sheet.getRow(1);
			Cell titleCell = titleRow.getCell(0);
			titleCell.setCellValue("Travel Insurance Daily Receipt Report from  " + fromDate + "  to  " + toDate);

			int i = 2;
			int index = 0;
			XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
			XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
			XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
			XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

			Row row = null;
			Cell noCell = null;
			Cell receiptDateCell = null;
			Cell customerCell = null;
			Cell agentNameCell = null;
			Cell proposalNoCell = null;
			Cell receiptNoCell = null;
			Cell suminsuredCell = null;
			Cell netpremiumCell = null;
			Cell grandtotalCell = null;
			Cell siCell = null;
			Cell premiumCell = null;

			for (TravelDailyReceiptReport mmr : viewList) {
				i = i + 1;
				index = index + 1;
				row = sheet.createRow(i);

				noCell = row.createCell(0);
				noCell.setCellValue(index);
				noCell.setCellStyle(defaultCellStyle);

				receiptDateCell = row.createCell(1);
				receiptDateCell.setCellValue(mmr.getReceiptDate());
				receiptDateCell.setCellStyle(dateCellStyle);

				customerCell = row.createCell(2);
				customerCell.setCellValue(mmr.getCustomerName());
				customerCell.setCellStyle(textCellStyle);

				agentNameCell = row.createCell(3);
				agentNameCell.setCellValue(mmr.getAgent());
				agentNameCell.setCellStyle(textCellStyle);

				proposalNoCell = row.createCell(4);
				proposalNoCell.setCellValue(mmr.getProposalNo());
				proposalNoCell.setCellStyle(textCellStyle);

				receiptNoCell = row.createCell(5);
				receiptNoCell.setCellValue(mmr.getReceiptNo());
				receiptNoCell.setCellStyle(textCellStyle);

				suminsuredCell = row.createCell(6);
				suminsuredCell.setCellValue(mmr.isMMK() ? mmr.getSuminsured() : 0);
				suminsuredCell.setCellStyle(currencyCellStyle);

				suminsuredCell = row.createCell(7);
				suminsuredCell.setCellValue(!mmr.isMMK() ? mmr.getSuminsured() : 0);
				suminsuredCell.setCellStyle(currencyCellStyle);

				netpremiumCell = row.createCell(8);
				netpremiumCell.setCellValue(mmr.isMMK() ? mmr.getPremium() : 0);
				netpremiumCell.setCellStyle(currencyCellStyle);

				netpremiumCell = row.createCell(9);
				netpremiumCell.setCellValue(!mmr.isMMK() ? mmr.getPremium() : 0);
				netpremiumCell.setCellStyle(currencyCellStyle);

				netpremiumCell = row.createCell(10);
				netpremiumCell.setCellValue(mmr.getStampFees());
				netpremiumCell.setCellStyle(currencyCellStyle);

			}
			i = i + 1;
			sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 5));
			row = sheet.createRow(i);

			grandtotalCell = row.createCell(0);
			grandtotalCell.setCellValue("Grand Total");
			grandtotalCell.setCellStyle(textCellStyle);
			ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 5), sheet, wb);

			siCell = row.createCell(6);
			siCell.setCellValue(totalSi);
			siCell.setCellStyle(currencyCellStyle);

			siCell = row.createCell(7);
			siCell.setCellValue(totalSiUSD);
			siCell.setCellStyle(currencyCellStyle);

			premiumCell = row.createCell(8);
			premiumCell.setCellValue(totalPremium);
			premiumCell.setCellStyle(currencyCellStyle);

			premiumCell = row.createCell(9);
			premiumCell.setCellValue(totalPremiumUSD);
			premiumCell.setCellStyle(currencyCellStyle);

			premiumCell = row.createCell(10);
			premiumCell.setCellValue(totalStampFees);
			premiumCell.setCellStyle(currencyCellStyle);

			wb.setPrintArea(0, "$A$1:$T$" + (i + 1));
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
