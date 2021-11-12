package org.ace.insurance.web.manage.travel.personTravel;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.ace.insurance.travel.personTravel.policy.PersonTravelPolicy;
import org.ace.insurance.travel.personTravel.policy.service.interfaces.IPersonTravelPolicyService;
import org.ace.java.web.common.BaseBean;

@ViewScoped
@ManagedBean(name = "PersonTravelPolicyTemplateBean")
public class PersonTravelPolicyTemplateBean extends BaseBean {

	private PersonTravelPolicy personTravelPolicy;

	@ManagedProperty(value = "#{PersonTravelPolicyService}")
	private IPersonTravelPolicyService personTravelPolicyService;

	public void setPersonTravelPolicyService(IPersonTravelPolicyService personTravelPolicyService) {
		this.personTravelPolicyService = personTravelPolicyService;
	}

	@SuppressWarnings("unchecked")
	private void initializationInjection() {

		personTravelPolicy = (PersonTravelPolicy) getParam("personTravelPolicy");

	}

	@PreDestroy
	public void destroy() {
		removeParam("personTravelPolicy");

	}

	@PostConstruct
	public void init() {
		initializationInjection();
	}

	public PersonTravelPolicy getPersonTravelPolicy() {
		return personTravelPolicy;
	}

}
