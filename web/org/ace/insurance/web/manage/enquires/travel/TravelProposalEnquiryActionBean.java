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

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.service.interfaces.IProductService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.branch.service.interfaces.IBranchService;
import org.ace.insurance.system.common.express.Express;
import org.ace.insurance.travel.expressTravel.TPL001;
import org.ace.insurance.travel.expressTravel.TravelExpress;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
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
@ManagedBean(name = "TravelProposalEnquiryActionBean")
public class TravelProposalEnquiryActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
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

	@ManagedProperty(value = "#{BranchService}")
	private IBranchService branchService;

	public void setBranchService(IBranchService branchService) {
		this.branchService = branchService;
	}

	private TravelProposal travelProposal;
	private TravelExpress travelExpress;
	private User user;
	private List<TPL001> travelProposalList;
	private WorkFlow workFlow;
	private EnquiryCriteria criteria;
	private PaymentDTO paymentDTO;
	private String message;
	private List<Branch> branchList;

	String pdfDirPath = "";
	String fileName = "";
	String dirPath = "";

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
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
		criteria.setBranch(user.getLoginBranch());
		//criteria.setAccessBranchList(user.getAccessBranchList());
	}

	public void findTrabelProposalListByEnquiryCriteria() {
		travelProposalList = travelProposalService.findTravelProposalByEnquiryCriteria(criteria);
	}

	public List<String> getAllBranchIdList() {
		List<String> allbranch = new ArrayList<String>();
		for (Branch b : branchList) {
			allbranch.add(b.getId());
		}
		return allbranch;
	}

	public void findTravelProposalListByAllBranch() {
		criteria.setAccessibleBranchIdList(getAllBranchIdList());
		travelProposalList = travelProposalService.findTravelProposalByEnquiryCriteria(criteria);
	}

	public void showDetailTravelProposal(TPL001 travelProposal) {
		TravelProposal tra=travelProposalService.findTravelProposalById(travelProposal.getId());
		this.travelProposal = tra;
		putParam("travelProposal", tra);
		putParam("workFlowList", getWorkFlowList());
		openTravelProposalInfoTemplate();
	}

	public String editTravelProposal(TravelProposal travelProposal) {
		if (allowToEdit(travelProposal.getId())) {
			outjectTravelProposal(travelProposal);
			return "editTravelProposal";
		} else {
			return null;
		}
	}

	public void outjectTravelProposal(TravelProposal travelProposal) {
		putParam("editTravelProposal", travelProposal);
		putParam("isEnquiryEdit", true);
	}

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateTravelProposalInvoiceLetter(TPL001 tra) {
		
		TravelProposal travelProposal=travelProposalService.findTravelProposalById(tra.getId());
		if (allowToPrint(travelProposal, WorkflowTask.FINISHED, WorkflowTask.PAYMENT, WorkflowTask.ISSUING)) {
			String reportName = "TravelPolicyInvoice";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			fileName = reportName + ".pdf";
			List<Payment> paymentList = paymentService.findByProposal(travelProposal.getId(), PolicyReferenceType.SPECIAL_TRAVEL_PROPOSAL, null);
			paymentDTO = new PaymentDTO(paymentList);
			DocumentBuilder.generateTravelPaymentInvoice(travelProposal, paymentDTO, dirPath, fileName);
			showPdfDialog();
		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
			showInformationDialog();
		}
	}

	public void generateCashReceipt(TPL001 tra) {
		TravelProposal travelProposal=travelProposalService.findTravelProposalById(tra.getId());
		if (allowToPrint(travelProposal, WorkflowTask.FINISHED, WorkflowTask.ISSUING)) {
			List<Payment> paymentList = paymentService.findByProposal(travelProposal.getId(), PolicyReferenceType.SPECIAL_TRAVEL_PROPOSAL, null);
			String reportName = "TravelProposalCashReceipt";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			fileName = reportName + ".pdf";
			DocumentBuilder.generateTravelCashReceipt(travelProposal, paymentList.get(0), dirPath, fileName);
			showPdfDialog();

		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.CONFIRMATION.getLabel().toLowerCase());
			showInformationDialog();
		}
	}

	public void generateTravelPolicyCertificate(TPL001 tra) {
		TravelProposal travelProposal=travelProposalService.findTravelProposalById(tra.getId());
		if (allowToPrint(travelProposal, WorkflowTask.FINISHED)) {
			String reportName = "TravelPolicyCertificate";
			pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
			dirPath = getWebRootPath() + pdfDirPath;
			fileName = reportName + ".pdf";
			DocumentBuilder.generateSpecialTravelCertificate(travelProposal, dirPath, fileName);
			showPdfDialog();

		} else {
			this.message = getMessage(MessageId.WORKFLOW_INFORMATION_MESSAGE, WorkflowTask.PAYMENT.getLabel().toLowerCase());
			showInformationDialog();
		}

	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public EnquiryCriteria getCriteria() {
		return criteria;
	}

	public List<TPL001> getTravelProposalList() {
		return travelProposalList;
	}

	public void setTravelProposalList(List<TPL001> travelProposalList) {
		this.travelProposalList = travelProposalList;
	}

	public void setCriteria(EnquiryCriteria criteria) {
		this.criteria = criteria;
	}

	public void returnBranch(SelectEvent event) {
		Branch branch = (Branch) event.getObject();
		criteria.setBranch(branch);
	}

	public WorkFlow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkFlow workFlow) {
		this.workFlow = workFlow;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(travelProposal.getId());
	}

	public TravelExpress getTravelExpress() {
		return travelExpress;
	}

	public void setTravelExpress(TravelExpress travelExpress) {
		this.travelExpress = travelExpress;
	}

	public void returnExpress(SelectEvent event) {
		Express express = (Express) event.getObject();
		criteria.setExpress(express);
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
			this.message = "This proposal is not in the editable workflow phase. It's been paid. ";
			showInformationDialog();
		} else {
			if (wf.getWorkflowTask().equals(WorkflowTask.CONFIRMATION) || wf.getWorkflowTask().equals(WorkflowTask.PAYMENT)) {
				List<Payment> paymentList = paymentService.findByProposal(refNo, PolicyReferenceType.SPECIAL_TRAVEL_PROPOSAL, false);
				PaymentDTO payment = new PaymentDTO(paymentList);
				flag = false;
				if (payment != null) {
					flag = true;
				}
				this.message = "This proposal is not in the editable workflow phase. It's been paid. ";
				showInformationDialog();
			}
		}
		return flag;
	}

	/**
	 * Allow to print when workflowTask of current WorkFlow contains in
	 * workflowTasks(param)
	 */
	private boolean allowToPrint(TravelProposal travelProposal, WorkflowTask... workflowTasks) {
		WorkFlow workflow = workFlowService.findWorkFlowByRefNo(travelProposal.getId());
		WorkflowTask wfTask = workflow == null ? WorkflowTask.FINISHED : workflow.getWorkflowTask();
		boolean isallowed = false;
		if (Arrays.asList(workflowTasks).contains(wfTask)) {
			this.travelProposal = travelProposal;
			isallowed = true;
		} else {
			showInformationDialog();
		}
		return isallowed;
	}

	private void showPdfDialog() {
		RequestContext.getCurrentInstance().execute("PF('pdfPreviewDialog').show();");
	}

	private void showInformationDialog() {
		RequestContext.getCurrentInstance().execute("PF('informationDialog').show();");
	}
}
