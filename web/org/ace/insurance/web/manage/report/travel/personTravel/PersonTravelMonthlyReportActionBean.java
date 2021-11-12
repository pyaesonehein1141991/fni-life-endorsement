package org.ace.insurance.web.manage.report.travel.personTravel;

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
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.report.common.MonthlyReportNewCriteria;
import org.ace.insurance.report.personTravel.PersonTravelMonthlyReportExcel;
import org.ace.insurance.report.personTravel.services.interfaces.IPersonTravelMonthlyReportService;
import org.ace.insurance.report.personTravel.view.PersonTravelMonthlyReportView;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "PersonTravelMonthlyReportActionBean")
public class PersonTravelMonthlyReportActionBean extends BaseBean {
	@ManagedProperty(value = "#{PersonTravelMonthlyReportService}")
	private IPersonTravelMonthlyReportService personTravelMonthlyReportService;

	public void setPersonTravelMonthlyReportService(IPersonTravelMonthlyReportService personTravelMonthlyReportService) {
		this.personTravelMonthlyReportService = personTravelMonthlyReportService;
	}

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	private List<PersonTravelMonthlyReportView> viewList;
	private MonthlyReportNewCriteria criteria;
	private List<Branch> branchList;
	private List<Product> productList;

	@PostConstruct
	public void init() {
		viewList = new ArrayList<PersonTravelMonthlyReportView>();
		branchList = branchService.findAllBranch();
		productList = productService.findProductByInsuranceType(InsuranceType.PERSON_TRAVEL);
		resetCriteria();
	}

	public void filter() {
		try {
			viewList = personTravelMonthlyReportService.findPersonTravelMonthlyReport(criteria);
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
		String fileName = "PersonTravel_Monthly_Report.xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try (OutputStream op = ec.getResponseOutputStream();) {
			PersonTravelMonthlyReportExcel monthlyIncomeExcel = new PersonTravelMonthlyReportExcel();
			Branch branch = null;
			if (criteria.getBranchId() != null)
				branch = getBranchList().stream().filter(b -> criteria.getBranchId().equals(b.getId())).findAny().orElse(null);
			criteria.setBranchName(branch != null ? branch.getName() : "All Branches");
			monthlyIncomeExcel.generate(op, viewList, criteria);
			getFacesContext().responseComplete();
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export PersonTravel_Monthly_Report.xlsx", e);
		}
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

	public List<PersonTravelMonthlyReportView> getViewList() {
		return viewList;
	}

	public MonthlyReportNewCriteria getCriteria() {
		return criteria;
	}

	public List<Product> getProductList() {
		return productList;
	}

}
