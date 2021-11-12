package org.ace.insurance.system.common.reinsuranceRatio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.system.common.coinsurancecompany.CoinsuranceCompany;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.REINSURANCE_DETAIL_RATIO)
@TableGenerator(name = "REINSURANCE_DETAIL_RATIO_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "REINSURANCE_DETAIL_RATIO_GEN", allocationSize = 10)
@EntityListeners(IDInterceptor.class)
public class ReinsuranceDetailRatio implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "REINSURANCE_DETAIL_RATIO_GEN")
	private String id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COINSURANCECOMPANYID", referencedColumnName = "ID")
	private CoinsuranceCompany coinsuranceCompany;

	@Column(name = "MAXIMUMSUMINSURED")
	private double maxSI;
	@Column(name = "MINIIMUMSUMINSURED")
	private double minSI;
	@Embedded
	private UserRecorder recorder;

	@Version
	private int version;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CoinsuranceCompany getCoinsuranceCompany() {
		return coinsuranceCompany;
	}

	public void setCoinsuranceCompany(CoinsuranceCompany coinsuranceCompany) {
		this.coinsuranceCompany = coinsuranceCompany;
	}

	public double getMaxSI() {
		return maxSI;
	}

	public void setMaxSI(double maxSI) {
		this.maxSI = maxSI;
	}

	public double getMinSI() {
		return minSI;
	}

	public void setMinSI(double minSI) {
		this.minSI = minSI;
	}

	public UserRecorder getRecorder() {
		return recorder;
	}

	public void setRecorder(UserRecorder recorder) {
		this.recorder = recorder;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coinsuranceCompany == null) ? 0 : coinsuranceCompany.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		long temp;
		temp = Double.doubleToLongBits(maxSI);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minSI);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
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
		ReinsuranceDetailRatio other = (ReinsuranceDetailRatio) obj;
		if (coinsuranceCompany == null) {
			if (other.coinsuranceCompany != null)
				return false;
		} else if (!coinsuranceCompany.equals(other.coinsuranceCompany))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(maxSI) != Double.doubleToLongBits(other.maxSI))
			return false;
		if (Double.doubleToLongBits(minSI) != Double.doubleToLongBits(other.minSI))
			return false;
		if (recorder == null) {
			if (other.recorder != null)
				return false;
		} else if (!recorder.equals(other.recorder))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
