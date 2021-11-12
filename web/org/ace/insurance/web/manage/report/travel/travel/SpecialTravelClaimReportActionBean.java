//package org.ace.insurance.web.manage.report.travel.travel;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//
//import javax.annotation.PostConstruct;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.ManagedProperty;
//import javax.faces.bean.ViewScoped;
//import javax.faces.context.ExternalContext;
//
//import org.ace.insurance.common.ErrorCode;
//import org.ace.insurance.common.Utils;
//import org.ace.insurance.report.travel.SpecialTravelClaimReportExcel;
//import org.ace.insurance.system.common.branch.Branch;
//import org.ace.insurance.travel.claim.SpecialTravelClaimReportView;
//import org.ace.insurance.travel.claim.TravelClaimReportCriteria;
//import org.ace.insurance.travel.claim.service.interfaces.ITravelClaimService;
//import org.ace.insurance.user.User;
//import org.ace.java.component.SystemException;
//import org.ace.java.web.common.BaseBean;
//
//@ViewScoped
//@ManagedBean(name = "SpecialTravelClaimReportActionBean")
//public class SpecialTravelClaimReportActionBean extends BaseBean {
//	@ManagedProperty(value = "#{TravelClaimService}")
//	private ITravelClaimService travelClaimService;
//
//	private User user;
//	private TravelClaimReportCriteria criteria;
//	private List<SpecialTravelClaimReportView> criteriaList;
//	private List<Branch> branchList;
//
//	@PostConstruct
//	private void init() {
//		criteriaList = new ArrayList<SpecialTravelClaimReportView>();
//		user = (User) getParam("LoginUser");
//		resetCriteria();
//	}
//
//	public void resetCriteria() {
//		criteria = new TravelClaimReportCriteria();
//		branchList = user.getAccessBranchList();
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.MONTH, 0);
//		int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//		int min = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
//		cal.set(Calendar.DAY_OF_MONTH, min);
//		criteria.setFromDate(cal.getTime());
//		cal.set(Calendar.DAY_OF_MONTH, max);
//		criteria.setToDate(cal.getTime());
//	}
//
//	public void filter() {
//		criteriaList = travelClaimService.findSpiecialTravelClaimByCriteria(criteria);
//	}
//
//	public void exportExcel() {
//		ExternalContext ec = getFacesContext().getExternalContext();
//		ec.responseReset();
//		ec.setResponseContentType("application/vnd.ms-excel");
//		String fileName = "SpecialTravel_Claim_Report.xlsx";
//		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//		try (OutputStream op = ec.getResponseOutputStream();) {
//			SpecialTravelClaimReportExcel excel = new SpecialTravelClaimReportExcel(Utils.getDateFormatString(criteria.getFromDate()),
//					Utils.getDateFormatString(criteria.getToDate()), criteriaList);
//			excel.generate(op, criteriaList, criteria);
//			getFacesContext().responseComplete();
//		} catch (IOException e) {
//			throw new SystemException(ErrorCode.SYSTEM_ERROR, "Failed to export SpecialTravel_Claim_Report.xlsx", e);
//		}
//	}
//
//	public ITravelClaimService getTravelClaimService() {
//		return travelClaimService;
//	}
//
//	public User getUser() {
//		return user;
//	}
//
//	public TravelClaimReportCriteria getCriteria() {
//		return criteria;
//	}
//
//	public List<SpecialTravelClaimReportView> getCriteriaList() {
//		return criteriaList;
//	}
//
//	public List<Branch> getBranchList() {
//		return branchList;
//	}
//
//	public void setTravelClaimService(ITravelClaimService travelClaimService) {
//		this.travelClaimService = travelClaimService;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//
//	public void setCriteria(TravelClaimReportCriteria criteria) {
//		this.criteria = criteria;
//	}
//
//	public void setCriteriaList(List<SpecialTravelClaimReportView> criteriaList) {
//		this.criteriaList = criteriaList;
//	}
//
//	public void setBranchList(List<Branch> branchList) {
//		this.branchList = branchList;
//	}
//
//}
