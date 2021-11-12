package org.ace.insurance.travel.personTravel.proposal;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.common.TableName;
import org.ace.insurance.system.common.keyfactor.KeyFactor;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.PERSONTRAVEL_PROPOSAL_KEYFACTORVALUE)
@TableGenerator(name = "PERSONTRAVEL_PROPOSAL_KEYFACTORVALUE_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "PERSONTRAVEL_PROPOSAL_KEYFACTORVALUE_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "PersonTravelProposalKeyfactorValue.findAll", query = "SELECT t FROM PersonTravelProposalKeyfactorValue t") })
@EntityListeners(IDInterceptor.class)
public class PersonTravelProposalKeyfactorValue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "PERSONTRAVEL_PROPOSAL_KEYFACTORVALUE_GEN")
	private String id;
	private String value;

	@OneToOne
	@JoinColumn(name = "KEYFACTORID", referencedColumnName = "ID")
	private KeyFactor keyfactor;

	@Version
	private int version;

	@Embedded
	private UserRecorder recorder;

	public PersonTravelProposalKeyfactorValue() {
	}

	public PersonTravelProposalKeyfactorValue(KeyFactor keyfactor) {
		this.keyfactor = keyfactor;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public KeyFactor getKeyfactor() {
		return keyfactor;
	}

	public void setKeyfactor(KeyFactor keyfactor) {
		this.keyfactor = keyfactor;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public UserRecorder getRecorder() {
		return recorder;
	}

	public void setRecorder(UserRecorder recorder) {
		this.recorder = recorder;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((keyfactor == null) ? 0 : keyfactor.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + version;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonTravelProposalKeyfactorValue other = (PersonTravelProposalKeyfactorValue) obj;
		if (recorder == null) {
			if (other.recorder != null)
				return false;
		} else if (!recorder.equals(other.recorder))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (keyfactor == null) {
			if (other.keyfactor != null)
				return false;
		} else if (!keyfactor.equals(other.keyfactor))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
