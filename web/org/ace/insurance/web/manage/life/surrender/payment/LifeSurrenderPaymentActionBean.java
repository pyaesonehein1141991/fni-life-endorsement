package org.ace.insurance.web.manage.life.surrender.payment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.event.AjaxBehaviorEvent;

import org.ace.insurance.claimproduct.service.interfaces.IClaimProductService;
import org.ace.insurance.common.PaymentDTO;
import org.ace.insurance.common.PolicyReferenceType;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.RequestStatus;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.surrender.LifeSurrenderKeyFactor;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.life.surrender.service.interfaces.ILifeSurrenderProposalService;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.system.common.PaymentChannel;
import org.ace.insurance.system.common.bank.Bank;
import org.ace.insurance.system.common.currency.Currency;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "LifeSurrenderPaymentActionBean")
public class LifeSurrenderPaymentActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeSurrenderProposalService}")
	private ILifeSurrenderProposalService lifeSurrenderProposalService;

	public void setLifeSurrenderProposalService(ILifeSurrenderProposalService lifeSurrenderProposalService) {
		this.lifeSurrenderProposalService = lifeSurrenderProposalService;
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

	@ManagedProperty(value = "#{ClaimProductService}")
	private IClaimProductService claimProductService;

	public void setClaimProductService(IClaimProductService claimProductService) {
		this.claimProductService = claimProductService;
	}
	
	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}


	private User user;
	private LifeSurrenderProposal lifeSurrenderProposal;
	private PaymentDTO paymentDTO;
	private String remark;
	private User responsiblePerson;
	private boolean actualPayment;
	private boolean cashPayment = true;
	List<Payment> paymentList;
	private Currency currency;
	private String payee;
	private List<WorkFlowHistory> workFlowList;
	private List<PaymentTrackDTO> paymentTrackList;

	private PaymentChannel channelValue;
	// for paymenttransfer
	private boolean isCheque;
	// add for transfer
	private boolean isAccountBank;
	private boolean isBank;
	private boolean isTransfer;
	private LifePolicy lifePolicy;

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeSurrenderProposal = (LifeSurrenderProposal) getParam("surrenderProposal");
		workFlowList = workFlowService.findWorkFlowHistoryByRefNo(lifeSurrenderProposal.getId());
		paymentTrackList = paymentService.findPaymentTrack(lifeSurrenderProposal.getPolicyNo());
		lifePolicy=lifePolicyService.findLifePolicyByPolicyNo(lifeSurrenderProposal.getPolicyNo());
		paymentService.findTotalPermiumAmountFromPaymentListWithPolicyId(lifePolicy.getId());
		getTotalPremiumAmount();
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeSurrenderProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		paymentList = paymentService.findByClaimProposal(lifeSurrenderProposal.getId(), PolicyReferenceType.LIFE_SURRENDER_CLAIM, false);
		Payment payment = paymentList.get(0);
		//cashPayment = payment.getPaymentChannel().equals(PaymentChannel.CASHED) ? true : false;
		paymentDTO = new PaymentDTO(paymentList);

	}

	private final String reportName = "lifeSurrenderClaimCashReceipt";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		DocumentBuilder.generateLifeSurrenderpaymentletter(lifeSurrenderProposal, paymentTrackList, paymentDTO, dirPath, fileName);
	}

	public void paymentLifeSurrenderProposal() {
		String result = null;
		if(paymentDTO.getProvisionAmount() == 0) {
			addInfoMessage(MessageId.INVALID_LIFE_SURRENDER_PROVISIONAMOUNT);
		} else if(paymentDTO.getPreIncomeAmount() == 0) {
			addInfoMessage(MessageId.INVALID_LIFE_SURRENDER_PREINCOMEAMOUNT);
		} else if(lifeSurrenderProposal.getSurrenderAmount() != (paymentDTO.getProvisionAmount() + paymentDTO.getPreIncomeAmount())) {
			addInfoMessage(null, MessageId.INVALID_LIFE_SURRENDER_AMOUNT); 
		} else {
			
			try {			
				setPaymentChannelList();
				WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeSurrenderProposal.getId(), getLoginBranchId(), remark, WorkflowTask.ISSUING, ReferenceType.LIFESURRENDER,
						TransactionType.UNDERWRITING, user, responsiblePerson);

				lifeSurrenderProposalService.updateLifeSurrenderProposal(lifeSurrenderProposal);
				lifeSurrenderProposalService.payLifeSurrender(paymentList, workFlowDTO, user.getBranch(), RequestStatus.FINISHED.name(),lifePolicy,lifeSurrenderProposal);
				actualPayment = true;
				paymentDTO = new PaymentDTO(paymentList);
				ExternalContext extContext = getFacesContext().getExternalContext();
				extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.PAYMENT_PROCESS_SUCCESS);
				extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeSurrenderProposal.getProposalNo());
			} catch (SystemException ex) {
				handelSysException(ex);
			}

		}
	}

	public boolean isActualPayment() {
		return actualPayment;
	}

	public LifeSurrenderProposal getLifeSurrenderProposal() {
		return lifeSurrenderProposal;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public boolean isCashPayment() {
		return cashPayment;
	}

	public void selectUser() {
		selectUser(WorkflowTask.ISSUING, WorkFlowType.LIFESURRENDER, TransactionType.UNDERWRITING, getLoginBranchId(), null);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public PaymentDTO getPaymentDTO() {
		return paymentDTO;
	}

	public double getNetSurrenderAmount() {
		return lifeSurrenderProposal.getSurrenderAmount() - paymentDTO.getServicesCharges() - lifeSurrenderProposal.getLifePremium();
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowList;
	}

	public List<PaymentTrackDTO> getPaymentTrackList() {
		return paymentTrackList;
	}

	private String getLoginBranchId() {
		return user.getLoginBranch().getId();
	}

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	//Payment Channel
	public PaymentChannel[] getChannelValues() {
		return new PaymentChannel[] { PaymentChannel.CASHED, PaymentChannel.CHEQUE, PaymentChannel.TRANSFER };
	}
	
	public void changePaymentChannel(AjaxBehaviorEvent event) {
		paymentDTO.setAccountBank(null);
		paymentDTO.setBank(null);
		paymentDTO.setChequeNo(null);
		paymentDTO.setPoNo(null);
	}

	public void returnAccountBank(SelectEvent event) {
		Bank accountBank = (Bank) event.getObject();
		paymentDTO.setAccountBank(accountBank);
	}

	public void returnBank(SelectEvent event) {
		Bank bank = (Bank) event.getObject();
		paymentDTO.setBank(bank);
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

	public boolean isAccountBank() {
		return isAccountBank;
	}

	public void setAccountBank(boolean isAccountBank) {
		this.isAccountBank = isAccountBank;
	}

	public boolean isBank() {
		return isBank;
	}

	public void setBank(boolean isBank) {
		this.isBank = isBank;
	}

	public boolean isTransfer() {
		return isTransfer;
	}

	public void setTransfer(boolean isTransfer) {
		this.isTransfer = isTransfer;
	}

	private void setPaymentChannelList() {
		paymentDTO.setPaymentChannel(channelValue);
		paymentList.get(0).setProvisionAmount(paymentDTO.getProvisionAmount());
		paymentList.get(0).setPreIncomeAmount(paymentDTO.getPreIncomeAmount());
		paymentList.get(0).setPaymentChannel(paymentDTO.getPaymentChannel());
		if (!paymentDTO.getPaymentChannel().equals(PaymentChannel.CASHED)) {
			paymentList.get(0).setAccountBank(paymentDTO.getAccountBank());
			paymentList.get(0).setBank(paymentDTO.getBank());
			if (paymentDTO.getPaymentChannel().equals(PaymentChannel.TRANSFER)) {
				paymentList.get(0).setPoNo(paymentDTO.getPoNo());
			} else {
				paymentList.get(0).setChequeNo(paymentDTO.getChequeNo());
			}
		} 
	}
	
	public double getSurrenderAmounts() {
		Map<KeyFactor, String> keyfatorValueMap = new HashMap<KeyFactor, String>();
		for (LifeSurrenderKeyFactor lsKeyFactor : lifeSurrenderProposal.getLifeSurrenderKeyFactors()) {
			keyfatorValueMap.put(lsKeyFactor.getKeyFactor(), lsKeyFactor.getValue());
		}
		Double result = claimProductService.findClaimProductRateByRp(keyfatorValueMap, lifeSurrenderProposal.getClaimProduct(), lifeSurrenderProposal.getSumInsured());
		double si = lifeSurrenderProposal.getLifePolicy().getSumInsured();
		return (result * si) / 1000;
	}

	public double getTotalPremiumAmount() {
		/*
		 * if (paymentDTO.getProvisionAmount() > 0 && paymentDTO.getPreIncomeAmount() >
		 * 0) { return paymentDTO.getProvisionAmount() +
		 * paymentDTO.getPreIncomeAmount(); }
		 */
		
		return paymentService.findTotalPermiumAmountFromPaymentListWithPolicyId(lifePolicy.getId());
	}
}