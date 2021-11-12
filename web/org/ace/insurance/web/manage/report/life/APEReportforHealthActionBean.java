package org.ace.insurance.web.manage.report.life;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ErrorCode;
import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.MonthNames;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.report.config.AgentJobConfigLoader;
import org.ace.insurance.report.config.service.interfaces.IReportConfigService;
import org.ace.insurance.report.life.APEReportCriteria;
import org.ace.insurance.report.life.APEReportDTO;
import org.ace.insurance.report.life.APEReportforHealthCriteria;
import org.ace.insurance.report.life.APEReportforHealthDTO;
import org.ace.insurance.report.life.CeoReportCriteria;
import org.ace.insurance.report.life.CeoReportDTO;
import org.ace.insurance.report.life.LifeDailyIncomeReportCriteria;
import org.ace.insurance.report.life.LifeDailyIncomeReportDTO;
import org.ace.insurance.report.life.service.interfaces.IAPEReportforHealthService;
import org.ace.insurance.report.life.service.interfaces.ICeoReportService;
import org.ace.insurance.report.life.service.interfaces.ILifeDailyIncomeReportService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.model.DualListModel;

@ViewScoped
@ManagedBean(name = "APEReportforHealthActionBean")
public class APEReportforHealthActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{APEReportforHealthService}")
	private IAPEReportforHealthService apeReportforHealthService;

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	public void setApeReportforHealthService(IAPEReportforHealthService apeReportforHealthService) {
		this.apeReportforHealthService = apeReportforHealthService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{ReportConfigService}")
	private IReportConfigService reportConfigService;

	public void setReportConfigService(IReportConfigService reportConfigService) {
		this.reportConfigService = reportConfigService;
	}

	private APEReportforHealthCriteria criteriaforhealth;
	private List<APEReportforHealthDTO> apeforhealthList;
	private User user;
	private List<Product> productList;
	private List<Product> selectedProductList;
	private Map<String, String> productMap;
	private DualListModel<String> dualListModel;
	private List<String> source;
	private List<String> target;

	@PostConstruct
	public void init() {
		user = (User) getParam("LoginUser");
		createNew();
		resetCriteria();
		loadDualListModel();
		productList = productService.findProductByInsuranceType(InsuranceType.HEALTH);
		if (productList.size() > 0) {
			changeToProductMap();
		}
	}

	/* to render Product Name in UI */
	private void changeToProductMap() {
		productMap = new HashMap<>();
		for (Product product : productList) {
			productMap.put(product.getId(), product.getProductContent().getName());
		}
	}

	private void createNew() {
		productList = new ArrayList<>();
		apeforhealthList = new ArrayList<APEReportforHealthDTO>();
	}

	public void resetCriteria() {
		criteriaforhealth = new APEReportforHealthCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		
		apeforhealthList = new ArrayList<APEReportforHealthDTO>();
		selectedProductList = new ArrayList<Product>();
	}

	private void loadDualListModel() {
		source = AgentJobConfigLoader.getKeys();
		target = new ArrayList<String>();
		target.add("APE");
		dualListModel = new DualListModel<String>(source, target);
	}

	public void filter() {
		if (!selectedProductList.isEmpty()) {
			criteriaforhealth.getProductIdList().removeAll(criteriaforhealth.getProductIdList());
			for (Product p : selectedProductList) {
				criteriaforhealth.getProductIdList().add(p.getId());
			}
		}
		apeforhealthList = apeReportforHealthService.findapeforHealth(criteriaforhealth);
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

	public List<Branch> getBranchList() {
		return branchService.findAllBranch();

	}
	
	public Map<Integer, Integer> getYears() {
		SortedMap<Integer, Integer> years = new TreeMap<Integer, Integer>(Collections.reverseOrder());
		int endYear = Calendar.getInstance().get(Calendar.YEAR);
		for (int startYear = 1900; startYear <= endYear; startYear++) {
			years.put(startYear, startYear);
		}
		return years;
	}
	
	public EnumSet<MonthNames> getMonthSet() {
		return EnumSet.allOf(MonthNames.class);
	}

	public void exportExcel() {
		ExternalContext ec = getFacesContext().getExternalContext();
		ec.responseReset();
		ec.setResponseContentType("application/vnd.ms-excel");
		String fileName = "APEReport.xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try {
			OutputStream op = ec.getResponseOutputStream();
			APEReportExcelforHealth apeExportExcelforHealth = new APEReportExcelforHealth();
			apeExportExcelforHealth.generate(op, apeforhealthList);
			getFacesContext().responseComplete();
		} catch (IOException e) {
			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export APEReport.xlsx", e);
		}
	}

	public List<Branch> getBranches() {
		return user.getAccessBranchList();
	}

	public List<Product> getProductList() {
		return productList;
	}

	public APEReportforHealthCriteria getCriteriaforhealth() {
		return criteriaforhealth;
	}


	public void setCriteriaforhealth(APEReportforHealthCriteria criteriaforhealth) {
		this.criteriaforhealth = criteriaforhealth;
	}


	public List<APEReportforHealthDTO> getApeforhealthList() {
		return apeforhealthList;
	}


	public void setApeforhealthList(List<APEReportforHealthDTO> apeforhealthList) {
		this.apeforhealthList = apeforhealthList;
	}


	public IAPEReportforHealthService getApeReportforHealthService() {
		return apeReportforHealthService;
	}


	public List<Product> getSelectedProductList() {
		return selectedProductList;
	}

	public void setSelectedProductList(List<Product> selectedProductList) {
		this.selectedProductList = selectedProductList;
	}

}
