//package org.ace.insurance.report.personTravel;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.List;
//
//import org.ace.insurance.common.ErrorCode;
//import org.ace.insurance.common.Utils;
//import org.ace.insurance.travel.claim.PersonTravelClaimReportView;
//import org.ace.insurance.travel.claim.TravelClaimReportCriteria;
//import org.ace.insurance.web.common.ExcelUtils;
//import org.ace.java.component.SystemException;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.xssf.usermodel.XSSFCellStyle;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//public class PersonTravelClaimReportExcel {
//	private String fromDate;
//	private String toDate;
//
//	private XSSFWorkbook wb;
//
//	public PersonTravelClaimReportExcel(String fromDate, String toDate, List<PersonTravelClaimReportView> viewList) {
//
//		this.fromDate = fromDate;
//		this.toDate = toDate;
//
//		load(viewList);
//	}
//
//	private void load(List<PersonTravelClaimReportView> viewList) {
//
//		try {
//			InputStream inp = this.getClass().getResourceAsStream("/report-template/personTravel/PersonTravel_Claim_Report.xlsx");
//			wb = new XSSFWorkbook(inp);
//
//		} catch (
//
//		IOException e) {
//			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load PersonTravel_Claim_Report.xlsx template", e);
//		}
//	}
//
//	public void generate(OutputStream op, List<PersonTravelClaimReportView> viewList, TravelClaimReportCriteria criteria) {
//		try {
//			Sheet sheet = wb.getSheet("persontravelclaim");
//			Row titleRow = sheet.getRow(1);
//			Cell titleCell = titleRow.getCell(0);
//			titleCell.setCellValue("( " + fromDate + " - " + toDate + " )");
//
//			int i = 2;
//			int index = 0;
//
//			XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
//			XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
//			XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
//			XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);
//
//			Row row = null;
//			Cell noCell = null;
//			Cell informCell = null;
//			Cell claimNoCell = null;
//			Cell claimDateCell = null;
//			Cell paidAmtCell = null;
//			Cell policyBranchCell = null;
//			Cell claimBranchCell = null;
//			Cell insuredNameCell = null;
//			Cell routeCell = null;
//			Cell termCell = null;
//			Cell sICell = null;
//			Cell unitCell = null;
//			Cell premiumCell = null;
//			Cell paymentDateCell = null;
//			Cell claimAmtCell = null;
//			Cell nameCell = null;
//			Cell nrcCell = null;
//			Cell agentNameCell = null;
//			Cell claimCoverTypeCell = null;
//
//			for (PersonTravelClaimReportView mmr : viewList) {
//				i = i + 1;
//				index = index + 1;
//				row = sheet.createRow(i);
//
//				noCell = row.createCell(0);
//				noCell.setCellValue(index);
//				noCell.setCellStyle(defaultCellStyle);
//
//				informCell = row.createCell(1);
//				informCell.setCellValue(mmr.getInformedDate());
//				informCell.setCellStyle(dateCellStyle);
//
//				claimNoCell = row.createCell(2);
//				claimNoCell.setCellValue(mmr.getClaimNo());
//				claimNoCell.setCellStyle(textCellStyle);
//
//				claimDateCell = row.createCell(3);
//				claimDateCell.setCellValue(mmr.getClaimDate());
//				claimDateCell.setCellStyle(dateCellStyle);
//
//				policyBranchCell = row.createCell(4);
//				policyBranchCell.setCellValue(mmr.getPolicyBranch());
//				policyBranchCell.setCellStyle(dateCellStyle);
//
//				claimBranchCell = row.createCell(5);
//				claimBranchCell.setCellValue(mmr.getClaimBranch());
//				claimBranchCell.setCellStyle(dateCellStyle);
//
//				insuredNameCell = row.createCell(6);
//				insuredNameCell.setCellValue(mmr.getInsuredPersonName());
//				insuredNameCell.setCellStyle(textCellStyle);
//
//				routeCell = row.createCell(7);
//				routeCell.setCellValue(mmr.getRoute());
//				routeCell.setCellStyle(textCellStyle);
//
//				termCell = row.createCell(8);
//				String term = Utils.formattedDate(mmr.getPolicyStartDate(), "dd/MM/yyyy") + "  To  " + Utils.formattedDate(mmr.getPolicyEndDate(), "dd/MM/yyyy");
//				termCell.setCellValue(term);
//				termCell.setCellStyle(textCellStyle);
//
//				sICell = row.createCell(9);
//				sICell.setCellValue(mmr.getsI());
//				sICell.setCellStyle(currencyCellStyle);
//
//				unitCell = row.createCell(10);
//				unitCell.setCellValue(mmr.getUnit());
//				unitCell.setCellStyle(currencyCellStyle);
//
//				premiumCell = row.createCell(11);
//				premiumCell.setCellValue(mmr.getPremium());
//				premiumCell.setCellStyle(currencyCellStyle);
//
//				nameCell = row.createCell(12);
//				nameCell.setCellValue(mmr.getName());
//				nameCell.setCellStyle(textCellStyle);
//
//				nrcCell = row.createCell(13);
//				nrcCell.setCellValue(mmr.getNrcNo());
//				nrcCell.setCellStyle(textCellStyle);
//
//				claimCoverTypeCell = row.createCell(14);
//				claimCoverTypeCell.setCellValue(mmr.getClaimCoverType());
//				claimCoverTypeCell.setCellStyle(textCellStyle);
//
//				claimAmtCell = row.createCell(15);
//				claimAmtCell.setCellValue(mmr.getClaimAmount());
//				claimAmtCell.setCellStyle(currencyCellStyle);
//
//				paidAmtCell = row.createCell(16);
//				paidAmtCell.setCellValue(mmr.getPaidAmount());
//				paidAmtCell.setCellStyle(currencyCellStyle);
//
//				paymentDateCell = row.createCell(17);
//				String paymentDate = mmr.getPaymentDate() == null ? " " : Utils.getDateFormatString(mmr.getPaymentDate());
//				paymentDateCell.setCellValue(paymentDate);
//				paymentDateCell.setCellStyle(dateCellStyle);
//
//				agentNameCell = row.createCell(18);
//				agentNameCell.setCellValue(mmr.getAgentName());
//				agentNameCell.setCellStyle(textCellStyle);
//
//			}
//			wb.setPrintArea(0, "$A$1:$T$" + (i + 1));
//			wb.write(op);
//			op.flush();
//			op.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
