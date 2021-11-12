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


import org.ace.insurance.report.travel.service.interfaces.ITravelMonthlyReportService;
import org.ace.insurance.report.travel.view.TravelMonthlyReportView;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.web.manage.report.common.MonthlyReportNewCriteria;
import org.ace.insurance.web.manage.report.travel.TravelMonthlyReportExcel;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "TravelMonthlyReportActionBean")
public class TravelMonthlyReportActionBean extends BaseBean {
	@ManagedProperty(value = "#{TravelMonthlyReportService}")
	private ITravelMonthlyReportService travelMonthlyReportService;

	public void setTravelMonthlyReportService(ITravelMonthlyReportService travelMonthlyReportService) {
		this.travelMonthlyReportService = travelMonthlyReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private List<TravelMonthlyReportView> viewList;
	private MonthlyReportNewCriteria criteria;
	private List<Branch> branchList;

	@PostConstruct
	public void init() {
		viewList = new ArrayList<TravelMonthlyReportView>();
		branchList = branchService.findAllBranch();
		resetCriteria();
	}

	public void filter() {
		try {
			viewList = travelMonthlyReportService.findTravelMonthlyReport(criteria);
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void resetCriteria() {
		criteria = new MonthlyReportNewCriteria();
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
		String fileName = "SpecialTravel_Monthly_Report.xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try (OutputStream op = ec.getResponseOutputStream();) {
			TravelMonthlyReportExcel monthlyIncomeExcel = new TravelMonthlyReportExcel();
			Branch branch = null;
			if (criteria.getBranchId() != null)
				branch = getBranchList().stream().filter(b -> criteria.getBranchId().equals(b.getId())).findAny().orElse(null);
			criteria.setBranchName(branch != null ? branch.getName() : "All Branches");
			monthlyIncomeExcel.generate(op, viewList, criteria);
			getFacesContext().responseComplete();
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export SpecialTravel_Monthly_Report.xlsx", e);
		}
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

	public List<TravelMonthlyReportView> getViewList() {
		return viewList;
	}

	public MonthlyReportNewCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MonthlyReportNewCriteria criteria) {
		this.criteria = criteria;
	}

}
