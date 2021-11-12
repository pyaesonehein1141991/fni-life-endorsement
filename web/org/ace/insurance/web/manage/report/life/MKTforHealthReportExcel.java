package org.ace.insurance.web.manage.report.life;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.report.life.APEReportDTO;
import org.ace.insurance.report.life.MKTforHealthReportDTO;
import org.ace.insurance.report.life.MKTforLifeReportDTO;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MKTforHealthReportExcel {

	private XSSFWorkbook wb;
	// private FormulaEvaluator evaluator ;

	public MKTforHealthReportExcel() {
		load();
	}

	private void load() {
		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/life/MKTforHealthReport.xlsx");
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load MKTforHealthReport.xlsx tempalte", e);
		}
	}

	public Map<String, List<MKTforHealthReportDTO>> separateByPaymentChannel(List<MKTforHealthReportDTO> mktforhealthlist) {
		Map<String, List<MKTforHealthReportDTO>> map = new LinkedHashMap<String, List<MKTforHealthReportDTO>>();
		if (mktforhealthlist != null) {
			for (MKTforHealthReportDTO report : mktforhealthlist) {
				if (map.containsKey(report.getProductName())) {
					map.get(report.getProductName()).add(report);
				} else {
					List<MKTforHealthReportDTO> list = new ArrayList<MKTforHealthReportDTO>();
					list.add(report);
					map.put(report.getProductName(), list);
				}
			}
		}
		return map;

	}

	public void generate(OutputStream op, List<MKTforHealthReportDTO> mktforhealthlist) {
		try {
			Sheet sheet1 = wb.getSheet("MKTforHealthReport");

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

			Date date = mktforhealthlist.get(0).getPaymentDate();
			if (date != null) {
				year = Utils.getYearFormat(date);
				month = Utils.getMonthStringWithLowerCase(date.getMonth());
			}
		

			int i = 3;
			int index = 0;
		

			for (MKTforHealthReportDTO mktforhealthDto : mktforhealthlist) {
				

				i = i + 1;
				index = index + 1;
				row = sheet1.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(Utils.formattedDate(mktforhealthDto.getReceipt()));
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(mktforhealthDto.getPolicyNo());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(mktforhealthDto.getProductName());
				cell.setCellStyle(textCellStyle);


				cell = row.createCell(4);
				cell.setCellValue(mktforhealthDto.getNews());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(mktforhealthDto.getDue());
				cell.setCellStyle(textCellStyle);
				

				cell = row.createCell(6);
				cell.setCellValue(mktforhealthDto.getPaymentType());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(7);
				cell.setCellValue(mktforhealthDto.getSumInsured());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(8);
				cell.setCellValue(mktforhealthDto.getAmount());
				cell.setCellStyle(textCellStyle);
				

				cell = row.createCell(9);
				cell.setCellValue(mktforhealthDto.getApe());
				cell.setCellStyle(textCellStyle);
				
				
				cell = row.createCell(10);
				cell.setCellValue(mktforhealthDto.getPeriod());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(11);
				cell.setCellValue(mktforhealthDto.getSalepoint());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(12);
				cell.setCellValue(Utils.formattedDate(mktforhealthDto.getStartDate()));
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(13);
				cell.setCellValue(Utils.formattedDate(mktforhealthDto.getEndDate()));
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(14);
				cell.setCellValue(mktforhealthDto.getSaleChannel());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(15);
				cell.setCellValue(mktforhealthDto.getCustomerName());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(16);
				cell.setCellValue(mktforhealthDto.getAgentName() == null ? "NA[-]" : mktforhealthDto.getAgentName() + " \n [" + mktforhealthDto.getLiscenseno() + "]");
				cell.setCellStyle(textCellStyle);

			}

			i = i + 1;
			sheet1.addMergedRegion(new CellRangeAddress(i, i, 0, 16));
			row = sheet1.createRow(i);
			cell = row.createCell(0);
			//ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet1, wb);

			
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
			cell = row.createCell(15);
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