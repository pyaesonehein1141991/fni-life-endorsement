package org.ace.insurance.life.endorsement;

public enum LifeEndorseItem {

	INCREASE_SI("Increase SI"), INCREASE_TERM("Increas Term"), DECREASE_SI("Decrease SI"), DECREASE_TERM("Decrease Term"), TERMINATE_CUSTOMER(
			"Terminate Customer"), INSUREDPERSON_NEW("InsuredPerson New"), ORGANIZATION_CHANGE("Organization Change"), BENEFICIARY_CHANGE(
					"Beneficiary Change"), REPLACE("Replace"), INSUREDPERSON_CHANG("InsuredPersonChange"), ADDRESSS_CHANGE("AddressChange"), NRCNO_CHANGE("NRCNo Change"),PAYMENTTYPE_CHANGE("PaymentTypeChange");

	public String label;

	private LifeEndorseItem(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
