package org.ace.insurance.web.manage.report.account;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.common.utils.DateUtils;
import org.ace.insurance.report.TLF.PAMonthlyIncomeReportDTO;
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

public class PAMonthlyIncomeReportExcel {

	private XSSFWorkbook wb;

	public PAMonthlyIncomeReportExcel() {
		load();
	}

	private void load() {
		try (InputStream inp = this.getClass().getResourceAsStream("/report-template/personalAccident/PAMonthlyIncomeReport.xlsx");) {
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load PA_MonthlyReport.xlsx template", e);
		}
	}

	public void generate(OutputStream op, List<PAMonthlyIncomeReportDTO> paMonthlyIncomeReportDTOList) {
		try {
			Sheet sheet1 = wb.getSheet("PersonalAccident");

			XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
			XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
			XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
			XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
			XSSFCellStyle centerCellStyle = ExcelUtils.getAlignCenterStyle(wb);
			XSSFCellStyle centerBoldCellStyle = ExcelUtils.getFontBoldAlignCenterStyle(wb);

			Row row;
			Cell cell;

			row = sheet1.getRow(2);
			cell = row.createCell(0);

			Date date = paMonthlyIncomeReportDTOList.get(0).getPaymentDate();
			String year = Utils.getYearFormat(date);
			String month = Utils.getMonthStringWithLowerCase(date.getMonth());

			cell.setCellValue(year + " " + MyanmarLanguae.getMyanmarLanguaeString("YEAR_MYANMAR_002") + " " + month + " "
					+ MyanmarLanguae.getMyanmarLanguaeString("MONTH_MYANMAR_003") + MyanmarLanguae.getMyanmarLanguaeString("PA_MONTHLY_HEADER_001"));
			cell.setCellStyle(centerBoldCellStyle);

			int i = 5;
			int index = 0;
			String siFormula = "";
			String premiumFormula = "";
			String commissionFormula = "";
			String siUsdFormula = "";
			String premiumUsdFormula = "";
			String commissionUsdFormula = "";
			for (PAMonthlyIncomeReportDTO monthlyDto : paMonthlyIncomeReportDTOList) {
				i = i + 1;
				index = index + 1;
				row = sheet1.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(monthlyDto.getCustomerName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(monthlyDto.getAddress());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(monthlyDto.getPolicyNo());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(4);
				cell.setCellValue(monthlyDto.getNumberOfInsuredPerson());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(monthlyDto.getPeriodMonth());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(6);
				cell.setCellValue(
						DateUtils.getDateFormatString(monthlyDto.getActivedPolicyStartDate()) + " \n " + DateUtils.getDateFormatString(monthlyDto.getActivedPolicyEndDate()));
				cell.setCellStyle(dateCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(monthlyDto.getSumInsured());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(8);
				cell.setCellValue(monthlyDto.getSiUsd());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(9);
				cell.setCellValue(monthlyDto.getPremium());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(10);
				cell.setCellValue(monthlyDto.getPremiumUsd());

				cell.setCellStyle(currencyCellStyle);
				cell = row.createCell(11);
				cell.setCellValue(monthlyDto.getCommission());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(12);
				cell.setCellValue(monthlyDto.getComissionUsd());
				cell.setCellStyle(currencyCellStyle);

				cell = row.createCell(13);
				cell.setCellValue(monthlyDto.getReceiptNo() + " \n [" + Utils.getDateFormatString(monthlyDto.getPaymentDate()) + "]");
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(14);
				cell.setCellValue(monthlyDto.getAgentName() == null ? "NA[-]" : monthlyDto.getAgentName() + " \n [" + monthlyDto.getLiscenseNo() + "]");
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(15);
				cell.setCellValue(monthlyDto.getSalePointsName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(16);
				cell.setCellValue(monthlyDto.getFromDateToDate());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(17);
				cell.setCellValue(monthlyDto.getFromTermToTerm());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(18);
				cell.setCellValue(monthlyDto.getSaleChannelType().toString());
				cell.setCellStyle(textCellStyle);

			}

			i = i + 1;
			sheet1.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
			row = sheet1.createRow(i);
			cell = row.createCell(0);
			ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet1, wb);

			cell.setCellValue("Total");
			cell.setCellStyle(defaultCellStyle);

			cell = row.createCell(7);
			cell.setCellStyle(currencyCellStyle);
			siFormula = "SUM(H6:H" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(siFormula);

			cell = row.createCell(8);
			cell.setCellStyle(currencyCellStyle);
			siUsdFormula = "SUM(I6:I" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(siUsdFormula);

			cell = row.createCell(9);
			cell.setCellStyle(currencyCellStyle);
			premiumFormula = "SUM(J6:J" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(premiumFormula);

			cell = row.createCell(10);
			cell.setCellStyle(currencyCellStyle);
			premiumUsdFormula = "SUM(K6:K" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(premiumUsdFormula);

			cell = row.createCell(11);
			cell.setCellStyle(currencyCellStyle);
			commissionFormula = "SUM(L6:L" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(commissionFormula);

			cell = row.createCell(12);
			cell.setCellStyle(currencyCellStyle);
			commissionUsdFormula = "SUM(M6:M" + i + ")";
			cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
			cell.setCellFormula(commissionUsdFormula);

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

			cell = row.createCell(17);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");
			
			cell = row.createCell(18);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			wb.setPrintArea(0, 0, 18, 0, i);

			wb.write(op);
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
