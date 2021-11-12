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
import org.ace.insurance.report.life.MKTforLifeReportDTO;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.java.component.SystemException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MKTforLifeReportExcel {

	private XSSFWorkbook wb;
	// private FormulaEvaluator evaluator ;

	public MKTforLifeReportExcel() {
		load();
	}

	private void load() {
		try {
			InputStream inp = this.getClass().getResourceAsStream("/report-template/life/MKTforLifeReport.xlsx");
			wb = new XSSFWorkbook(inp);
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load MKTforLifeReport.xlsx tempalte", e);
		}
	}

	public Map<String, List<MKTforLifeReportDTO>> separateByPaymentChannel(List<MKTforLifeReportDTO> mktforlifelist) {
		Map<String, List<MKTforLifeReportDTO>> map = new LinkedHashMap<String, List<MKTforLifeReportDTO>>();
		if (mktforlifelist != null) {
			for (MKTforLifeReportDTO report : mktforlifelist) {
				if (map.containsKey(report.getProductName())) {
					map.get(report.getProductName()).add(report);
				} else {
					List<MKTforLifeReportDTO> list = new ArrayList<MKTforLifeReportDTO>();
					list.add(report);
					map.put(report.getProductName(), list);
				}
			}
		}
		return map;

	}

	public void generate(OutputStream op, List<MKTforLifeReportDTO> mktforlifelist) {
		try {
			Sheet sheet1 = wb.getSheet("MKTforLifeReport");

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

			Date date = mktforlifelist.get(0).getPaymentDate();
			if (date != null) {
				year = Utils.getYearFormat(date);
				month = Utils.getMonthStringWithLowerCase(date.getMonth());
			}
		

			int i = 3;
			int index = 0;
		

			for (MKTforLifeReportDTO mktforlifeDto : mktforlifelist) {
				

				i = i + 1;
				index = index + 1;
				row = sheet1.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue(index);
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(1);
				cell.setCellValue(Utils.formattedDate(mktforlifeDto.getReceipt()));
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(2);
				cell.setCellValue(mktforlifeDto.getPolicyNo());
				cell.setCellStyle(defaultCellStyle);

				cell = row.createCell(3);
				cell.setCellValue(mktforlifeDto.getProductName());
				cell.setCellStyle(textCellStyle);


				cell = row.createCell(4);
				cell.setCellValue(mktforlifeDto.getNews());
				cell.setCellStyle(textCellStyle);

				cell = row.createCell(5);
				cell.setCellValue(mktforlifeDto.getDue());
				cell.setCellStyle(textCellStyle);
				

				cell = row.createCell(6);
				cell.setCellValue(mktforlifeDto.getPaymentType());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(7);
				cell.setCellValue(mktforlifeDto.getSumInsured());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(8);
				cell.setCellValue(mktforlifeDto.getAmount());
				cell.setCellStyle(textCellStyle);
				

				cell = row.createCell(9);
				cell.setCellValue(mktforlifeDto.getApe());
				cell.setCellStyle(textCellStyle);
				
				
				cell = row.createCell(10);
				cell.setCellValue(mktforlifeDto.getPeriod());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(11);
				cell.setCellValue(mktforlifeDto.getSalepoint());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(12);
				cell.setCellValue(Utils.formattedDate(mktforlifeDto.getStartDate()));
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(13);
				cell.setCellValue(Utils.formattedDate(mktforlifeDto.getEndDate()));
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(14);
				cell.setCellValue(mktforlifeDto.getSaleChannel());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(15);
				cell.setCellValue(mktforlifeDto.getCustomerName());
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(16);
				cell.setCellValue(mktforlifeDto.getAgentName() == null ? "NA[-]" : mktforlifeDto.getAgentName() + " \n [" + mktforlifeDto.getLiscenseno() + "]");
				cell.setCellStyle(textCellStyle);
				
				cell = row.createCell(17);
				cell.setCellValue(mktforlifeDto.getRemark());
				cell.setCellStyle(textCellStyle);

			}

			i = i + 1;
			sheet1.addMergedRegion(new CellRangeAddress(i, i, 0, 17));
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
			
			cell = row.createCell(16);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");
			
			cell = row.createCell(17);
			cell.setCellStyle(defaultCellStyle);
			cell.setCellValue("-");
			
			wb.setPrintArea(0, 0, 17, 0, i);

			wb.write(op);
			op.flush();
			op.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}