package org.ace.insurance.web.manage.travel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.bank.service.interfaces.IBankService;
import org.ace.insurance.system.common.salesPoints.SalesPoints;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposalInfo;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
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
@ManagedBean(name = "AddNewPersonTravelPaymentActionBean")
public class AddNewPersonTravelPaymentActionBean extends BaseBean {

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

	@ManagedProperty(value = "#{BankService}")
	private IBankService bankService;

	public void setBankService(IBankService bankService) {
		this.bankService = bankService;
	}

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

	private User user;
	private User responsiblePerson;
	private PersonTravelProposal travelProposal;
	private PersonTravelPolicy personTravelPolicy;
	private PaymentDTO paymentDTO;
	private boolean cashPayment = true;
	private String remark;
	private String personTravelProposalId;
	private List<Payment> paymentList;
	private List<PersonTravelPolicy> personTravelPolicyList;
	private PaymentChannel channelValue;
	private boolean isAccountBank;
	private boolean isCheque;
	private boolean isTransfer;
	private boolean isBank;
	private boolean receiptPrint;

	private final String reportName = "TravelReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		travelProposal = (travelProposal == null) ? (PersonTravelProposal) getParam("personTravelProposal") : travelProposal;

	}

	@PreDestroy
	public void destroy() {
		removeParam("personTravelProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		personTravelPolicyList = new ArrayList<PersonTravelPolicy>();

		if (travelProposal != null) {
			personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyByProposalId(travelProposal.getId());
			paymentList = paymentService.findByPolicy(personTravelPolicy.getId());
			travelProposal = personTravelProposalService.findPersonTravelProposalById(travelProposal.getId());
			personTravelPolicyList.add(personTravelPolicy);
			PersonTravelProposalInfo proposalInfo = travelProposal.getPersonTravelInfo();
		}

		paymentList.get(0).setBasicPremium(travelProposal.getPersonTravelInfo().getPremium());

		paymentDTO = new PaymentDTO(paymentList);
	}

	public void paymentTravelProposal() {
		try {
			loadPaymentData();
			WorkflowTask workflowTask = WorkflowTask.ISSUING;
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(travelProposal.getId(), travelProposal.getBranch().getId(), remark, workflowTask, ReferenceType.TRAVEL,
					TransactionType.UNDERWRITING, user, responsiblePerson);
			personTravelProposalService.paymentPersonTravelProposal(travelProposal, workFlowDTO, paymentList, user.getBranch());
			addInfoMessage(null, MessageId.PAYMENT_PROCESS_SUCCESS_PARAM, travelProposal.getProposalNo());
			receiptPrint = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void openTemplate() {
		putParam("personTravelProposal", travelProposal);
		putParam("workFlowList", getWorkFlowList());
		openPersonTravelProposalInfoTemplate();
	}

	public void selectUser() {
		selectUser(WorkflowTask.ISSUING, WorkFlowType.TRAVEL, TransactionType.UNDERWRITING, travelProposal.getBranch().getId());
	}

	public void returnSalesPoints(SelectEvent event) {
		SalesPoints salesPoints = (SalesPoints) event.getObject();
		paymentDTO.setSalesPoints(salesPoints);
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

	/** Generate Report */
	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateCashReceipt() {
		DocumentBuilder.generatePersonTravelCashReceiptLetter(travelProposal, paymentList.get(0), dirPath, fileName);
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isCashPayment() {
		return cashPayment;
	}

	public PersonTravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(PersonTravelProposal travelProposal) {
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

	public void changePaymentChannel(AjaxBehaviorEvent event) {
		switch (paymentDTO.getPaymentChannel()) {
			case CASHED: {
				paymentDTO.setAccountBank(null);
				paymentDTO.setBank(null);
				paymentDTO.setChequeNo(null);
				paymentDTO.setPoNo(null);
				isAccountBank = false;
				isCheque = false;
				isBank = false;
				isTransfer = false;
			}
				break;
			case CHEQUE: {
				paymentDTO.setPoNo(null);
				isAccountBank = true;
				isCheque = true;
				isBank = true;
				isTransfer = false;
			}
				break;
			case TRANSFER: {
				paymentDTO.setChequeNo(null);
				paymentDTO.setAccountBank(null);
				isAccountBank = true;
				isCheque = false;
				isBank = true;
				isTransfer = true;
			}
				break;
		}

	}

	public PaymentChannel getChannelValue() {
		return channelValue;
	}

	public void setChannelValue(PaymentChannel channelValue) {
		this.channelValue = channelValue;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public String getPersonTravelProposalId() {
		return personTravelProposalId;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public List<PersonTravelPolicy> getPersonTravelPolicyList() {
		return personTravelPolicyList;
	}

	public boolean isAccountBank() {
		return isAccountBank;
	}

	public void setAccountBank(boolean isAccountBank) {
		this.isAccountBank = isAccountBank;
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
