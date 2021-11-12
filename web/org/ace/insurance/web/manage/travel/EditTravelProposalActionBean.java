package org.ace.insurance.web.manage.travel;

import java.io.Serializable;
import java.util.ArrayList;
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

import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "EditTravelProposalActionBean")
public class EditTravelProposalActionBean extends BaseBean implements Serializable {
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

	private User user;
	private TravelProposal travelProposal;
	private TravelExpress travelExpress;
	private Map<String, TravelExpress> expressMap;

	private User responsiblePerson;
	private boolean isCreateNew;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (TravelProposal) getParam("travelProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		expressMap = new HashMap<String, TravelExpress>();
		if (travelProposal != null) {
			for (TravelExpress e : travelProposal.getExpressList()) {
				expressMap.put(e.getTempId(), e);
			}
		} else {
			createNewProposal();
		}
		createNewExpress();
		isCreateNew = true;
	}

	@PreDestroy
	public void destroy() {
		removeParam("travelProposal");
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		travelProposal.setBranch(branch);
	}

	public void returnPaymentType(SelectEvent event) {
		PaymentType paymentType = (PaymentType) event.getObject();
		travelProposal.setPaymentType(paymentType);
	}

	public void selectUser() {
		selectUser(WorkflowTask.CONFIRMATION, WorkFlowType.SPECIAL_TRAVEL, TransactionType.UNDERWRITING, travelProposal.getBranch().getId(),null);
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

	public void addExpress() {
		if (isValidateTravelExpress()) {
			double premiumRate = travelProposalService.findPremiumRateByProductID(travelProposal.getExpressList().get(0).getProduct().getId());
			double totalPremium = premiumRate * travelExpress.getNoOfUnit();
			double commission = totalPremium / travelProposal.getExpressList().get(0).getProduct().getFirstCommission();
			double netPremium = totalPremium - commission;
			travelExpress.setPremium(netPremium);
			travelExpress.setCommission(commission);
			expressMap.put(travelExpress.getTempId(), travelExpress);
			createNewExpress();
		}
	}

	public String updateNewTravelPropoasl() {
		String result = null;
		try {
			if (isValidTravelProposal()) {
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), travelProposal.getBranch().getId(), null, WorkflowTask.CONFIRMATION, ReferenceType.SPECIAL_TRAVEL,
						TransactionType.UNDERWRITING, user, responsiblePerson);
				travelProposal.setExpressList(getTravelExpressList());
				travelProposalService.updateTravelProposal(travelProposal, workFlowDTO);
				outjectTravelProposal(travelProposal);

				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.UNDERWRITING_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
				createNewProposal();
				result = "dashboard";
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	private boolean isValidateTravelExpress() {
		boolean valid = true;
		String formID = "travelProposalEntryForm";
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
		return valid;
	}

	private boolean isValidTravelProposal() {
		boolean valid = true;
		String formID = "travelProposalEntryForm";
		if (isEmpty(travelProposal.getSubmittedDate())) {
			addErrorMessage(formID + ":submittedDate", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(travelProposal.getBranch())) {
			addErrorMessage(formID + ":branch", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(travelProposal.getPaymentType())) {
			addErrorMessage(formID + ":paymentType", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (isEmpty(responsiblePerson)) {
			addErrorMessage(formID + ":responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		if (expressMap.isEmpty()) {
			addErrorMessage(formID + ":expressTable", UIInput.REQUIRED_MESSAGE_ID);
			valid = false;
		}
		return valid;

	}

	public void createNewExpress() {
		travelExpress = new TravelExpress();
		isCreateNew = true;
	}

	public void createNewProposal() {
		travelProposal = new TravelProposal();
	}

	private List<TravelExpress> getTravelExpressList() {
		List<TravelExpress> expressList = new ArrayList<TravelExpress>();
		for (TravelExpress express : expressMap.values()) {
			express.setTravelProposal(travelProposal);
			expressList.add(express);
		}
		return expressList;
	}

	private void outjectTravelProposal(TravelProposal travelProposal) {
		putParam("travelProposal", travelProposal);
	}

	public void prepareEditExpressInfo(TravelExpress express) {
		this.travelExpress = express;
		this.travelExpress.setPremium(300);
		isCreateNew = false;
	}

	public void removeExpress(TravelExpress express) {
		expressMap.remove(express.getTempId());
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

	public void returnExpress(SelectEvent event) {
		Express express = (Express) event.getObject();
		this.travelExpress.setExpress(express);
	}

}
