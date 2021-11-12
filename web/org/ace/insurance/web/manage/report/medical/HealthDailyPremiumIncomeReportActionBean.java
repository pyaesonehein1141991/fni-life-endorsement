package org.ace.insurance.web.manage.report.medical;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.report.config.AgentJobConfigLoader;
import org.ace.insurance.report.config.service.interfaces.IReportConfigService;
import org.ace.insurance.report.medical.HealthDailyPremiumReportDTO;
import org.ace.insurance.report.medical.service.interfaces.IHealthDailyIncomeReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.model.DualListModel;

@ViewScoped
@ManagedBean(name = "HealthDailyPremiumIncomeReportActionBean")
public class HealthDailyPremiumIncomeReportActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{HealthDailyIncomeReportService}")
	private IHealthDailyIncomeReportService healthDailyIncomeReportService;

	public void setHealthDailyIncomeReportService(IHealthDailyIncomeReportService healthDailyIncomeReportService) {
		this.healthDailyIncomeReportService = healthDailyIncomeReportService;
	}

	@ManagedProperty(value = "#{ReportConfigService}")
	private IReportConfigService reportConfigService;

	public void setReportConfigService(IReportConfigService reportConfigService) {
		this.reportConfigService = reportConfigService;
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

	private HealthDailyIncomeReportCriteria criteria;
	private List<HealthDailyPremiumReportDTO> healthDailyReportList;
	private List<Branch> branchList;
	private List<Product> productList;
	private List<Product> selectProductList;
	private boolean accessBranches;
	private User user;
	private DualListModel<String> dualListModel;
	private List<String> source;
	private List<String> target;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		productList = productService.findProductByInsuranceType(InsuranceType.HEALTH);
		resetCriteria();
		selectProductList = new ArrayList<Product>();
		loadDualListModel();

	}

	public void resetCriteria() {
		criteria = new HealthDailyIncomeReportCriteria();
		criteria.setBranchId(user.getLoginBranch().getId());
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		healthDailyReportList = new ArrayList<HealthDailyPremiumReportDTO>();
		selectProductList = new ArrayList<Product>();
	}

	public void filter() {
		if (!selectProductList.isEmpty()) {
			for (Product p : selectProductList) {
				criteria.getProductIdList().add(p.getId());
			}
		}

		healthDailyReportList = healthDailyIncomeReportService.findHealthDailyPremiumReportDTO(criteria);
		selectProductList.clear();
	}

	private void loadDualListModel() {
		source = AgentJobConfigLoader.getKeys();
		target = new ArrayList<String>();
		target.add("HEALTHDAILYINCOME");
		dualListModel = new DualListModel<String>(source, target);
	}

	public void submit() {
		try {
			reportConfigService.configReport(dualListModel.getTarget());
			loadDualListModel();
			addInfoMessage(null, MessageId.REPORT_CONFIG_SUCCESS);
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void exportHealthDailyReport() {
		ExternalContext ec = getFacesContext().getExternalContext();
		ec.responseReset();
		ec.setResponseContentType("application/vnd.ms-excel");
		String fileName = "HealthDailyPremiumIncomeReport.xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try {
			OutputStream op = ec.getResponseOutputStream();
			HealthDailyPremiumReportExcel accpetedLetterExportExcel = new HealthDailyPremiumReportExcel();
			accpetedLetterExportExcel.generate(op, healthDailyReportList, criteria.getStartDate(), criteria.getEndDate());
			getFacesContext().responseComplete();

		} catch (IOException e) {
			throw new SystemException(null, "Failed to export HealthDailyPremiumReport.xlsx", e);
		}
	}

	public List<Branch> getBranches() {
		return user.getAccessBranchList();
	}

	public HealthDailyIncomeReportCriteria getCriteria() {
		return criteria;
	}

	public List<HealthDailyPremiumReportDTO> getHealthDailyReportList() {
		return healthDailyReportList;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

	public boolean isAccessBranches() {
		return accessBranches;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public List<Product> getSelectProductList() {
		return selectProductList;
	}

	public void setSelectProductList(List<Product> selectProductList) {
		this.selectProductList = selectProductList;
	}

}
