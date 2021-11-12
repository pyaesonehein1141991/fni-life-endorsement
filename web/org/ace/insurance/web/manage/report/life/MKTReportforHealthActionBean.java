package org.ace.insurance.web.manage.report.life;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import org.ace.insurance.medical.policy.MPL002;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.report.config.AgentJobConfigLoader;
import org.ace.insurance.report.config.service.interfaces.IReportConfigService;
import org.ace.insurance.report.life.MKTforHealthReportCriteria;
import org.ace.insurance.report.life.MKTforHealthReportDTO;
import org.ace.insurance.report.life.service.interfaces.IMKTReportforHealthService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.user.User;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

@ViewScoped
@ManagedBean(name = "MKTReportforHealthActionBean")
public class MKTReportforHealthActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{MKTReportforHealthService}")
	private IMKTReportforHealthService mktReportforHealthService;

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	public IMKTReportforHealthService getMktReportforHealthService() {
		return mktReportforHealthService;
	}

	public void setMktReportforHealthService(IMKTReportforHealthService mktReportforHealthService) {
		this.mktReportforHealthService = mktReportforHealthService;
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

	private MKTforHealthReportCriteria criteria;
	private List<MKTforHealthReportDTO> mktforhealthList;
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
		mktforhealthList = new ArrayList<MKTforHealthReportDTO>();
	}

	public void resetCriteria() {
		criteria = new MKTforHealthReportCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);

		mktforhealthList = new ArrayList<MKTforHealthReportDTO>();
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
			criteria.getProductIdList().removeAll(criteria.getProductIdList());
			for (Product p : selectedProductList) {
				criteria.getProductIdList().add(p.getId());
			}
		}
		mktforhealthList = mktReportforHealthService.findmktforhealth(criteria);
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
		String fileName = "MKTReportforHealth.xlsx";
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		try {
			OutputStream op = ec.getResponseOutputStream();
			MKTforHealthReportExcel mktExportExcel = new MKTforHealthReportExcel();
			mktExportExcel.generate(op, mktforhealthList);
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

	public MKTforHealthReportCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MKTforHealthReportCriteria criteria) {
		this.criteria = criteria;
	}

	public List<MKTforHealthReportDTO> getMktforhealthList() {
		return mktforhealthList;
	}

	public void setMktforhealthList(List<MKTforHealthReportDTO> mktforhealthList) {
		this.mktforhealthList = mktforhealthList;
	}

	public List<Product> getSelectedProductList() {
		return selectedProductList;
	}

	public void setSelectedProductList(List<Product> selectedProductList) {
		this.selectedProductList = selectedProductList;
	}

	public void returnLifePolicyNo(SelectEvent event) {
		MPL002 medicalPolicysearch = (MPL002) event.getObject();
		criteria.setPolicyNo(medicalPolicysearch.getPolicyNo());
	}
}
