package org.ace.insurance.web.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.GenericDataModel;
import org.ace.insurance.common.ReferenceType;
import org.ace.insurance.common.TransactionType;
import org.ace.insurance.common.WorkFlowDTO;
import org.ace.insurance.common.WorkFlowType;
import org.ace.insurance.common.WorkflowTask;
import org.ace.insurance.common.interfaces.IDataModel;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.life.proposal.service.interfaces.ILifeProposalService;
import org.ace.insurance.life.surrender.LifeSurrenderProposal;
import org.ace.insurance.life.surrender.service.interfaces.ILifeSurrenderProposalService;
import org.ace.insurance.medical.policy.MedicalPolicy;
import org.ace.insurance.medical.policy.service.interfaces.IMedicalPolicyService;
import org.ace.insurance.medical.proposal.service.interfaces.IMedicalProposalService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.proxy.LIF001;
import org.ace.insurance.proxy.LSP001;
import org.ace.insurance.proxy.MED001;
import org.ace.insurance.proxy.TRA001;
import org.ace.insurance.proxy.WorkflowCriteria;
import org.ace.insurance.proxy.service.interfaces.IProxyService;
import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.service.interfaces.IPersonTravelProposalService;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.travel.expressTravel.service.interfaces.ITravelProposalService;
import org.ace.insurance.user.User;
import org.ace.insurance.web.manage.life.billcollection.BC0001;
import org.ace.java.component.SystemException;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.primefaces.event.SelectEvent;

@ViewScoped
@ManagedBean(name = "WorkFlowChangerActionBean")
public class WorkflowChangerActionBean<T extends IDataModel> extends BaseBean {

	@ManagedProperty(value = "#{ProxyService}")
	private IProxyService proxyService;

	public void setProxyService(IProxyService proxyService) {
		this.proxyService = proxyService;
	}

	@ManagedProperty(value = "#{LifeProposalService}")
	private ILifeProposalService lifeProposalService;

	public void setLifeProposalService(ILifeProposalService lifeProposalService) {
		this.lifeProposalService = lifeProposalService;
	}

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}

	@ManagedProperty(value = "#{MedicalPolicyService}")
	private IMedicalPolicyService medicalPolicyService;

	public void setMedicalPolicyService(IMedicalPolicyService medicalPolicyService) {
		this.medicalPolicyService = medicalPolicyService;
	}

	@ManagedProperty(value = "#{MedicalProposalService}")
	private IMedicalProposalService medicalProposalService;

	public void setMedicalProposalService(IMedicalProposalService medicalProposalService) {
		this.medicalProposalService = medicalProposalService;
	}

	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}
	@ManagedProperty(value = "#{TravelProposalService}")
	private ITravelProposalService travelProposalService;

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}
	
	@ManagedProperty(value = "#{PersonTravelProposalService}")
	private IPersonTravelProposalService personTravelProposalService;

	public void setPersonTravelProposalService(IPersonTravelProposalService personTravelProposalService) {
		this.personTravelProposalService = personTravelProposalService;
	}

	public void setTravelProposalService(ITravelProposalService travelProposalService) {
		this.travelProposalService = travelProposalService;
	}

	@ManagedProperty(value = "#{LifeSurrenderProposalService}")
	private ILifeSurrenderProposalService surrderProposalService;

	public void setSurrderProposalService(ILifeSurrenderProposalService surrderProposalService) {
		this.surrderProposalService = surrderProposalService;
	}

	private ReferenceType referenceType;
	private User user;
	private GenericDataModel<IDataModel> proposalDataModel;
	private String loginBranchId;

	private T[] selectedLifeProposals;
	private User responsiblePerson;

	@PostConstruct
	public void init() {
		user = (User) getParam(Constants.LOGIN_USER);
		loginBranchId = user.getLoginBranch().getId();
		referenceType = ReferenceType.ENDOWMENT_LIFE;
		search();
	}

	public User getUser() {
		return user;
	}

	public List<ReferenceType> getReferenceTypes() {
		List<ReferenceType> refList = new ArrayList<ReferenceType>();
		refList.addAll(getLifeReferenceTypes());
		refList.addAll(getHealthReferenceTypes());
		refList.addAll(getTravelReferenceTypes());
		return refList;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public GenericDataModel<IDataModel> getProposalDataModel() {
		return proposalDataModel;
	}

	public T[] getSelectedLifeProposals() {
		return selectedLifeProposals;
	}

	public void setSelectedLifeProposals(T[] selectedLifeProposals) {
		this.selectedLifeProposals = selectedLifeProposals;
	}

	public void search() {
		if (getLifeReferenceTypes().contains(referenceType)) {
			if(referenceType.equals(ReferenceType.TRAVEL)) {
				List<TRA001> personTravelPaymentList = proxyService
						.find_TRA001_WFC(new WorkflowCriteria(referenceType, WorkflowTask.PAYMENT, user.getLoginBranch().getId()));
				proposalDataModel = new GenericDataModel(personTravelPaymentList);
			} else if(referenceType.equals(ReferenceType.LIFESURRENDER)) {
				List<LSP001> lifeSurrenderList = proxyService.find_LSP001_WFC(new WorkflowCriteria(referenceType, WorkflowTask.PAYMENT, loginBranchId));
				proposalDataModel = new GenericDataModel(lifeSurrenderList);
			} else {
				List<LIF001> lifePaymentList = proxyService.find_LIF001(new WorkflowCriteria(referenceType, WorkflowTask.PAYMENT, loginBranchId));
				proposalDataModel = new GenericDataModel(lifePaymentList);
			}
		} else if (getHealthReferenceTypes().contains(referenceType)) {
			List<MED001> medicalPaymentList = proxyService.find_MED001(new WorkflowCriteria(referenceType, WorkflowTask.PAYMENT, loginBranchId));
			proposalDataModel = new GenericDataModel(medicalPaymentList);
		}  else if (getTravelReferenceTypes().contains(referenceType)) {
			if (referenceType.equals(ReferenceType.SPECIAL_TRAVEL)) {
				List<TRA001> travelPaymentList = proxyService.findTravel_WFC(new WorkflowCriteria(referenceType, WorkflowTask.PAYMENT, loginBranchId));
				proposalDataModel = new GenericDataModel(travelPaymentList);
			} 
		}
		
	}
	
	private void updateWorkFlowForLife(WorkFlowDTO workFlowDTO, String proposalId) {
		LifePolicy lifePolicy = lifePolicyService.findLifePolicyByLifeProposalId(proposalId);
		lifeProposalService.deletePayment(lifePolicy, workFlowDTO);
	}

	public String updateWorkflow() {
		String result = null;
		try {
			if (responsiblePerson != null) {
				if (getLifeReferenceTypes().contains(referenceType)) {
					for (T proposal : selectedLifeProposals) {
						WorkFlowDTO workFlowDTO = new WorkFlowDTO(proposal.getId(), loginBranchId, "", WorkflowTask.CONFIRMATION, referenceType, TransactionType.UNDERWRITING, user,
								responsiblePerson);
						
						if (referenceType.equals(ReferenceType.TRAVEL)) {
							// Person Travel, policy based
							PersonTravelPolicy personTravelPolicy = personTravelPolicyService.findPersonTravelPolicyByProposalId(proposal.getId());
							personTravelProposalService.deletePayment(personTravelPolicy, workFlowDTO);
							 //if (transactionType.equals(TransactionType.UNDERWRITING)) {
							 //}					
						} else if(referenceType.equals(ReferenceType.LIFESURRENDER)) {
							LifeSurrenderProposal surrenderProposal = surrderProposalService.findLifeSurrenderProposalById(proposal.getId());
							surrderProposalService.deletePayment(surrenderProposal, workFlowDTO);
						} else {
							updateWorkFlowForLife(workFlowDTO, proposal.getId());
						}
					}
				} else if (getHealthReferenceTypes().contains(referenceType)) {
					for (T proposal : selectedLifeProposals) {
						WorkFlowDTO workFlowDTO = new WorkFlowDTO(proposal.getId(), loginBranchId, "", WorkflowTask.CONFIRMATION, referenceType, TransactionType.UNDERWRITING, user,
								responsiblePerson);
						MedicalPolicy medicalPolicy = medicalPolicyService.findMedicalPolicyByProposalId(proposal.getId());
						medicalProposalService.deletePayment(medicalPolicy, workFlowDTO);
					}
				}else if (getTravelReferenceTypes().contains(referenceType)) {
					for (T proposal : selectedLifeProposals) {
						WorkFlowDTO workFlowDTO = new WorkFlowDTO(proposal.getId(), loginBranchId, "", WorkflowTask.CONFIRMATION, referenceType, TransactionType.UNDERWRITING, user,
								responsiblePerson);
						TravelProposal travelPolicy = travelProposalService.findTravelProposalById(proposal.getId());
						travelProposalService.deletePayment(travelPolicy, workFlowDTO);
					}
				}

			}
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.WORKFLOW_CHANGER_PROCESS_SUCCESS);

			result = "dashboard";
		} catch (SystemException ex) {
			handelSysException(ex);
		}
		return result;
	}

	public void selectUser() {
		WorkFlowType wfType = null;
		switch (referenceType) {
			case ENDOWMENT_LIFE:
			case GROUP_LIFE:
			case SPORT_MAN:
				wfType = WorkFlowType.LIFE;
				break;
			case SHORT_ENDOWMENT_LIFE:
				wfType = WorkFlowType.SHORT_ENDOWMENT;
				break;
			case FARMER:
				wfType = WorkFlowType.FARMER;
				break;
			case PA:
				wfType = WorkFlowType.PERSONAL_ACCIDENT;
				break;
			case SNAKE_BITE:
				wfType = WorkFlowType.SNAKE_BITE;
				break;
			case TRAVEL:
				wfType = WorkFlowType.TRAVEL;
				break;

			case HEALTH:
			case CRITICAL_ILLNESS:
			case MICRO_HEALTH:
				wfType = WorkFlowType.MEDICAL_INSURANCE;
				break;
			case STUDENT_LIFE:
				wfType = WorkFlowType.STUDENT_LIFE;
				break;
			case PUBLIC_TERM_LIFE:
				wfType = WorkFlowType.PUBLIC_TERM_LIFE;
				break;
			case SPECIAL_TRAVEL:
				wfType = WorkFlowType.SPECIAL_TRAVEL;
				break;
			case LIFESURRENDER:
				wfType = WorkFlowType.LIFESURRENDER;
				break;
			default:
				break;
		}
		selectUser(WorkflowTask.CONFIRMATION, wfType, TransactionType.UNDERWRITING, loginBranchId, null);
	}

	public void returnUser(SelectEvent event) {
		User user = (User) event.getObject();
		this.responsiblePerson = user;
	}

	public User getResponsiblePerson() {
		return responsiblePerson;
	}

}
