package org.ace.insurance.web.manage.enquires.travel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ProposalType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.agent.Agent;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.customer.Customer;
import org.ace.insurance.system.common.organization.Organization;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.PTPL001;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.document.DocumentBuilder;
import org.ace.insurance.web.manage.enquires.EnquiryCriteria;
import org.ace.insurance.workflow.WorkFlow;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "PersonTravelProposalEnquiryActionBean")
public class PersonTravelProposalEnquiryActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
	}

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
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

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	private PersonTravelProposal travelProposal;
	private User user;
	private List<PTPL001> travelProposalList;
	private List<Product> productList;
	private EnquiryCriteria criteria;
	boolean approvedProposal = false;
	private String message;
	private List<Branch> branchList;

	String pdfDirPath = "";
	String fileName = "";
	String dirPath = "";

	String issuereportName = "TravelPolicyIssue";
	String issuePdfDirPath = "/pdf-report/" + issuereportName + "/" + System.currentTimeMillis() + "/";
	String issueDirPath = getWebRootPath() + issuePdfDirPath;
	String issueFileName = issuereportName + ".pdf";

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		productList = productService.findProductByInsuranceType(InsuranceType.PERSON_TRAVEL);
		branchList = branchService.findAllBranch();
		resetCriteria();
	}

	public void resetCriteria() {
		criteria = new EnquiryCriteria();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		criteria.setStartDate(cal.getTime());
		Date endDate = new Date();
		criteria.setEndDate(endDate);
		criteria.setPolicyNo("");
		criteria.setProposalType(ProposalType.UNDERWRITING);
		criteria.setBranch(user.getLoginBranch());
		//criteria.setAccessibleBranchIdList(user.getAccessibleBranchIdList());
		travelProposalList = new ArrayList<PTPL001>();
	}

	public void findTravelProposalListByEnquiryCriteria() {
		this.travelProposalList = personTravelProposalService.findPersonTravelDTOByCriteria(criteria);
	}

	public List<String> getAllBranchIdList() {
		List<String> allbranch = new ArrayList<String>();
		for (Branch b : branchList) {
			allbranch.add(b.getId());
		}
		return allbranch;
	}

	public void findPersonTravelProposalListByAllBranch() {
		criteria.setAccessibleBranchIdList(getAllBranchIdList());
		this.travelProposalList = personTravelProposalService.findPersonTravelDTOByCriteria(criteria);
	}

	public void showDetailtravelProposal(PTPL001 proposalDTO) {
		this.travelProposal = personTravelProposalService.findPersonTravelProposalById(proposalDTO.getId());
		putParam("personTravelProposal", travelProposal);
		putParam("workFlowList", getWorkFlowList());
		openPersonTravelProposalInfoTemplate();
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(travelProposal.getId());
	}

	public String editPersonTravelProposal(PTPL001 proposalDTO) {
		String result = null;
		if (allowToEdit(proposalDTO.getId())) {
			travelProposal = personTravelProposalService.findPersonTravelProposalById(proposalDTO.getId());
			putParam("personTravelProposal", travelProposal);
			putParam("isEnquiryEdit", true);
			result = "editPersonTravelProposal";

		}
		return result;

	}

	public String editInsurancePerson(PTPL001 proposalDTO) {
		//String result = null;
		travelProposal = personTravelProposalService.findPersonTravelProposalById(proposalDTO.getId());
		outjectTravelProposal(travelProposal);
		//putParam("isInsurancePersonEdit", true);
		return "editPersonTravelProposal";
	}

	public void outjectTravelProposal(PersonTravelProposal personTravelProposal) {
		putParam("personTravelProposal", personTravelProposal);
	}

	/********************************************
	 * PDF Document Generation
	 ***************************************/
	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void handleCloseIssuePolicy(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(issueDirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getIssueReportStream() {
		return issuePdfDirPath + issueFileName;
	}

	public String generatePaymentInvoiceLetter(PTPL001 personTravelProposalDTO) {
		if (allowToPrint(personTravelProposalDTO.getId(), WorkflowTask.FINISHED, WorkflowTask.ISSUING, WorkflowTask.PAYMENT)) {
			PersonTravelProposal personTravelProposal = personTravelProposalService.findPersonTravelProposalById(personTravelProposalDTO.getId());
			List<Payment> paymentList = paymentService.findByProposal(personTravelProposal.getId(), PolicyReferenceType.TRAVEL_POLICY, null);
			String reportName = "TravelPaymentInvoice";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			String dirPath = getWebRootPath() + pdfDirPath;
			fileName = reportName + ".pdf";
			DocumentBuilder.generatePersonTravelPaymentInvoiceLetter(personTravelProposal, paymentList.get(0), dirPath, fileName);
			RequestContext.getCurrentInstance().execute("PF('pdfPreviewDialog').show();");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
			RequestContext.getCurrentInstance().execute("PF('informationDialog').show();");
		}
		return null;
	}

	public void generateCashReceipt(PTPL001 proposalDTO) {
		if (allowToPrint(proposalDTO.getId(), WorkflowTask.FINISHED, WorkflowTask.ISSUING)) {
			PersonTravelProposal personTravelProposal = personTravelProposalService.findPersonTravelProposalById(proposalDTO.getId());
			List<Payment> paymentList = paymentService.findByProposal(personTravelProposal.getId(), PolicyReferenceType.TRAVEL_POLICY, null);
			String reportName = "TravelCashReceipt";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			fileName = reportName + ".pdf";
			DocumentBuilder.generatePersonTravelCashReceiptLetter(personTravelProposal, paymentList.get(0), dirPath, fileName);
			RequestContext.getCurrentInstance().execute("PF('pdfPreviewDialog').show();");
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
			RequestContext.getCurrentInstance().execute("PF('informationDialog').show();");
		}
	}

	public void generatePersonTravelIssue(PTPL001 personTravelProposalDTO) throws Exception {
		if (allowToPrint(personTravelProposalDTO.getId(), WorkflowTask.FINISHED)) {
			PersonTravelPolicy personTravlePolicy = personTravelPolicyService.findPersonTravelPolicyByProposalId(personTravelProposalDTO.getId());
			String reportName = "TravelPolicyIssue";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			fileName = reportName + ".pdf";
			DocumentBuilder.generatePersonTravelPolicyIssue(personTravlePolicy, dirPath, fileName);
			RequestContext.getCurrentInstance().execute("PF('pdfPreviewDialog').show();");

		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
			RequestContext.getCurrentInstance().execute("PF('informationDialog').show();");
		}
	}

	public PersonTravelProposal getTravelProposal() {
		return travelProposal;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public List<Product> getProductList() {
		return productList;
	}

	public List<PTPL001> getTravelProposalList() {
		return travelProposalList;
	}

	public String getMessage() {
		return message;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Branch> getAllBranches() {
		return branchList;
	}

	private boolean allowToEdit(String refNo) {
		boolean flag = true;
		WorkFlow wf = workFlowService.findWorkFlowByRefNo(refNo);
		if (wf == null) {
			flag = false;
			this.message = "This proposal has been legalized as policy contract.";
			RequestContext.getCurrentInstance().execute("PF('informationDialog').show();");
		} else {
			if (wf.getWorkflowTask().equals(WorkflowTask.PAYMENT) || wf.getWorkflowTask().equals(WorkflowTask.ISSUING)) {
				flag = false;
				this.message = "This proposal is not in the editable workflow phase. It's currently at " + wf.getWorkflowTask().getLabel() + " phase";
				RequestContext.getCurrentInstance().execute("PF('informationDialog').show();");
			}
		}
		return flag;
	}

	/**
	 * Allow to print when workflowTask of current WorkFlow contains in
	 * workflowTasks(param)
	 */
	private boolean allowToPrint(String proposalId, WorkflowTask... workflowTasks) {
		WorkFlow workflow = workFlowService.findWorkFlowByRefNo(proposalId);
		WorkflowTask wfTask = workflow == null ? WorkflowTask.FINISHED : workflow.getWorkflowTask();
		boolean isallowed = false;
		if (Arrays.asList(workflowTasks).contains(wfTask)) {
			isallowed = true;
		}
		return isallowed;
	}
}
