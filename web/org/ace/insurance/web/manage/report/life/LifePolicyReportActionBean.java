package org.ace.insurance.web.manage.report.life;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.KeyFactorIDConfig;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.report.life.LifePolicyReport;
import org.ace.insurance.report.life.LifePolicyReportCriteria;
import org.ace.insurance.report.life.service.interfaces.ILifePolicyReportService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.ExcelUtils;
import org.ace.insurance.web.util.FileHandler;
import org.ace.java.component.SystemException;
import org.ace.java.web.ApplicationSetting;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifePolicyReportActionBean")
public class LifePolicyReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifePolicyReportService}")
	private ILifePolicyReportService lifePolicyReportService;

	public void setLifePolicyReportService(ILifePolicyReportService lifePolicyReportService) {
		this.lifePolicyReportService = lifePolicyReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private LifePolicyReportCriteria criteria;
	private List<LifePolicyReport> lifePolicyList;
	private boolean accessBranches;
	private User user;
	private String branch;
	private List<String> productIdList;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		// if (user.isAccessAllBranch()) {
		accessBranches = true;
		// }
		productIdList = new ArrayList<String>();
		productIdList.add(KeyFactorIDConfig.getPublicLifeId());
		productIdList.add(KeyFactorIDConfig.getGroupLifeId());
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new LifePolicyReportCriteria();
		if (!accessBranches) {
			criteria.setBranch(user.getBranch());
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setPaymentStartDate(cal.getTime());
		criteria.setCommenceStartDate(cal.getTime());
		Date todayDate = new Date();
		criteria.setPaymentEndDate(todayDate);
		criteria.setCommenceEndDate(todayDate);
		filter();
	}

	public ProposalType[] getProposalTypeSelectItemList() {
		return ProposalType.values();
	}

	public void filter() {
		boolean valid = true;
		String formID = "policyList";
		if (criteria.getPaymentStartDate() != null && criteria.getPaymentEndDate() != null) {
			if (criteria.getPaymentStartDate().after(criteria.getPaymentEndDate())) {
				addErrorMessage(formID + ":paymentStartDate", MessageId.STARTDATE_MUSTBE_LESSTHAN_ENDDATE);
				valid = false;
			}
		}
		if (criteria.getCommenceStartDate() != null && criteria.getCommenceEndDate() != null) {
			if (criteria.getCommenceStartDate().after(criteria.getCommenceEndDate())) {
				addErrorMessage(formID + ":commenceStartDate", MessageId.STARTDATE_MUSTBE_LESSTHAN_ENDDATE);
				valid = false;
			}
		}
		if (valid) {
			lifePolicyList = lifePolicyReportService.findLifePolicyReport(criteria, productIdList);
		}
	}

	public List<LifePolicyReport> getLifePolicyList() {
		return lifePolicyList;
	}

	public ILifePolicyReportService getLifePolicyReportService() {
		return lifePolicyReportService;
	}

	public LifePolicyReportCriteria getCriteria() {
		return criteria;
	}

	private final String reportName = "LifePolicyReport";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		try {
			FileHandler.forceMakeDirectory(dirPath);
			if (criteria.getBranch() == null) {
				branch = "All";
			} else {
				branch = criteria.getBranch().getName();
			}

			lifePolicyReportService.generateLifePolicyReport(lifePolicyList, dirPath, fileName, branch);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Branch> getBranchList() {
		return branchService.findAllBranch();
	}

	public double totalReportPremiun() {
		double total = 0.0;
		for (LifePolicyReport lifePolicyReport : lifePolicyList) {
			total += lifePolicyReport.getPremium();
		}
		return total;

	}

	public double totalReportSI() {
		double total = 0.0;
		for (LifePolicyReport lifePolicyReport : lifePolicyList) {
			total += lifePolicyReport.getSumInsured();
		}
		return total;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		criteria.setCustomer(customer);
	}

	public void selectProduct() {
		selectProduct(InsuranceType.LIFE);
	}

	// Exporting
	public void exportExcel() {
		ExternalContext ec = getFacesContext().getExternalContext();
		ec.responseReset();
		ec.setResponseContentType("application/vnd.ms-excel");
		String fileName = "LifePolicyReport.xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try {
			OutputStream op = ec.getResponseOutputStream();
			ExportExcel exportExcel = new ExportExcel();
			exportExcel.generate(op);
			getFacesContext().responseComplete();
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export LifePolicyReport.xlsx", e);
		}
	}

	private class ExportExcel {
		private XSSFWorkbook wb;

		public ExportExcel() {
			load();
		}

		private void load() {
			try {
				InputStream inp = this.getClass().getResourceAsStream("/report-template/life/LifePolicyReport.xlsx");
				wb = new XSSFWorkbook(inp);
			} catch (IOException e) {
				throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to load LifePolicyReport.xlsx template", e);
			}
		}

		public Map<String, List<LifePolicyReport>> separateBranch() {
			Map<String, List<LifePolicyReport>> map = new LinkedHashMap<String, List<LifePolicyReport>>();
			for (LifePolicyReport report : lifePolicyList) {
				if (map.containsKey(report.getBranchName())) {
					map.get(report.getBranchName()).add(report);
				} else {
					List<LifePolicyReport> list = new ArrayList<LifePolicyReport>();
					list.add(report);
					map.put(report.getBranchName(), list);
				}
			}
			return map;
		}

		public void generate(OutputStream op) {
			try {
				Sheet sheet = wb.getSheet("LifePolicyReport");

				XSSFCellStyle defaultCellStyle = ExcelUtils.getDefaultCellStyle(wb);
				XSSFCellStyle textCellStyle = ExcelUtils.getTextCellStyle(wb);
				XSSFCellStyle dateCellStyle = ExcelUtils.getDateCellStyle(wb);
				XSSFCellStyle currencyCellStyle = ExcelUtils.getCurrencyCellStyle(wb);

				Row row;
				Cell cell;

				ExcelUtils.fillCompanyLogo(wb, sheet, 8);
				row = sheet.getRow(0);
				cell = row.getCell(0);
				if (criteria.getBranch() == null) {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Life Policy Report ( All )");
				} else {
					cell.setCellValue(ApplicationSetting.getCompanyLabel() + " \n Life Policy Report ( " + criteria.getBranch().getName() + " )");
				}

				int i = 2;
				int index = 0;
				int startIndex;
				String strFormula;
				String GrandSIFormula = "";
				String GrandPremiumFormula = "";
				Map<String, List<LifePolicyReport>> map = separateBranch();
				for (List<LifePolicyReport> branchList : map.values()) {
					startIndex = i + 1 + 1;
					for (LifePolicyReport lifePolicyReport : branchList) {
						index = index + 1;
						row = sheet.createRow(i);

						// index
						cell = row.createCell(0);
						cell.setCellValue(index);
						cell.setCellStyle(defaultCellStyle);

						// policyNo
						cell = row.createCell(1);
						cell.setCellValue(lifePolicyReport.getPolicyNo());
						cell.setCellStyle(textCellStyle);

						// proposalNo
						cell = row.createCell(2);
						cell.setCellValue(lifePolicyReport.getProposalNo());
						cell.setCellStyle(textCellStyle);

						// receiptNo
						cell = row.createCell(3);
						cell.setCellValue(lifePolicyReport.getReceiptNo());
						cell.setCellStyle(textCellStyle);

						// cashReceiptDate
						cell = row.createCell(4);
						if (lifePolicyReport.getCashReceiptDate() != null) {
							cell.setCellValue(lifePolicyReport.getCashReceiptDate());
							cell.setCellStyle(dateCellStyle);
						}

						// customerName
						cell = row.createCell(5);
						cell.setCellValue(lifePolicyReport.getCustomerName());
						cell.setCellStyle(textCellStyle);

						// address
						cell = row.createCell(6);
						cell.setCellValue(lifePolicyReport.getAddress());
						cell.setCellStyle(textCellStyle);

						// sumInsured
						cell = row.createCell(7);
						cell.setCellValue(lifePolicyReport.getSumInsured());
						cell.setCellStyle(currencyCellStyle);

						// premium
						cell = row.createCell(8);
						cell.setCellValue(lifePolicyReport.getPremium());
						cell.setCellStyle(currencyCellStyle);

						// branch
						cell = row.createCell(9);
						cell.setCellValue(lifePolicyReport.getBranchName());
						cell.setCellStyle(textCellStyle);

						// remark
						cell = row.createCell(10);
						cell.setCellStyle(textCellStyle);
						i = i + 1;
					}
					sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
					row = sheet.createRow(i);

					cell = row.createCell(0);
					cell.setCellValue("Sub Total");
					cell.setCellStyle(defaultCellStyle);
					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);

					cell = row.createCell(7);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(H" + startIndex + ":H" + i + ")";
					GrandSIFormula += "H" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);

					cell = row.createCell(8);
					cell.setCellStyle(currencyCellStyle);
					strFormula = "SUM(I" + startIndex + ":I" + i + ")";
					GrandPremiumFormula += "I" + (i + 1) + "+";
					cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
					cell.setCellFormula(strFormula);
					ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 9, 10), sheet, wb);
					i = i + 1;
				}
				sheet.addMergedRegion(new CellRangeAddress(i, i, 0, 6));
				row = sheet.createRow(i);

				cell = row.createCell(0);
				cell.setCellValue("Grand Total");
				cell.setCellStyle(defaultCellStyle);
				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 0, 6), sheet, wb);

				cell = row.createCell(7);
				cell.setCellStyle(currencyCellStyle);
				GrandSIFormula = GrandSIFormula.substring(0, GrandSIFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandSIFormula);

				cell = row.createCell(8);
				cell.setCellStyle(currencyCellStyle);
				GrandPremiumFormula = GrandPremiumFormula.substring(0, GrandPremiumFormula.length() - 1);
				cell.setCellType(XSSFCell.CELL_TYPE_FORMULA);
				cell.setCellFormula(GrandPremiumFormula);

				ExcelUtils.setRegionBorder(CellStyle.BORDER_THIN, new CellRangeAddress(i, i, 9, 10), sheet, wb);

				wb.setPrintArea(0, "$A$1:$K$" + (i + 1));
				wb.write(op);
				op.flush();
				op.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
