package org.ace.insurance.web.manage.travel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.travel.expressTravel.TPL001;
import org.ace.insurance.travel.expressTravel.Tour;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "EnquiryExpressDetailActionBean")
public class EnquiryExpressDetailActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	private User user;
	private TravelExpress express;
	private List<TPL001> travelProposalList;
	private EnquiryCriteria criteria;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposalList = new ArrayList<TPL001>();
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		resetCriteria();
		createNewExpress();
		criteria.setBranch(user.getLoginBranch());
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -3);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setProposalNo("");
		criteria.setBranch(user.getBranch());
		//criteria.setAccessibleBranchIdList(user.getAuthorityList());
		travelProposalList = new ArrayList<TPL001>();
	}

	public void findTravelProposalListByEnquiryCriteria() {
		travelProposalList = travelProposalService.findTravelProposalByEnquiryCriteria(criteria);
	}

	public String prepareAddTour(TravelExpress travelExpress) {
		outjectTravelExpress(travelExpress);
		return "tour";
	}

	public double requireUnit(TravelExpress travelExpress) {
		int presentUnit = 0;
		for (Tour tour : travelExpress.getTourList()) {
			presentUnit += tour.getNoOfUnit();
		}
		return travelExpress.getNoOfUnit() - presentUnit;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public void createNewExpress() {
		express = new TravelExpress();
	}

	public TravelExpress getExpress() {
		return express;
	}

	public void setExpress(TravelExpress express) {
		this.express = express;
	}

	public List<TPL001> getTravelProposalList() {
		return travelProposalList;
	}

	private void outjectTravelExpress(TravelExpress travelExpress) {
		putParam("TravelExpress", travelExpress);
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Branch> getBranches() {
		return user.getAccessBranchList();
	}

}
