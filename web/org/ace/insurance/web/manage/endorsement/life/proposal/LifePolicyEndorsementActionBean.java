package org.ace.insurance.web.manage.endorsement.life.proposal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;

import org.ace.insurance.common.InsuranceType;
import org.ace.insurance.common.PolicyCriteria;
import org.ace.insurance.life.policy.LPC001;
import org.ace.insurance.life.policy.LifePolicy;
import org.ace.insurance.life.policy.service.interfaces.ILifePolicyService;
import org.ace.insurance.payment.service.interfaces.IPaymentService;
import org.ace.insurance.product.Product;
import org.ace.insurance.web.common.KeyFactorChecker;
import org.ace.java.web.common.BaseBean;
import org.ace.java.web.common.Constants;
import org.ace.java.web.common.MessageId;
import org.apache.commons.lang.StringUtils;

@ViewScoped
@ManagedBean(name = "LifePolicyEndorsementActionBean")
public class LifePolicyEndorsementActionBean extends BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private PolicyCriteria policyCriteria;
	private List<LPC001> lifePolicyList;
	private int paymentYear;
	private int paymentMonth;
	boolean isShortermEndownment;
	boolean isPubliclife;
	

	@ManagedProperty(value = "#{LifePolicyService}")
	private ILifePolicyService lifePolicyService;

	public void setLifePolicyService(ILifePolicyService lifePolicyService) {
		this.lifePolicyService = lifePolicyService;
	}
	
	@ManagedProperty(value = "#{PaymentService}")
	private IPaymentService paymentService;

	public void setPaymentService(IPaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostConstruct
	public void init() {
		lifePolicyList = new ArrayList<LPC001>();
		policyCriteria = new PolicyCriteria();
	}

	public void search() {
		if (inputCheck(policyCriteria)) {
			lifePolicyList = lifePolicyService.findByPolicyCriteria(policyCriteria, 30);
			
			}
		
	}

	public void reset() {
		policyCriteria.setCriteriaValue(null);
		policyCriteria.setPolicyCriteria(null);
		policyCriteria.setProduct(null);
		lifePolicyList = new ArrayList<LPC001>();
	}

	private boolean inputCheck(PolicyCriteria policyCriteria) {
		boolean result = true;
		String formId = "LifePolicyEndorsementForm";
		if ((policyCriteria.getPolicyCriteria() == null)) {
			addErrorMessage(formId + ":selectPolicyCriteria", MessageId.PLEASE_SELECT_POLICY_CRITERIA);
			result = false;
		}
		if (StringUtils.isBlank(policyCriteria.getCriteriaValue())) {
			addErrorMessage(formId + ":policyCriteria", MessageId.CRITERIA_VALUE_REQUIRED);
			result = false;
		}
		if (policyCriteria.getProduct() == null) {
			addErrorMessage(formId + ":selectProductCriteria", MessageId.PLEASE_SELECT_PRODUCT);
			result = false;
		}
		return result;
	}

	public String paymentTypeChange(LPC001 lifePolicy) {
		
		String result="";
		LifePolicy policy = lifePolicyService.findLifePolicyById(lifePolicy.getId());
		int paymentTerm = paymentService.findPaymentTermByPolicyID(lifePolicy.getId()).stream().mapToInt(v -> v.getToTerm()).max().orElseThrow(NoSuchElementException::new);
	int year=calculatePaymentType(paymentTerm, policy);
	Product product =policy.getLifeProposal().getProposalInsuredPersonList().get(0).getProduct();

	isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
	isPubliclife = KeyFactorChecker.isPublicLife(product);
	if(isShortermEndownment || isPubliclife) {
	if(year >= 1) {
		putParam("lifePolicy", policy);
		putParam("InsuranceType", InsuranceType.LIFE);
		putParam("isPaymentTypeChange",true);
		result= "endorselifeProposal";
		
	}else {
		ExternalContext extContext = getFacesContext().getExternalContext();
		extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.POLICYNOTFINISH);
		result = "dashboard";
	}
	}else {
		putParam("lifePolicy", policy);
		putParam("InsuranceType", InsuranceType.LIFE);
		putParam("isPaymentTypeChange",true);
		result= "endorselifeProposal";
		
	}
		return result;
		
	}
	
	public String siChange(LPC001 lifePolicy) {
		String result="";
		LifePolicy policy = lifePolicyService.findLifePolicyById(lifePolicy.getId());
		int paymentTerm = paymentService.findPaymentTermByPolicyID(lifePolicy.getId()).stream().mapToInt(v -> v.getToTerm()).max().orElseThrow(NoSuchElementException::new);
		int year=calculatePaymentType(paymentTerm, policy);
		Product product =policy.getLifeProposal().getProposalInsuredPersonList().get(0).getProduct();
		isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
		isPubliclife = KeyFactorChecker.isPublicLife(product);
		if(isShortermEndownment || isPubliclife) {
		if(year >= 1) {
			putParam("lifePolicy", policy);
			putParam("InsuranceType", InsuranceType.LIFE);
			putParam("isSIChange",true);
			result= "endorselifeProposal";
			
		}else {
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.POLICYNOTFINISH);
			result = "dashboard";
		}
		}else {
			putParam("lifePolicy", policy);
			putParam("InsuranceType", InsuranceType.LIFE);
			putParam("isSIChange",true);
			result= "endorselifeProposal";
			
		}
		
		return result;

	}
	public String termChange(LPC001 lifePolicy) {
		String result="";
		LifePolicy policy = lifePolicyService.findLifePolicyById(lifePolicy.getId());
		int paymentTerm = paymentService.findPaymentTermByPolicyID(lifePolicy.getId()).stream().mapToInt(v -> v.getToTerm()).max().orElseThrow(NoSuchElementException::new);
		int year=calculatePaymentType(paymentTerm, policy);
		Product product =policy.getLifeProposal().getProposalInsuredPersonList().get(0).getProduct();
		isShortermEndownment = KeyFactorChecker.isShortTermEndowment(product.getId());
		isPubliclife = KeyFactorChecker.isPublicLife(product);
		if(isShortermEndownment || isPubliclife) {
		if(year >= 1) {
			putParam("lifePolicy", policy);
			putParam("InsuranceType", InsuranceType.LIFE);
			putParam("isTermChange",true);
			result= "endorselifeProposal";
			
		}else {
			ExternalContext extContext = getFacesContext().getExternalContext();
			extContext.getSessionMap().put(Constants.MESSAGE_ID, MessageId.POLICYNOTFINISH);
			result = "dashboard";
		}
		}else {
			putParam("lifePolicy", policy);
			putParam("InsuranceType", InsuranceType.LIFE);
			putParam("isTermChange",true);
			result= "endorselifeProposal";
			
		}
		return result;
	}
	public String nonfinancialEndorseChange(LPC001 lifePolicy) {
		String result="";
		LifePolicy policy = lifePolicyService.findLifePolicyById(lifePolicy.getId());
		if(policy.isEndorsementApplied()==true) {
			result="";
			addInfoMessage(null, MessageId.POLICYNOTFINISH);
		}else {
		putParam("lifePolicy", policy);
		putParam("InsuranceType", InsuranceType.LIFE);
		putParam("isNonfinancialChange",true);
		result= "endorselifeProposal";
		}
		return result;
	}
	
	public String groupLifeChange(LPC001 lifePolicy) {
		String result="";
		LifePolicy policy = lifePolicyService.findLifePolicyById(lifePolicy.getId());
		if(policy.isEndorsementApplied()==true) {
			result="";
			addInfoMessage(null, MessageId.POLICYNOTFINISH);
		}else {
		putParam("lifePolicy", policy);
		putParam("InsuranceType", InsuranceType.LIFE);
		putParam("isGroupLifeChange",true);
		result= "endorselifeProposal";
		}
		return result;
	}
	
	
	public PolicyCriteria getPolicyCriteria() {
		return policyCriteria;
	}

	public void setPolicyCriteria(PolicyCriteria policyCriteria) {
		this.policyCriteria = policyCriteria;
	}

	public List<LPC001> getLifePolicyList() {
		return lifePolicyList;
	}
	
	private int calculatePaymentType(int paymentTerm, LifePolicy lifePolicy) {
		if(lifePolicy.getPaymentType().getName().equals("MONTHLY")) {
			paymentYear = Integer.valueOf(String.valueOf(Math.round(paymentTerm / 12)).toString());
			paymentMonth = paymentTerm % 12;
		} else if(lifePolicy.getPaymentType().getName().equals("SEMI-ANNUAL")) {
			paymentYear = Integer.valueOf(String.valueOf(Math.round((paymentTerm * 6) / 12)).toString());
			paymentMonth = (paymentTerm * 6) % 12;
		} else if(lifePolicy.getPaymentType().getName().equals("QUARTER")){
			paymentYear = Integer.valueOf(String.valueOf(Math.round((paymentTerm * 3) / 12)).toString());
			paymentMonth = (paymentTerm * 3) % 12;
		} else {
			paymentYear = paymentTerm;
		}
		return paymentYear;
	}

	public int getPaymentYear() {
		return paymentYear;
	}

	public void setPaymentYear(int paymentYear) {
		this.paymentYear = paymentYear;
	}

	public int getPaymentMonth() {
		return paymentMonth;
	}

	public void setPaymentMonth(int paymentMonth) {
		this.paymentMonth = paymentMonth;
	}
	
	public boolean isGroupLife() {
		boolean result = false;
		if(policyCriteria.getProduct().equals("GROUP LIFE")) {
			result=true;
		}
			
	
		return result;
	
}
}
