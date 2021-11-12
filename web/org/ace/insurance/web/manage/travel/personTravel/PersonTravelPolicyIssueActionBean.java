package org.ace.insurance.web.manage.travel.personTravel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
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

@ViewScoped
@ManagedBean(name = "PersonTravelPolicyIssueActionBean")
public class PersonTravelPolicyIssueActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@ManagedProperty(value = "#{PersonTravelProposalService}")
	IPersonTravelProposalService personTravelProposalService;

	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
	}

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private boolean disableIssueBtn;
	private User user;
	private PersonTravelPolicy personTravelPolicy;
	private PersonTravelProposal personTravelProposal;
	private List<PersonTravelPolicy> personTravelPolicyList;

	private final String reportName = "TravelPolicyIssue";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getWebRootPath() + pdfDirPath;
	private final String fileName = reportName + ".pdf";

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		personTravelProposal = (PersonTravelProposal) getParam("personTravelProposal");
	}

	@PreDestroy
	public void destroy() {
		removeParam("personTravelProposal");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		personTravelPolicyList = new ArrayList<PersonTravelPolicy>();
		personTravelPolicy = new PersonTravelPolicy();
		if (personTravelProposal != null) {
			personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyByProposalId(personTravelProposal.getId());
		}
		personTravelPolicyList.add(personTravelPolicy);
		personTravelProposal = personTravelProposalService.findPersonTravelProposalById(personTravelProposal.getId());
		disableIssueBtn = true;
	}

	public void issuePolicy() {
		try {
			personTravelProposalService.issuePersonTravelProposal(personTravelProposal);
			personTravelProposal = personTravelProposalService.findPersonTravelProposalById(personTravelProposal.getId());
			disableIssueBtn = false;
			addInfoMessage(null, MessageId.ISSUING_PROCESS_SUCCESS_PARAM, personTravelProposal.getProposalNo());
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void openTemplate() {
		putParam("personTravelProposal", personTravelProposal);
		putParam("showPolicy", true);
		putParam("workFlowList", getWorkFlowList());
		openPersonTravelProposalInfoTemplate();
	}

	public void generateReport(PersonTravelPolicy personTravelPolicy) throws Exception {
		DocumentBuilder.generatePersonTravelPolicyIssue(personTravelPolicy, dirPath, fileName);
	}

	public int getYear() {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		return year;
	}

	public int getMonth() {
		Calendar now = Calendar.getInstance();
		int month = now.get(Calendar.MONTH);
		return month + 1;
	}

	public int getDay() {
		Calendar now = Calendar.getInstance();
		int day = now.get(Calendar.DAY_OF_MONTH);
		return day;
	}

	public void handleClose(CloseEvent event) {
		try {
			org.ace.insurance.web.util.FileHandler.forceDelete(dirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public PersonTravelProposal getPersonTravelProposal() {
		return personTravelProposal;
	}

	public List<PersonTravelPolicy> getPersonTravelPolicyList() {
		return personTravelPolicyList;
	}

	public PersonTravelPolicy getPersonTravelPolicy() {
		return personTravelPolicy;
	}

	public boolean isDisableIssueBtn() {
		return disableIssueBtn;
	}

	public String getStream() {
		return pdfDirPath + fileName;

	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(personTravelProposal.getId());
	}

}
