package org.ace.insurance.web.manage.travel.personTravel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
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
@ManagedBean(name = "PersonTravelConfirmationActionBean")
public class PersonTravelConfirmationActionBean extends BaseBean {

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

	/**
	 * @param personTravelProposalService
	 *            the personTravelProposalService to set
	 */
	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
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
	private PaymentDTO payment;
	private PersonTravelProposal personTravelProposal;
	private String remark;
	private User responsiblePerson;
	private String presentDate;
	private Date effectiveDate;
	private List<Payment> paymentList;
	private boolean showPrintPreview;

	private final String reportName = "TravelInvoice";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	@PostConstruct
	public void init() {
		payment = new PaymentDTO();
		initializeInjection();
	}

	@PreDestroy
	public void destroy() {
		// removeParam("personTravelProposal");
	}

	public void confirmPersonTravel() {
		try {

			WorkFlowDTO workFlowDTO = new WorkFlowDTO(personTravelProposal.getId(), personTravelProposal.getBranch().getId(), remark, WorkflowTask.PAYMENT, ReferenceType.TRAVEL,
					TransactionType.UNDERWRITING, user, responsiblePerson);
			personTravelProposal.setSpecialDiscount(payment.getDiscountPercent());
			paymentList = personTravelProposalService.confirmPersonTravelProposal(personTravelProposal, workFlowDTO, payment);
			showPrintPreview = true;
			addInfoMessage(null, MessageId.CONFIRMATION_PROCESS_SUCCESS_PARAM, personTravelProposal.getProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);

		}
	}

	public void openTemplate() {
		putParam("personTravelProposal", personTravelProposal);
		putParam("workFlowList", getWorkFlowList());
		openPersonTravelProposalInfoTemplate();
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	public void generateReport() {
		DocumentBuilder.generatePersonTravelPaymentInvoiceLetter(personTravelProposal, paymentList.get(0), dirPath, fileName);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public void selectUser() {
		selectUser(WorkflowTask.PAYMENT, WorkFlowType.TRAVEL, TransactionType.UNDERWRITING, personTravelProposal.getBranch().getId());
	}

	public String editPersonTravel() {
		outjectPersonTravelProposal(personTravelProposal);
		return "personTravelProposal";
	}

	public String denyPersonTravel() {
		String result = null;
		try {

			WorkFlowDTO workFlowDTO = new WorkFlowDTO(personTravelProposal.getId(), personTravelProposal.getBranch().getId(), remark, WorkflowTask.REJECT, ReferenceType.TRAVEL,
					TransactionType.UNDERWRITING, user, responsiblePerson);
			personTravelProposalService.rejectPersonTravelProposal(personTravelProposal, workFlowDTO);

			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.DENY_PROCESS_OK);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, personTravelProposal.getProposalNo());
			result = "dashboard";

		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	/**
	 * @return the personTravelProposal
	 */
	public PersonTravelProposal getPersonTravelProposal() {
		return personTravelProposal;
	}

	/**
	 * @param personTravelProposal
	 *            the personTravelProposal to set
	 */
	public void setPersonTravelProposal(PersonTravelProposal personTravelProposal) {
		this.personTravelProposal = personTravelProposal;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the responsiblePerson
	 */
	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	/**
	 * @param responsiblePerson
	 *            the responsiblePerson to set
	 */
	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public PaymentDTO getPayment() {
		return payment;
	}

	public void setPayment(PaymentDTO payment) {
		this.payment = payment;
	}

	public String getPresentDate() {
		return presentDate;
	}

	public void setPresentDate(String presentDate) {
		this.presentDate = presentDate;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public boolean getIsShowPrintPreview() {
		return showPrintPreview;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(personTravelProposal.getId());
	}

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		personTravelProposal = (personTravelProposal == null) ? (PersonTravelProposal) getParam("personTravelProposal") : personTravelProposal;
		payment.setBasicPremium(personTravelProposal.getPersonTravelInfo().getPremium());
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		presentDate = format.format(new Date());
		calcStampFee();
	}

	private void calcStampFee() {
		double stamp = premiumCalculatorService.calculateStampFee(personTravelProposal.getProduct(),
				new PremiumCalData(null, null, null, personTravelProposal.getPersonTravelInfo().getTotalUnit()));
		payment.setStampFees(stamp);
	}

	private void outjectPersonTravelProposal(PersonTravelProposal personTravelProposal) {
		putParam("personTravelProposal", personTravelProposal);
	}
}
