package org.ace.insurance.life.endorsement;

public enum LifeEndorseValue {
	
	NAME_CHANGE("Name Change"), ADDRESSS_CHANGE("AddressChange"), NRCNO_CHANGE("NRCNo Change"), 
	RELATIONSHIP_CHANGE("Relationship Change");

	public String label;

	private LifeEndorseValue(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
