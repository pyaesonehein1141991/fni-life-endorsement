package org.ace.insurance.web.manage.life;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.ace.insurance.life.policyHistory.service.interfaces.ILifePolicyHistoryService;
import org.ace.insurance.life.proposal.LifeProposal;
import org.ace.insurance.product.Product;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.insurance.workflow.WorkFlowHistory;
import org.ace.java.web.common.BaseBean;

@RequestScoped
@ManagedBean(name = "LifeProposalEndorsementTemplateBean")
public class LifeProposalEndorsementTemplateBean extends BaseBean {

	@ManagedProperty(value = "#{LifePolicyHistoryService}")
	private ILifePolicyHistoryService lifePolicyHistoryService;

	public void setLifePolicyHistoryService(ILifePolicyHistoryService lifePolicyHistoryService) {
		this.lifePolicyHistoryService = lifePolicyHistoryService;
	}

	private LifeProposal lifeProposal;
	private LifeProposal lifeEndorsementProposal;
	private List<WorkFlowHistory> workFlowList;

	private boolean isEndorse;
	private boolean isSportMan;
	private boolean isSnakeBite;
	private boolean isGroupLife;
	private boolean isFarmer;

	@SuppressWarnings("unchecked")
	private void initializeInjection() {
		lifeProposal = (LifeProposal) getParam("lifeProposalDetail");
		lifeEndorsementProposal = (LifeProposal) getParam("lifeEndrosementProposal");
		
		workFlowList = (List<WorkFlowHistory>) getParam("workFlowList");
	}

	@PostConstruct
	public void init() {
		initializeInjection();
		loadRenderValues();
	}

	private void loadRenderValues() {
		Product product = lifeProposal.getProposalInsuredPersonList().get(0).getProduct();
		isFarmer = KeyFactorChecker.isFarmer(product);
		isSportMan = KeyFactorChecker.isSportMan(product);
		isSnakeBite = KeyFactorChecker.isSnakeBite(product.getId());
		isGroupLife = KeyFactorChecker.isGroupLife(product);
	}

	public LifeProposal getLifeProposal() {
		return lifeProposal;
	}

	public List<WorkFlowHistory> getWorkFlowList() {
		return workFlowList;
	}

	public boolean isEndorse() {
		return isEndorse;
	}

	public ILifePolicyHistoryService getLifePolicyHistoryService() {
		return lifePolicyHistoryService;
	}

	public boolean getIsSportMan() {
		return isSportMan;
	}

	public boolean getIsSnakeBite() {
		return isSnakeBite;
	}

	public boolean getIsGroupLife() {
		return isGroupLife;
	}

	public boolean getIsFarmer() {
		return isFarmer;
	}

	public LifeProposal getLifeEndorsementProposal() {
		return lifeEndorsementProposal;
	}

	public void setLifeEndorsementProposal(LifeProposal lifeEndorsementProposal) {
		this.lifeEndorsementProposal = lifeEndorsementProposal;
	}

}
