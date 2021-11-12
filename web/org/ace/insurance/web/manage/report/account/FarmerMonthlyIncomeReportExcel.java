package org.ace.insurance.web.manage.report.account;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.TLF.FarmerMonthlyIncomeReportDTO;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.common.myanmarLanguae.MyanmarLanguae;
import org.ace.java.component.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FarmerMonthlyIncomeReportExcel {

	private XSSFWorkbook wb;

	public FarmerMonthlyIncomeReportExcel() {
		load();
	}

	private void load() {
		try (InputStream inp = this.getClass().getResourceAsStream("/report-template/Farmer/farmersLifeMonthlyReport.xlsx");) {
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load FarmerMonthlyReport.xlsx template", e);
		}
	}

	public void generate(OutputStream op, List<FarmerMonthlyIncomeReportDTO> farmerMonthlyIncomeReportDTOList) {
		try {
			Sheet sheet1 = wb.getSheet("Farmer");

			XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
			XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
			XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
			XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
			XSSFCellStyle centerCellStyle = ExcelUtils.getAlignCenterStyle(wb);

			Row row;
			Cell cell;

			row = sheet1.getRow(3);
			cell = row.createCell(0);

			Date date = farmerMonthlyIncomeReportDTOList.get(0).getPaymentDate();
			String year = Utils.getYearFormat(date);
			String month = Utils.getMonthStringWithLowerCase(date.getMonth());

			cell.setCellValue(year + " " + MyanmarLanguae.getMyanmarLanguaeString("YEAR_MYANMAR_002") + " " + month + " "
					+ MyanmarLanguae.getMyanmarLanguaeString("MONTH_MYANMAR_003") + MyanmarLanguae.getMyanmarLanguaeString("FARMER_MONTHLY_HEADER_001"));
			cell.setCellStyle(centerCellStyle);

			int i = 5;
			int index = 0;
			String siFormula = "";
			String premiumFormula = "";
			String commissionFormula = "";

			for (FarmerMonthlyIncomeReportDTO monthlyDto : farmerMonthlyIncomeReportDTOList) {
				i = i + 1;
				index = index + 1;
				row = sheet1.createRow(i);

				cell = row.createCell(1);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(monthlyDto.getPaymentDate());
				cell.setCellStyle(dateCellStyle);
				
				cell = row.createCell(3);
				cell.setCellValue(monthlyDto.getPolicyNo());
				cell.setCellStyle(dateCellStyle);
				
				cell = row.createCell(4);
				cell.setCellValue(monthlyDto.getReceiptNo());
				cell.setCellStyle(dateCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(monthlyDto.getCustomerName());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(6);
				cell.setCellValue(monthlyDto.getAddress());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(monthlyDto.getNrcNo());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(8);
				cell.setCellValue(monthlyDto.getSumInsured());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(9);
				cell.setCellValue(monthlyDto.getPremium());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(10);
				cell.setCellValue(monthlyDto.getCommission());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(11);
				cell.setCellValue(monthlyDto.getAgentName() == null ? "NA[-]" : monthlyDto.getAgentName() + " \n [" + monthlyDto.getLiscenseNo() + "]");
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(12);
				cell.setCellValue(monthlyDto.getSalePointsName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(13);
				cell.setCellValue(monthlyDto.getFromDateToDate());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(14);
				cell.setCellValue(monthlyDto.getFromTermToTerm());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(15);
				cell.setCellValue(monthlyDto.getSaleChannelType().toString());
				cell.setCellStyle(textCellStyle);
			}

			i = i + 1;
			sheet1.addMergedRegion(new CellRangeAddress(i, i, 0, 7));
			row = sheet1.createRow(i);
			cell = row.createCell(0);
			ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 7), sheet1, wb);

			cell.setCellValue("Total");
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(8);
			cell.setCellStyle(currencyCellStyle);
			siFormula = "SUM(I6:I" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(siFormula);

			cell = row.createCell(9);
			cell.setCellStyle(currencyCellStyle);
			premiumFormula = "SUM(J6:J" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(premiumFormula);

			cell = row.createCell(10);
			cell.setCellStyle(currencyCellStyle);
			commissionFormula = "SUM(K6:K" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(commissionFormula);

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
			
			wb.setPrintArea(0, 0, 15, 0, i);

			wb.write(op);
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}