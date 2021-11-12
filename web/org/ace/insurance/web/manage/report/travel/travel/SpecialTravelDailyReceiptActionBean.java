package org.ace.insurance.web.manage.report.travel.travel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.Utils;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.travel.SpecialTravelDailyReceiptReport;
import org.ace.insurance.travel.expressTravel.service.interfaces.ISpecialTravelReportService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.manage.report.travel.SpecialTravelDailyReceiptReportExcel;
import org.ace.insurance.web.manage.report.travel.TravelReportCriteria;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "SpecialTravelDailyReceiptActionBean")
public class SpecialTravelDailyReceiptActionBean extends BaseBean {
	@ManagedProperty(value = "#{SpecialTravelReportService}")
	private ISpecialTravelReportService travelDailyIncomeReportService;

	public void setTravelDailyIncomeReportService(ISpecialTravelReportService travelDailyIncomeReportService) {
		this.travelDailyIncomeReportService = travelDailyIncomeReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private TravelReportCriteria criteria;
	private List<SpecialTravelDailyReceiptReport> specialTravelDailyReceiptReportList;
	private User user;
	private List<Branch> branchList;
	private boolean accessBranches;

	@PostConstruct
	private void init() {
		specialTravelDailyReceiptReportList = new ArrayList<SpecialTravelDailyReceiptReport>();
		user = (User) getParam("LoginUser");
		resetCriteria();
	}

	public void filter() {
		specialTravelDailyReceiptReportList = travelDailyIncomeReportService.findSpecialTravelByCriteria(criteria);
	}

	public void resetCriteria() {
		criteria = new TravelReportCriteria();
		branchList = user.getAccessBranchList();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 0);
		int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int min = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, min);
		criteria.setFromDate(cal.getTime());
		cal.set(Calendar.DAY_OF_MONTH, max);
		criteria.setToDate(cal.getTime());
	}

	public void exportExcel() {
		ExternalContext ec = getFacesContext().getExternalContext();
		ec.responseReset();
		ec.setResponseContentType("application/vnd.ms-excel");

		String fileName = "SpecialTravel_Daily_Receipt_Report.xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try (OutputStream op = ec.getResponseOutputStream();) {
			SpecialTravelDailyReceiptReportExcel excel = new SpecialTravelDailyReceiptReportExcel(Utils.getDateFormatString(criteria.getFromDate()),
					Utils.getDateFormatString(criteria.getToDate()), specialTravelDailyReceiptReportList);

			excel.generate(op, specialTravelDailyReceiptReportList, criteria);
			getFacesContext().responseComplete();
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export SpecialTravel_Daily_Receipt_Report.xlsx", e);
		}
	}

	public TravelReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(TravelReportCriteria criteria) {
		this.criteria = criteria;
	}

	public List<SpecialTravelDailyReceiptReport> getSpecialTravelDailyReceiptReportList() {
		return specialTravelDailyReceiptReportList;
	}

	public void setSpecialTravelDailyReceiptReportList(List<SpecialTravelDailyReceiptReport> specialTravelDailyReceiptReportList) {
		this.specialTravelDailyReceiptReportList = specialTravelDailyReceiptReportList;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public void setAccessBranches(boolean accessBranches) {
		this.accessBranches = accessBranches;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<Branch> branchList) {
		this.branchList = branchList;
	}
}
