package org.ace.insurance.web.manage.life.claim.approve;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.life.claim.DisabilityLifeClaim;
import org.ace.insurance.life.claim.LifeClaimProposal;
import org.ace.insurance.life.claim.LifeDeathClaim;
import org.ace.insurance.life.claim.LifeHospitalizedClaim;
import org.ace.insurance.life.claim.LifePolicyClaim;
import org.ace.insurance.life.claim.service.interfaces.ILifeClaimProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.common.document.DocumentBuilder;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.insurance.workflow.service.interfaces.IWorkFlowService;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "ApproveLifeClaimActionBean")
public class ApproveLifeClaimActionBean extends BaseBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{LifeClaimProposalService}")
	private ILifeClaimProposalService lifeClaimProposalService;

	public void setLifeClaimProposalService(ILifeClaimProposalService lifeClaimProposalService) {
		this.lifeClaimProposalService = lifeClaimProposalService;
	}

	@ManagedProperty(value = "#{WorkFlowService}")
	private IWorkFlowService workFlowService;

	public void setWorkFlowService(IWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}

	private User user;
	private LifeClaimProposal lifeClaimProposal;
	private boolean death;
	private boolean disbility;
	private boolean approved;
	private boolean hospital;
	private LifeDeathClaim lifeDeathClaim;
	private LifeHospitalizedClaim lifeHospitalizedClaim;
	private DisabilityLifeClaim disabilityLifeClaim;
	private List<WorkFlowHistory> workflowList;
	private User responsiblePerson;
	private String remark;
	private LifePolicyClaim lifePolicyClaim;
	private boolean isDisPrint;
	private final String reportName = "LifePolicyInform";
	private final String pdfDirPath = "/pdf-report/" + reportName + "/" + System.currentTimeMillis() + "/";
	private final String dirPath = getSystemPath() + pdfDirPath;
	private String fileName = "LifeClaimApproval.pdf";

	private void initializeInjection() {
		user = (user == null) ? (User) getParam(Constants.LOGIN_USER) : user;
		lifeClaimProposal = (lifeClaimProposal == null) ? (LifeClaimProposal) getParam("lifeClaimProposal") : lifeClaimProposal;
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		loadClaimList();
		if(lifeClaimProposal != null) {
			this.approved = lifeClaimProposal.getLifePolicyClaim().isApproved() ? true : false;
			this.lifePolicyClaim = (lifeClaimProposal.getLifePolicyClaim() == null) ? this.lifePolicyClaim 
					: lifeClaimProposal.getLifePolicyClaim();
		}
	}

	public String getReportStream() {
		return pdfDirPath + fileName;
	}

	private void loadClaimList() {
		lifeDeathClaim = new LifeDeathClaim();
		lifeHospitalizedClaim = new LifeHospitalizedClaim();
		disabilityLifeClaim = new DisabilityLifeClaim();
		lifePolicyClaim = new LifePolicyClaim();
		
		if (lifeClaimProposal.getLifePolicyClaim() instanceof LifeDeathClaim) {
			lifeDeathClaim = (LifeDeathClaim) lifeClaimProposal.getLifePolicyClaim();
			death = true;
		} else if (lifeClaimProposal.getLifePolicyClaim() instanceof LifeHospitalizedClaim) {
			hospital = true;
			lifeHospitalizedClaim = (LifeHospitalizedClaim) lifeClaimProposal.getLifePolicyClaim();
		} else {
			disbility = true;
			disabilityLifeClaim = (DisabilityLifeClaim) lifeClaimProposal.getLifePolicyClaim();
		}
	}

	public String approveLifeClaim() {
		lifeClaimProposal.getLifePolicyClaim().setApproved(true);
		lifeClaimProposal.getLifePolicyClaim().setApprovedReason(lifePolicyClaim.getApprovedReason());
		String result = null;
		try {
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeClaimProposal.getId(), user.getLoginBranch().getId(), remark, WorkflowTask.INFORM, ReferenceType.LIFE_CLAIM,
					TransactionType.CLAIM, user, responsiblePerson);
			if (death) {
				lifeClaimProposal.setLifePolicyClaim(lifeDeathClaim);
			} else if (hospital) {
				lifeClaimProposal.setLifePolicyClaim(lifeHospitalizedClaim);
			} else {
				lifeClaimProposal.setLifePolicyClaim(disabilityLifeClaim);
			}
			lifeClaimProposalService.approveLifeClaim(lifeClaimProposal, workFlowDTO);
			isDisPrint = true;
			
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.LIFE_ClAIM_APPROVAL_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeClaimProposal.getClaimProposalNo());
			addInfoMessage( MessageId.LIFE_ClAIM_APPROVAL_SUCCESS);
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public String rejectLifeClaim() {
		String result = null;
		try {
			lifeClaimProposal.getLifePolicyClaim().setApproved(false);
			WorkFlowDTO workFlowDTO = new WorkFlowDTO(lifeClaimProposal.getId(), user.getLoginBranch().getId(), remark, WorkflowTask.INFORM, ReferenceType.LIFE_CLAIM,
					TransactionType.CLAIM, user, responsiblePerson);
			workFlowService.updateWorkFlow(workFlowDTO, WorkflowTask.INFORM);
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.REJECT_PROPOSAL_PROCESS_SUCCESS);
			extContext.getSessionMap().put(Constants.PROPOSAL_NO, lifeClaimProposal.getClaimProposalNo());
			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	@PreDestroy
	public void destroy() {
		removeParam("lifeClaimProposal");
	}

	public void openTemplateDialog() {
		putParam("lifeClaimProposal", lifeClaimProposal);
		putParam("workFlowList", getWorkflowList());
		openLifeClaimInfoTemplate();
	}

	public void addReasonType() {
		System.out.println(lifeClaimProposal.getLifePolicyClaim().getApprovedReason());
		lifeClaimProposalService.updateLifeClaimProposal(lifeClaimProposal);
	}

	public void removeApproveReason() {
		lifeClaimProposal.getLifePolicyClaim().setApprovedReason(null);
	}

	public void generatePrintLifePolicyInform() {
		DocumentBuilder.generateDisabilityLifeClaimInformLetter(lifeClaimProposal, dirPath, fileName);
	}

	public void loadWorkflow() {
		workflowList = workFlowService.findWorkFlowHistoryByRefNo(lifeClaimProposal.getId());
	}

	public ILifeClaimProposalService getLifeClaimProposalService() {
		return lifeClaimProposalService;
	}

	public User getUser() {
		return user;
	}

	public LifeClaimProposal getLifeClaimProposal() {
		return lifeClaimProposal;
	}

	public boolean isDeath() {
		return death;
	}

	public boolean isDisbility() {
		return disbility;
	}

	public boolean isHospital() {
		return hospital;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setLifeClaimProposal(LifeClaimProposal lifeClaimProposal) {
		this.lifeClaimProposal = lifeClaimProposal;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

	public void setResponsiblePerson(User responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	public void selectUser() {
		selectUser(WorkflowTask.SURVEY, WorkFlowType.LIFE);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public LifeDeathClaim getLifeDeathClaim() {
		return lifeDeathClaim;
	}

	public LifeHospitalizedClaim getLifeHospitalizedClaim() {
		return lifeHospitalizedClaim;
	}

	public DisabilityLifeClaim getDisabilityLifeClaim() {
		return disabilityLifeClaim;
	}

	public List<WorkFlowHistory> getWorkflowList() {
		return workFlowService.findWorkFlowHistoryByRefNo(lifeClaimProposal.getId());
	}

	public LifePolicyClaim getLifePolicyClaim() {
		return lifePolicyClaim;
	}

	public void setLifePolicyClaim(LifePolicyClaim lifePolicyClaim) {
		this.lifePolicyClaim = lifePolicyClaim;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isDisPrint() {
		return isDisPrint;
	}

	public void setDisPrint(boolean isDisPrint) {
		this.isDisPrint = isDisPrint;
	}

}
