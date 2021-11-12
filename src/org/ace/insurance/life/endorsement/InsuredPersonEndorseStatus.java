package org.ace.insurance.life.endorsement;

public enum InsuredPersonEndorseStatus {

	REPLACE("Replace"), NAME_CHANGE("NameChange"), NEW("New"), EDIT("Edit"), NRCNO_CHANGE("NRCNO Change"), ADDRESSS_CHANGE("AddressChange"), TERMINATE("Terminate"), TERM_CHANGE(
			"TERM_CHANGE"),PAYMENTTYPE_CHANGE("PAYMENTTYPE_CHANGE"),SI_CHANGE("SI Change");

	private String label;

	private InsuredPersonEndorseStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
