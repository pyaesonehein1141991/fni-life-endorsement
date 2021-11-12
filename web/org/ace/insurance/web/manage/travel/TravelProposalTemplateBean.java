package org.ace.insurance.web.manage.travel;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.travel.claim.TravelClaim;
import org.ace.insurance.travel.expressTravel.TravelProposal;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "TravelProposalTemplateBean")
public class TravelProposalTemplateBean extends BaseBean {

	private TravelProposal travelProposal;
	private TravelClaim travelClaim;
	private List<WorkFlowHistory> workFlowList;

	@SuppressWarnings("unchecked")
	private void initializationInjection() {
		travelProposal = (TravelProposal) getParam("travelProposal");
		travelClaim = (TravelClaim) getParam("travelClaim");
		workFlowList = (List<WorkFlowHistory>) getParam("workFlowList");
	}

	@PreDestroy
	public void destroy() {
		removeParam("travelProposal");
		removeParam("workFlowList");
	}

	@PostConstruct
	public void init() {
		initializationInjection();
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowList;
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public TravelClaim getTravelClaim() {
		return travelClaim;
	}

}
