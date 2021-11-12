package org.ace.insurance.web.manage.travel;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

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

@ViewScoped
@ManagedBean(name = "IssueTravelProposalActionBean")
public class IssueTravelProposalActionBean extends BaseBean implements Serializable {
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

	private User user;
	private TravelProposal travelProposal;
	private boolean isEnablePrintPreview;

	private final String reportName = "SpecialTravelPolicyCertificate";
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
		isEnablePrintPreview = true;
	}

	public void issuePolicy() {
		try {
			travelProposalService.issueTravelProposal(travelProposal);
			addInfoMessage(null, MessageId.ISSUING_PROCESS_SUCCESS_PARAM, travelProposal.getProposalNo());
			isEnablePrintPreview = false;
		} catch (SystemException ex) {
			handelSysException(ex);
		}
	}

	public void openTemplate() {
		putParam("travelProposal", travelProposal);
		putParam("workFlowList", getWorkFlowList());
		openTravelProposalInfoTemplate();
	}

	public void generateCertificate(TravelProposal travelProposal) {
		DocumentBuilder.generateSpecialTravelCertificate(travelProposal, dirPath, fileName);
	}

	public String getStream() {
		return pdfDirPath + fileName;
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

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(travelProposal.getId());
	}

	public boolean isEnablePrintPreview() {
		return isEnablePrintPreview;
	}
}
