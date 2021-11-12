package org.ace.insurance.life.claim;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.LIFEPOLICYCLAIM)
@TableGenerator(name = "LIFEPOLICYCLAIM_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "LIFEPOLICYCLAIM_GEN", allocationSize = 10)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "CLAIMTYPE", discriminatorType = DiscriminatorType.STRING)
@NamedQueries(value = { @NamedQuery(name = "findCountByPolicyNo", query = "SELECT COUNT(l) FROM LifePolicyClaim l WHERE l.policyNo = :policyNo and l.isApproved=true") })
@EntityListeners(IDInterceptor.class)
public class LifePolicyClaim implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "LIFEPOLICYCLAIM_GEN")
	private String id;

	@Column(name = "APPROVE")
	private boolean isApproved;

	@Column(name = "REJECTREASON")
	private String rejectReason;

	private String approvedReason;

	@Column(name = "CLAIMTYPE", insertable = false, updatable = false)
	private String claimType;

	@Column(name = "POLICYNO")
	private String policyNo;

	private String occurancePlace;

	private String hospitalName;

	@Version
	private int version;

	@Embedded
	private UserRecorder recorder;

	public LifePolicyClaim() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isApproved() {
		return isApproved;
	}

	public void setApproved(boolean isApproved) {
		this.isApproved = isApproved;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getClaimType() {
		return claimType;
	}

	public void setClaimType(String claimType) {
		this.claimType = claimType;
	}

	public String getApprovedReason() {
		return approvedReason;
	}

	public void setApprovedReason(String approvedReason) {
		this.approvedReason = approvedReason;
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

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getOccurancePlace() {
		return occurancePlace;
	}

	public void setOccurancePlace(String occurancePlace) {
		this.occurancePlace = occurancePlace;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((approvedReason == null) ? 0 : approvedReason.hashCode());
		result = prime * result + ((claimType == null) ? 0 : claimType.hashCode());
		result = prime * result + ((hospitalName == null) ? 0 : hospitalName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isApproved ? 1231 : 1237);
		result = prime * result + ((occurancePlace == null) ? 0 : occurancePlace.hashCode());
		result = prime * result + ((policyNo == null) ? 0 : policyNo.hashCode());
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		result = prime * result + ((rejectReason == null) ? 0 : rejectReason.hashCode());
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
		LifePolicyClaim other = (LifePolicyClaim) obj;
		if (approvedReason == null) {
			if (other.approvedReason != null)
				return false;
		} else if (!approvedReason.equals(other.approvedReason))
			return false;
		if (claimType == null) {
			if (other.claimType != null)
				return false;
		} else if (!claimType.equals(other.claimType))
			return false;
		if (hospitalName == null) {
			if (other.hospitalName != null)
				return false;
		} else if (!hospitalName.equals(other.hospitalName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isApproved != other.isApproved)
			return false;
		if (occurancePlace == null) {
			if (other.occurancePlace != null)
				return false;
		} else if (!occurancePlace.equals(other.occurancePlace))
			return false;
		if (policyNo == null) {
			if (other.policyNo != null)
				return false;
		} else if (!policyNo.equals(other.policyNo))
			return false;
		if (recorder == null) {
			if (other.recorder != null)
				return false;
		} else if (!recorder.equals(other.recorder))
			return false;
		if (rejectReason == null) {
			if (other.rejectReason != null)
				return false;
		} else if (!rejectReason.equals(other.rejectReason))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
