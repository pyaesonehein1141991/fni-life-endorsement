package org.ace.insurance.web.manage.enquires.travel;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.TPL001;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "PersonTravelPolicyEnquiryActionBean")
public class PersonTravelPolicyEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	@ManagedProperty(value = "#{ProductService}")
	private IProductService productService;

	public void setProductService(IProductService productService) {
		this.productService = productService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private PersonTravelPolicy personTravelPolicy;
	private List<Product> productList;
	private List<TPL001> personTravelPolicyList;
	private EnquiryCriteria criteria;
	private User user;
	private Date tempDate;

	@PostConstruct
	public void init() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		resetCriteria();
		// findPersonTravelPolicyListByEnquiryCriteria();
	}

	public void findPersonTravelPolicyListByEnquiryCriteria() {
		personTravelPolicyList = personTravelPolicyService.findByEnquiryCriteria(criteria);
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setBranch(user.getLoginBranch());
		//criteria.setAccessibleBranchIdList(user.getAccessibleBranchIdList());

	}

	public String getPagePolicy(TPL001 persontravelpolicy) {
		PersonTravelPolicy personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyById(persontravelpolicy.getId());
		outjectPersonTravelPolicy(personTravelPolicy);
		return "personTravelPolicyAttachment";
	}

	public String editPersonTravelPolicy(TPL001 persontravelpolicy) {
		PersonTravelPolicy personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyById(persontravelpolicy.getId());
		outjectPersonTravelPolicy(personTravelPolicy);
		return "editPersonTravelPolicy";
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		criteria.setAgent(agent);
	}

	public void returnCustomer(SelectEvent event) {
		Customer customer = (Customer) event.getObject();
		criteria.setCustomer(customer);
	}

	public void returnOrganization(SelectEvent event) {
		Organization organization = (Organization) event.getObject();
		criteria.setOrganization(organization);
	}

	public void returnProduct(SelectEvent event) {
		Product product = (Product) event.getObject();
		criteria.setProduct(product);
	}

	public void selectProduct() {
		selectProduct(InsuranceType.PERSON_TRAVEL);
	}

	public void showDetailtravelPolicy(TPL001 personTravelPolicyDTO) {
		this.personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyById(personTravelPolicyDTO.getId());
		putParam("personTravelPolicy", personTravelPolicy);
		openTravelPolicyInfoTempleate();
	}

	public EnquiryCriteria getCriteria() {
		if (criteria == null) {
			criteria = new EnquiryCriteria();
		}
		return criteria;
	}

	public PersonTravelPolicy getPersonTravelPolicy() {
		return personTravelPolicy;
	}

	public EnumSet<SaleChannelType> getSaleChannelTypes() {
		EnumSet<SaleChannelType> results = EnumSet.allOf(SaleChannelType.class);
		return results;
	}

	public List<TPL001> getPersonTravelPolicyList() {
		return personTravelPolicyList;
	}

	public void setPersonTravelPolicyList(List<TPL001> personTravelPolicyList) {
		this.personTravelPolicyList = personTravelPolicyList;
	}

	public void setPersonTravelPolicy(PersonTravelPolicy personTravelPolicy) {
		this.personTravelPolicy = personTravelPolicy;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(personTravelPolicy.getPersonTravelPolicyInfo().getId());
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public List<Branch> getBranches() {
		return user.getAccessBranchList();
	}

	public IPersonTravelPolicyService getPersonTravelPolicyService() {
		return personTravelPolicyService;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public IProductService getProductService() {
		return productService;
	}

	public IWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	public Date getTempDate() {
		return tempDate;
	}

	public void setTempDate(Date tempDate) {
		this.tempDate = tempDate;
	}

	private void outjectPersonTravelPolicy(PersonTravelPolicy personTravelPolicy) {
		putParam("personTravelPolicy", personTravelPolicy);
	}
}
