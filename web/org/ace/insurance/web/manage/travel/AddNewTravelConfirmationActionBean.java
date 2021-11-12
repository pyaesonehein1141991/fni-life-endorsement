package org.ace.insurance.web.manage.travel;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.product.PremiumCalData;
import org.ace.insurance.product.service.interfaces.IPremiumCalculatorService;
import org.ace.insurance.system.common.branch.Branch;
import org.ace.insurance.system.common.paymenttype.PaymentType;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.document.DocumentBuilder;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "AddNewTravelConfirmationActionBean")
public class AddNewTravelConfirmationActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{PremiumCalculatorService}")
	private IPremiumCalculatorService premiumCalculatorService;

	public void setPremiumCalculatorService(IPremiumCalculatorService premiumCalculatorService) {
		this.premiumCalculatorService = premiumCalculatorService;
	}

	private User user;
	private TravelProposal travelProposal;
	private String remark;
	private String fileName;
	private User responsiblePerson;
	private PaymentDTO payment;
	private List<Payment> paymentList;
	private boolean showPrintPreview;

	private final String reportName = "SpecialTravelCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (TravelProposal) getParam("travelProposal");
		payment.setBasicPremium(travelProposal.getPremium());
		fileName = "TravelPolicyInvoice.pdf";
		calcStampFee();
	}

	@PostConstruct
	public void init() {
		payment = new PaymentDTO();
		initializeInjection();
	}

	@PreDestroy
	public void destroy() {
		removeParam("travelProposal");
	}

	public void confirmTravelProposal() {
		try {
			if (responsiblePerson == null) {
				addErrorMessage("travelProposalConfirmForm:responsiblePerson", UIInput.REQUIRED_MESSAGE_ID);
			} else {
				travelProposal.setSpecialDiscount(payment.getDiscountPercent());
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), travelProposal.getBranch().getId(), remark, WorkflowTask.PAYMENT, ReferenceType.SPECIAL_TRAVEL,
						TransactionType.UNDERWRITING, user, responsiblePerson);
				paymentList = travelProposalService.confirmTravelProposal(travelProposal, workFlowDTO, payment);
				payment = new PaymentDTO(paymentList);
				showPrintPreview = true;
				addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, travelProposal.getProposalNo());
			}
		} catch (SystemException ex) {
			handelSysException(ex);
		}
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
		selectUser(WorkflowTask.PAYMENT, WorkFlowType.SPECIAL_TRAVEL, TransactionType.UNDERWRITING, travelProposal.getBranch().getId(),null);
	}

	public String editTravelProposal() {
		outjectTravelProposal(travelProposal);
		putParam("isConfirmEdit", true);
		return "editTravelProposal";
	}

	public String denyTravelProposal() {
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), travelProposal.getBranch().getId(), remark, WorkflowTask.REJECT, ReferenceType.SPECIAL_TRAVEL,
					TransactionType.UNDERWRITING, user, responsiblePerson);
			travelProposalService.rejectTravelProposal(travelProposal, workFlowDTO);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void openTemplate() {
		putParam("travelProposal", travelProposal);
		putParam("workFlowList", getWorkFlowList());
		openTravelProposalInfoTemplate();
	}

	/** Generate Report */
	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		DocumentBuilder.generateTravelPaymentInvoice(travelProposal, payment, dirPath, fileName);
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean getIsShowPrintPreview() {
		return showPrintPreview;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public PaymentDTO getPayment() {
		return payment;
	}

	public void setPayment(PaymentDTO payment) {
		this.payment = payment;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<Payment> paymentList) {
		this.paymentList = paymentList;
	}

	public String getFileName() {
		return fileName;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(travelProposal.getId());
	}

	private void calcStampFee() {
		double stamp = premiumCalculatorService.calculateStampFee(travelProposal.getExpressList().get(0).getProduct(),
				new PremiumCalData(null, null, null, Double.valueOf(travelProposal.getTotalUnit())));
		payment.setStampFees(stamp);
	}

	private void outjectTravelProposal(TravelProposal travelProposal) {
		putParam("editTravelProposal", travelProposal);
	}
}
