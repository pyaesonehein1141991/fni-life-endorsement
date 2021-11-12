package org.ace.insurance.web.manage.report.life;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.life.CeoReportDTO;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CeoReportExcel {

	private XSSFWorkbook wb;
	// private FormulaEvaluator evaluator ;

	public CeoReportExcel() {
		load();
	}

	private void load() {
		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/life/CeoReport.xlsx");
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load CeoReport.xlsx tempalte", e);
		}
	}

	public Map<String, List<CeoReportDTO>> separateByPaymentChannel(List<CeoReportDTO> ceolist) {
		Map<String, List<CeoReportDTO>> map = new LinkedHashMap<String, List<CeoReportDTO>>();
		if (ceolist != null) {
			for (CeoReportDTO report : ceolist) {
				if (map.containsKey(report.getProductName())) {
					map.get(report.getProductName()).add(report);
				} else {
					List<CeoReportDTO> list = new ArrayList<CeoReportDTO>();
					list.add(report);
					map.put(report.getProductName(), list);
				}
			}
		}
		return map;

	}

	public <T> void generate(OutputStream op, List<CeoReportDTO> ceolist) {
		try {

			ceolist = ceolist.stream().sorted(Comparator.comparing(CeoReportDTO::getPolicyNo)).collect(Collectors.toList());
			Sheet sheet1 = wb.getSheet("CeoReport");

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

			for (CeoReportDTO ceoDto : ceolist) {

				i = i + 1;
				index = index + 1;
				row = sheet1.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(ceoDto.getPolicyNo());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(ceoDto.getInsuredpersonName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(ceoDto.getProductName());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(4);
				cell.setCellValue(ceoDto.getSumInsured());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(ceoDto.getTerm());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(6);
				cell.setCellValue(ceoDto.getPaymentType());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(7);
				cell.setCellValue(Utils.getDateFormatString(ceoDto.getStartDate()));
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(8);
				cell.setCellValue(Utils.getDateFormatString((ceoDto.getEndDate())));
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(9);
				cell.setCellValue(ceoDto.getSaleChannelType().getLabel());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(10);
				cell.setCellValue(ceoDto.getSalePointName());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(11);
				cell.setCellValue(ceoDto.getPaymentYear());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(12);
				cell.setCellValue(ceoDto.getJanuary());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(13);
				cell.setCellValue(ceoDto.getFebruary());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(14);
				cell.setCellValue(ceoDto.getMarch());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(15);
				cell.setCellValue(ceoDto.getApril());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(16);
				cell.setCellValue(ceoDto.getMay());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(17);
				cell.setCellValue(ceoDto.getJune());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(18);
				cell.setCellValue(ceoDto.getJuly());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(19);
				cell.setCellValue(ceoDto.getAugust());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(20);
				cell.setCellValue(ceoDto.getSeptember());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(21);
				cell.setCellValue(ceoDto.getOctober());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(22);
				cell.setCellValue(ceoDto.getNovember());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(23);
				cell.setCellValue(ceoDto.getDecember());
				cell.setCellStyle(textCellStyle);

			}

			i = i + 1;
			sheet1.addMergedRegion(new CellRangeAddress(i, i, 0, 23));
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

			cell = row.createCell(17);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(18);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(19);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");

			cell = row.createCell(20);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");
			cell = row.createCell(21);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");
			cell = row.createCell(22);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");
			cell = row.createCell(22);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");
			wb.setPrintArea(0, 0, 23, 0, i);

			wb.write(op);
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}