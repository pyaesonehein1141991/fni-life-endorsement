package org.ace.insurance.web.manage.life.paidUp.confirm;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.claimaccept.ClaimAcceptedInfo;
import org.ace.insurance.claimaccept.service.interfaces.IClaimAcceptedInfoService;
import org.ace.insurance.common.PolicyStatus;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.life.paidUp.LifePaidUpProposal;
import org.ace.insurance.life.paidUp.service.interfaces.ILifePaidUpProposalService;
import org.ace.insurance.life.surrender.PaymentTrackDTO;
import org.ace.insurance.payment.Payment;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.user.User;
import org.ace.insurance.user.service.interfaces.IUserService;
import org.ace.insurance.web.common.DocumentBuilder;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;

@ViewScoped
@ManagedBean(name = "LifePaidUpConfirmActionBean")
public class LifePaidUpConfirmActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{UserService}")
	private IUserService userService;

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@ManagedProperty(value = "#{LifePaidUpProposalService}")
	private ILifePaidUpProposalService paidUpProposalService;

	public void setPaidUpProposalService(ILifePaidUpProposalService paidUpProposalService) {
		this.paidUpProposalService = paidUpProposalService;
	}

	@ManagedProperty(value = "#{ClaimAcceptedInfoService}")
	private IClaimAcceptedInfoService claimAcceptedInfoService;

	public void setClaimAcceptedInfoService(IClaimAcceptedInfoService claimAcceptedInfoService) {
		this.claimAcceptedInfoService = claimAcceptedInfoService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	private LifePaidUpProposal paidUpProposal;
	private List<PaymentTrackDTO> paymentList;
	private User user;
	private List<Payment> paymentlist;
	private boolean actualPayment;

	private final String reportName = "lifePaidUpConfirm";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	@PostConstruct
	public void init() {
		initializeInjection();
		ClaimAcceptedInfo claimAcceptedInfo = claimAcceptedInfoService.findClaimAcceptedInfoByReferenceNo(paidUpProposal.getId(), ReferenceType.LIFE_PAIDUP_PROPOSAL);
		if (claimAcceptedInfo != null) {
			paidUpProposal.setServiceCharges(claimAcceptedInfo.getServicesCharges());
		}
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		paidUpProposal = (LifePaidUpProposal) getParam("paidUpProposal");
		paymentList = paymentService.findPaymentTrack(paidUpProposal.getPolicyNo());
	}

	public void confirmPaidUpApproval() {

		try {
			paidUpProposalService.confirmLifePaidUpProposal(paidUpProposal, PolicyStatus.PAIDUP);
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, paidUpProposal.getProposalNo());
			actualPayment = true;
		} catch (SystemException ex) {
			handelSysException(ex);
		}

	}

	public LifePaidUpProposal getPaidUpProposal() {
		return paidUpProposal;
	}

	public List<PaymentTrackDTO> getPaymentList() {
		return paymentList;
	}

	public String editPaidUpApproval() {
		outjectLifePaidUpProposal(paidUpProposal);
		return "editLifePaidUpProposal";
	}

	private void outjectLifePaidUpProposal(LifePaidUpProposal paidUpProposal) {
		putParam("paidUpProposal", paidUpProposal);
	}

	public String denyPaidUpApproval() {
		String result = null;
		try {
			paidUpProposalService.rejectLifePaidUpProposal(paidUpProposal);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, paidUpProposal.getProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;

	}

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		//paymentlist = paymentService.findByPolicyForPaidUp(paidUpProposal.getLifePolicy().getId());
		paymentlist = paymentService.findPaymentListWithPolicyNo(paidUpProposal.getLifePolicy().getPolicyNo());
		Date lastPaymentDate = paymentService.findPaymentDateWithReferenceNo(paidUpProposal.getLifePolicy().getId());
		ClaimAcceptedInfo claimAcceptedInfo = claimAcceptedInfoService.findClaimAcceptedInfoByReferenceNo(paidUpProposal.getId(), ReferenceType.LIFE_PAIDUP_PROPOSAL);
		DocumentBuilder.generateLifePaidUpConfrimForm(paidUpProposal, paymentlist,claimAcceptedInfo, dirPath, fileName,lastPaymentDate);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Payment> getPaymentlist() {
		return paymentlist;
	}

	public void setPaymentlist(List<Payment> paymentlist) {
		this.paymentlist = paymentlist;
	}

	public boolean isActualPayment() {
		return actualPayment;
	}

	public void setActualPayment(boolean actualPayment) {
		this.actualPayment = actualPayment;
	}

	public void setPaidUpProposal(LifePaidUpProposal paidUpProposal) {
		this.paidUpProposal = paidUpProposal;
	}

}
