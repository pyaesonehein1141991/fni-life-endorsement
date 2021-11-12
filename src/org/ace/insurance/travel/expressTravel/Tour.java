package org.ace.insurance.travel.expressTravel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.system.common.occurrence.Occurrence;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.TOUR)
@TableGenerator(name = "TOUR_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "TOUR_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "Tour.findAll", query = "SELECT t FROM Tour t ORDER BY t.occurrence.description ASC"),
		@NamedQuery(name = "Tour.findById", query = "SELECT t FROM Tour t WHERE t.id = :id") })
@EntityListeners(IDInterceptor.class)
public class Tour implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TOUR_GEN")
	private String id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCCURRENCEID", referencedColumnName = "ID")
	private Occurrence occurrence;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tour", orphanRemoval = true)
	private List<ExpressDetail> expressDetailList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRAVELEXPRESSID", referencedColumnName = "ID")
	private TravelExpress travelExpress;
	@Embedded
	private UserRecorder recorder;
	@Version
	private int version;

	public Tour() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public void addExpressDetail(ExpressDetail expressDetail) {
		if (!getExpressDetailList().contains(expressDetail)) {
			expressDetail.setTour(this);
			getExpressDetailList().add(expressDetail);
		}
	}

	public List<ExpressDetail> getExpressDetails(String tourId) {
		List<ExpressDetail> detailsList = new ArrayList<>();
		for (ExpressDetail details : getExpressDetailList()) {
			if (details.getTour().id.equals(tourId)) {
				detailsList.add(details);
			}
		}
		return detailsList;
	}

	public List<ExpressDetail> getExpressDetailList() {
		if (expressDetailList == null) {
			expressDetailList = new ArrayList<ExpressDetail>();
		}
		return expressDetailList;
	}

	public void setExpressDetailList(List<ExpressDetail> expressDetailList) {
		this.expressDetailList = expressDetailList;
	}

	public TravelExpress getTravelExpress() {
		return travelExpress;
	}

	public void setTravelExpress(TravelExpress travelExpress) {
		this.travelExpress = travelExpress;
	}

	public Occurrence getOccurrence() {
		return occurrence;
	}

	public void setOccurrence(Occurrence occurrence) {
		this.occurrence = occurrence;
	}

	public int getNoOfPassenger() {
		int result = 0;
		if (expressDetailList != null) {
			for (ExpressDetail expressDetail : expressDetailList) {
				result += expressDetail.getNoOfPassenger();
			}
		}
		return result;
	}

	public int getNoOfUnit() {
		int result = 0;
		if (expressDetailList != null) {
			for (ExpressDetail expressDetail : expressDetailList) {
				result += expressDetail.getNoOfUnit();
			}
		}
		return result;
	}

	public double getNetPremium() {
		double result = 0;
		if (expressDetailList != null) {
			for (ExpressDetail expressDetail : expressDetailList) {
				result += expressDetail.getNetPremium();
			}
		}
		return result;
	}

	public String getRegistrationNoList() {
		String regNoList = null;
		StringBuffer result = new StringBuffer();

		Iterator<ExpressDetail> iterator = expressDetailList.iterator();
		while (iterator.hasNext()) {
			ExpressDetail expDetail = iterator.next();
			result.append(expDetail.getRegistrationNo());
			if (iterator.hasNext()) {
				result.append(", ");
			}
		}
		regNoList = result.toString();
		return regNoList;
	}

	public String getTourName() {
		String tourName = occurrence.getDescription();
		return tourName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((occurrence == null) ? 0 : occurrence.hashCode());
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		result = prime * result + ((travelExpress == null) ? 0 : travelExpress.hashCode());
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
		Tour other = (Tour) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (occurrence == null) {
			if (other.occurrence != null)
				return false;
		} else if (!occurrence.equals(other.occurrence))
			return false;
		if (recorder == null) {
			if (other.recorder != null)
				return false;
		} else if (!recorder.equals(other.recorder))
			return false;
		if (travelExpress == null) {
			if (other.travelExpress != null)
				return false;
		} else if (!travelExpress.equals(other.travelExpress))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
