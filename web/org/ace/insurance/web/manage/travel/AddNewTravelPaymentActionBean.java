package org.ace.insurance.web.manage.travel;

import java.io.IOException;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.salesPoints.SalesPoints;
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
@ManagedBean(name = "AddNewTravelPaymentActionBean")
public class AddNewTravelPaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	private User user;
	private User responsiblePerson;
	private TravelProposal travelProposal;
	private PaymentDTO paymentDTO;
	private boolean cashPayment = true;
	private String remark;
	private List<Payment> paymentList;
	private PaymentChannel channelValue;
	private boolean isCheque;
	private boolean isTransfer;
	private boolean isBank;
	private boolean isAccountBank;
	private boolean receiptPrint;

	private final String reportName = "SpecialTravelReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (travelProposal == null) ? (TravelProposal) getParam("travelProposal") : travelProposal;
	}

	@PreDestroy
	public void destroy() {
		removeParam("travelproposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		paymentList = paymentService.findByProposal(travelProposal.getId(), PolicyReferenceType.SPECIAL_TRAVEL_PROPOSAL, false);
		paymentDTO = new PaymentDTO(paymentList);
	}

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		paymentDTO.setPaymentChannel(channelValue);
		if (channelValue.equals(PaymentChannel.CASHED)) {
			paymentDTO.setAccountBank(null);
			paymentDTO.setBank(null);
			paymentDTO.setChequeNo(null);
			paymentDTO.setPoNo(null);
		} else if (channelValue.equals(PaymentChannel.TRANSFER)) {
			paymentDTO.setChequeNo(null);
			paymentDTO.setAccountBank(null);
		} else if (channelValue.equals(PaymentChannel.CHEQUE)) {
			paymentDTO.setPoNo(null);
		}
	}

	public void paymentTravelProposal() {
		try {
			loadPaymentData();
			WorkflowTask workflowTask = WorkflowTask.ISSUING;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), travelProposal.getBranch().getId(), remark, workflowTask, ReferenceType.SPECIAL_TRAVEL,
					TransactionType.UNDERWRITING, user, responsiblePerson);
			travelProposalService.paymentTravelProposal(travelProposal, workFlowDTO, paymentList, user.getBranch());
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, travelProposal.getProposalNo());
			receiptPrint = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void returnAccountBank(SelectEvent event) {
		Bank accountBank = (Bank) event.getObject();
		paymentDTO.setAccountBank(accountBank);
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		paymentDTO.setBank(bank);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void returnSalesPoints(SelectEvent event) {
		SalesPoints salesPoints = (SalesPoints) event.getObject();
		paymentDTO.setSalesPoints(salesPoints);
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

	public void generateCashReceipt() {
		DocumentBuilder.generateTravelCashReceipt(travelProposal, paymentList.get(0), dirPath, fileName);
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void selectUser() {
		selectUser(WorkflowTask.ISSUING, WorkFlowType.SPECIAL_TRAVEL, TransactionType.UNDERWRITING, travelProposal.getBranch().getId(),null);
	}

	public boolean isCashPayment() {
		return cashPayment;
	}

	public EnumSet<PaymentChannel> getPaymentChannels() {
		return EnumSet.of(PaymentChannel.CASHED, PaymentChannel.CHEQUE);
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public PaymentDTO getPayment() {
		return paymentDTO;
	}

	public void setPayment(PaymentDTO paymentDTO) {
		this.paymentDTO = paymentDTO;
	}

	public PaymentChannel[] getChannelValues() {
		return PaymentChannel.values();
	}

	public PaymentChannel getChannelValue() {
		return channelValue;
	}

	public void setChannelValue(PaymentChannel channelValue) {
		if (channelValue.equals(PaymentChannel.CHEQUE)) {
			isAccountBank = true;
			isCheque = true;
			isBank = true;
			isTransfer = false;
		} else if (channelValue.equals(PaymentChannel.TRANSFER)) {
			isAccountBank = true;
			isCheque = false;
			isBank = true;
			isTransfer = true;
		} else {
			isAccountBank = false;
			isCheque = false;
			isBank = false;
			isTransfer = false;
		}
		this.channelValue = channelValue;
	}

	public boolean isCheque() {
		return isCheque;
	}

	public void setCheque(boolean isCheque) {
		this.isCheque = isCheque;
	}

	public boolean isTransfer() {
		return isTransfer;
	}

	public void setTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	public boolean isBank() {
		return isBank;
	}

	public void setBank(boolean isBank) {
		this.isBank = isBank;
	}

	public boolean isAccountBank() {
		return isAccountBank;
	}

	public void setAccountBank(boolean isAccountBank) {
		this.isAccountBank = isAccountBank;
	}
	
	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public boolean isReceiptPrint() {
		return receiptPrint;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(travelProposal.getId());
	}

	private void loadPaymentData() {
		for (Payment payment : paymentList) {
			payment.setPaymentChannel(paymentDTO.getPaymentChannel());
			payment.setAccountBank(paymentDTO.getAccountBank());
			payment.setBank(paymentDTO.getBank());
			payment.setChequeNo(paymentDTO.getChequeNo());
			payment.setPoNo(paymentDTO.getPoNo());
			payment.setSalesPoints(paymentDTO.getSalesPoints());
		}
	}
}
