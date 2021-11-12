package org.ace.insurance.web.manage.travel;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.insurance.travel.personTravel.proposal.PersonTravelProposal;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "PersonTravelTemplateBean")
public class PersonTravelTemplateBean extends BaseBean {

	private PersonTravelProposal personTravelProposal;
	private PersonTravelPolicy personTravelPolicy;
	private boolean isshowPolicy;
	private List<WorkFlowHistory> workFlowList;

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	@SuppressWarnings("unchecked")
	private void initializationInjection() {
		personTravelProposal = (PersonTravelProposal) getParam("personTravelProposal");
		isshowPolicy = isExistParam("showPolicy") ? (Boolean) getParam("showPolicy") : false;
		personTravelPolicy = isshowPolicy ? personTravelPolicyService.findPersonTravelPolicyByProposalId(personTravelProposal.getId()) : null;
		workFlowList = (List<WorkFlowHistory>) getParam("workFlowList");
	}

	@PreDestroy
	public void destroy() {
		removeParam("personTravelProposal");
		removeParam("workFlowList");
		removeParam("showPolicy");
	}

	@PostConstruct
	public void init() {
		initializationInjection();
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowList;
	}

	public PersonTravelProposal getPersonTravelProposal() {
		return personTravelProposal;
	}

	public PersonTravelPolicy getPersonTravelPolicy() {
		return personTravelPolicy;
	}

	public boolean isIsshowPolicy() {
		return isshowPolicy;
	}

}
