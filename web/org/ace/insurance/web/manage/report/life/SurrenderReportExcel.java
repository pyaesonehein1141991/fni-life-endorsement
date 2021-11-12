package org.ace.insurance.web.manage.report.life;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.life.SurrenderReportDTO;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SurrenderReportExcel {

	private XSSFWorkbook wb;
	// private FormulaEvaluator evaluator ;

	public SurrenderReportExcel() {
		load();
	}

	private void load() {
		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/life/SurrenderReport.xlsx");
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load SurrenderReport.xlsx tempalte", e);
		}
	}

	public Map<String, List<SurrenderReportDTO>> separateByPaymentChannel(List<SurrenderReportDTO> surrenderlist) {
		Map<String, List<SurrenderReportDTO>> map = new LinkedHashMap<String, List<SurrenderReportDTO>>();

		return map;

	}

	public void generate(OutputStream op, List<SurrenderReportDTO> surrenderlist) {
		try {
			Sheet sheet1 = wb.getSheet("SurrenderReport");

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

			int i = 3;
			int index = 0;

			for (SurrenderReportDTO mktforlifeDto : surrenderlist) {

				i = i + 1;
				index = index + 1;
				row = sheet1.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				cell.setCellValue((mktforlifeDto.getInsruedPersonName()));
				cell.setCellStyle(defaultCellStyle);
				cell = row.createCell(2);
				cell.setCellValue(mktforlifeDto.getAge());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(mktforlifeDto.getPolicyNo());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(4);
				cell.setCellValue(mktforlifeDto.getFromDateToDate());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(mktforlifeDto.getPaymentType());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(6);
				cell.setCellValue(mktforlifeDto.getSumInsured());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(mktforlifeDto.getBasictermPremium());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(8);
				cell.setCellValue(mktforlifeDto.getDueNo());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(9);
				cell.setCellValue(mktforlifeDto.getSurrenderAmount());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(10);
				cell.setCellValue(mktforlifeDto.getReceiptNo());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(11);
				cell.setCellValue(mktforlifeDto.getAgentName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(12);
				cell.setCellValue("");
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(13);
				cell.setCellValue(Utils.formattedDate(mktforlifeDto.getSubmittedDate()));
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(14);
				cell.setCellValue(mktforlifeDto.getSalepoint());
				cell.setCellStyle(textCellStyle);
				cell = row.createCell(15);
				cell.setCellValue(mktforlifeDto.getPolicyTerm());
				cell.setCellStyle(textCellStyle);

			}

			i = i + 1;
			sheet1.addMergedRegion(new CellRangeAddress(i, i, 0, 15));
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

			wb.setPrintArea(0, 0, 15, 0, i);

			wb.write(op);
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}