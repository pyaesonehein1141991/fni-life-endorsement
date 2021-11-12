package org.ace.insurance.web.manage.travel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.currency.service.interfaces.ICurrencyService;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.system.common.salesPoints.SalesPoints;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.SaleChannelType;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
//import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewTravelProposalActionBean")
public class AddNewTravelProposalActionBean extends BaseBean implements Serializable {
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
	
	@ManagedProperty(value = "#{CurrencyService}")
	private ICurrencyService currencyService;

	public void setCurrencyService(ICurrencyService currencyService) {
		this.currencyService = currencyService;
	}
	
	private final String formID = "travelProposalEntryForm";
	private User user;
	private TravelProposal travelProposal;
	private TravelExpress travelExpress;
	private Map<String, TravelExpress> expressMap;
	private Product product;

	private User responsiblePerson;
	private boolean isCreateNew;
	private Boolean isConfirmEdit;
	private Boolean isEnquiryEdit;
	List<Product> productList;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (TravelProposal) getParam("editTravelProposal");
		isEnquiryEdit = (Boolean) getParam("isEnquiryEdit") == null ? false : true;
		isConfirmEdit = getParam("isConfirmEdit") == null ? false : true;
		destroy();
	}

	@PreDestroy
	public void destroy() {
		removeParam("editTravelProposal");
		removeParam("isEnquiryEdit");
		removeParam("isConfirmEdit");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		productList = productService.findProductByInsuranceType(InsuranceType.TRAVEL_INSURANCE);
		product = productList.get(0);
		if (travelProposal != null) {
			expressMap = new HashMap<String, TravelExpress>();
			for (TravelExpress e : travelProposal.getExpressList()) {
				expressMap.put(e.getTempId(), e);
			}
			createNewExpress();

		} else {
			expressMap = new HashMap<String, TravelExpress>();
			createNewProposal();
			createNewExpress();
			isCreateNew = true;
			travelProposal.setSubmittedDate(new Date());
			travelProposal.setBranch(user.getLoginBranch());
		}

	}

	public void addExpress() {
		if (isValidateTravelExpress()) {
			double premiumRate = travelProposalService.findPremiumRateByProductID(travelExpress.getProduct().getId());
			double totalPremium = premiumRate * travelExpress.getNoOfUnit();
			travelExpress.setPremium(totalPremium);
			travelExpress.setSumInsured(travelExpress.getProduct().getSumInsuredPerUnit() * travelExpress.getNoOfUnit());
			expressMap.put(travelExpress.getTempId(), travelExpress);
		}
		createNewExpress();
	}

	public void createNewExpress() {
		travelExpress = new TravelExpress();
		isCreateNew = true;

	}

	public void createNewProposal() {
		travelProposal = new TravelProposal();
	}

	public String addNewTravelPropoasl() {
		String result = null;
		if (isValidTravelProposal()) {
			try {
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), travelProposal.getBranch().getId(), null, WorkflowTask.CONFIRMATION, ReferenceType.SPECIAL_TRAVEL,
						TransactionType.UNDERWRITING, user, responsiblePerson);
				travelProposal.setExpressList(getExpressList());
				if (isConfirmEdit) {
					travelProposalService.updateTravelProposal(travelProposal, workFlowDTO);
				} else {
					travelProposalService.addNewTravelProposal(travelProposal, workFlowDTO);
				}
				outjectTravelProposal(travelProposal);

				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
				createNewProposal();
				result = "dashboard";
			} catch (SystemException ex) {
				handelSysException(ex);
			}
		}
		return result;
	}
	
	public void prepareEditExpressInfo(TravelExpress express) {
		this.travelExpress = express;
		isCreateNew = false;
	}

	public void removeExpress(TravelExpress express) {
		expressMap.remove(express.getTempId());
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnAgent(SelectEvent event) {
		Agent agent = (Agent) event.getObject();
		travelProposal.setAgent(agent);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		travelProposal.setPaymentType(paymentType);
	}

	public void selectUser() {
		selectUser(WorkflowTask.CONFIRMATION, WorkFlowType.SPECIAL_TRAVEL);
	}

	public void returnSalesPoints(SelectEvent event) {
		SalesPoints salesPoints = (SalesPoints) event.getObject();
		travelProposal.setSalesPoints(salesPoints);
	}

	public void returnExpress(SelectEvent event) {
		Express express = (Express) event.getObject();
		this.travelExpress.setExpress(express);
	}

	public void selectPaymentType() {
		selectPaymentType(product);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public List<TravelExpress> getExpressList() {
		return new ArrayList<TravelExpress>(expressMap.values());
	}

	public SaleChannelType[] getSaleChannel() {
		SaleChannelType[] types = { SaleChannelType.AGENT, SaleChannelType.WALKIN, SaleChannelType.DIRECTMARKETING };
		return types;
	}

	public void changeSaleEvent(AjaxBehaviorEvent event) {
		travelProposal.setAgent(null);
	}

	public boolean getIsCreateNew() {
		return isCreateNew;
	}

	public TravelExpress getTravelExpress() {
		return travelExpress;
	}

	public void setTravelExpress(TravelExpress travelExpress) {
		this.travelExpress = travelExpress;
	}

	public Boolean getIsConfirmEdit() {
		return isConfirmEdit;
	}

	public void setIsConfirmEdit(Boolean isConfirmEdit) {
		this.isConfirmEdit = isConfirmEdit;
	}

	public Boolean getIsEnquiryEdit() {
		return isEnquiryEdit;
	}

	public void setIsEnquiryEdit(Boolean isEnquiryEdit) {
		this.isEnquiryEdit = isEnquiryEdit;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public List<Currency> getCurrencyList() {
		return currencyService.findAllCurrency();
	}

	private boolean isValidateTravelExpress() {
		boolean valid = true;

		if (isEmpty(travelExpress.getExpress())) {
			addErrorMessage(formID + ":express", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (travelExpress.getNoOfPassenger() == 0) {
			addErrorMessage(formID + ":noOfPassenger", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (travelExpress.getNoOfUnit() == 0) {
			addErrorMessage(formID + ":noOfUnits", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (travelExpress.getNoOfUnit() > (travelExpress.getNoOfPassenger()*2)) {
			addErrorMessage(formID + ":noOfUnits", MessageId.OVER_TOTAL_UNIT);
		   valid = false;
	    }
		
		if (travelExpress.getProduct() == null) {
			addErrorMessage(formID + ":productName", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (!expressMap.isEmpty() && travelExpress.getProduct() != null) {
			boolean notExitProduct = false;
			for (Map.Entry<String, TravelExpress> entry : expressMap.entrySet()) {
				TravelExpress prevExpress = entry.getValue();
				if (!travelExpress.getProduct().getId().equals(prevExpress.getProduct().getId())) {
					notExitProduct = true;
				}
			}
			if (notExitProduct) {
				addErrorMessage(formID + ":productName", MessageId.INVALID_PRODUCT);
				valid = false;
			}
		}
		return valid;
	}

	private boolean isValidTravelProposal() {
		boolean valid = true;
		if (expressMap.isEmpty()) {
			addErrorMessage(formID + ":expressListPanel", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;

	}

	private void outjectTravelProposal(TravelProposal travelProposal) {
		putParam("travelProposal", travelProposal);
	}
}
