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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.ace.insurance.common.TableName;
import org.ace.insurance.common.UserRecorder;
import org.ace.insurance.product.Product;
import org.ace.insurance.system.common.express.Express;
import org.ace.java.component.idgen.service.IDInterceptor;

@Entity
@Table(name = TableName.TRAVEL_EXPRESS)
@TableGenerator(name = "EXPRESS_GEN", table = "ID_GEN", pkColumnName = "GEN_NAME", valueColumnName = "GEN_VAL", pkColumnValue = "EXPRESS_GEN", allocationSize = 10)
@NamedQueries(value = { @NamedQuery(name = "TravelExpress.findAll", query = "SELECT e FROM TravelExpress e") })
@EntityListeners(IDInterceptor.class)
public class TravelExpress implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "EXPRESS_GEN")
	private String id;

	private int noOfPassenger;
	private double noOfUnit;
	private double premium;
	private double sumInsured;
	private double commission;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EXPRESSID", referencedColumnName = "ID")
	private Express express;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "travelExpress", orphanRemoval = true)
	private List<Tour> tourList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TRAVELPROPOSALID", referencedColumnName = "ID")
	private TravelProposal travelProposal;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCTID", referencedColumnName = "ID")
	private Product product;

	@Transient
	private String tempId;
	@Embedded
	private UserRecorder recorder;
	@Version
	private int version;

	public TravelExpress() {
		tempId = System.nanoTime() + "";
	}

	public String getId() {
		return id;
	}

	public void addTour(Tour tour) {
		if (tourList == null) {
			tourList = new ArrayList<Tour>();
		}
		if (!tourList.contains(tour)) {
			tour.setTravelExpress(this);
			tourList.add(tour);
		}
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

	public Express getExpress() {
		return express;
	}

	public void setExpress(Express express) {
		this.express = express;
	}

	public int getNoOfPassenger() {
		return noOfPassenger;
	}

	public void setNoOfPassenger(int noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}

	public double getPremium() {
		return premium;
	}

	public void setPremium(double premium) {
		this.premium = premium;
	}

	public double getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(double sumInsured) {
		this.sumInsured = sumInsured;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public TravelProposal getTravelProposal() {
		return travelProposal;
	}

	public void setTravelProposal(TravelProposal travelProposal) {
		this.travelProposal = travelProposal;
	}

	public double getNoOfUnit() {
		return noOfUnit;
	}

	public void setNoOfUnit(double noOfUnit) {
		this.noOfUnit = noOfUnit;
	}

	public List<Tour> getTourList() {
		if (tourList == null) {
			tourList = new ArrayList<Tour>();
		}
		return tourList;
	}

	public void setTourList(List<Tour> tourList) {
		this.tourList = tourList;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public List<ExpressDetail> getExpressDetailsList(String id) {
		List<ExpressDetail> expressDetailsList = new ArrayList<>();
		for (Tour tour : getTourList()) {
			if (tour.getTravelExpress().id.equals(id)) {
				expressDetailsList.addAll(tour.getExpressDetails(tour.getId()));
			}

		}
		return expressDetailsList;
	}

	public String getTourNameList() {
		String tourNameList = null;
		StringBuffer result = new StringBuffer();

		Iterator<Tour> iterator = tourList.iterator();
		while (iterator.hasNext()) {
			Tour tour = iterator.next();
			result.append(tour.getTourName());
			if (iterator.hasNext()) {
				result.append(", ");
			}
		}
		tourNameList = result.toString();
		return tourNameList;
	}

	public String getRegistrationNoList() {
		String tourNameList = null;
		StringBuffer result = new StringBuffer();

		Iterator<Tour> iterator = tourList.iterator();
		while (iterator.hasNext()) {
			Tour tour = iterator.next();
			result.append(tour.getRegistrationNoList());
			if (iterator.hasNext()) {
				result.append(", ");
			}
		}
		tourNameList = result.toString();
		return tourNameList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(commission);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((express == null) ? 0 : express.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		temp = Double.doubleToLongBits(premium);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + noOfPassenger;
		temp = Double.doubleToLongBits(noOfUnit);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((product == null) ? 0 : product.hashCode());
		result = prime * result + ((recorder == null) ? 0 : recorder.hashCode());
		temp = Double.doubleToLongBits(sumInsured);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((tempId == null) ? 0 : tempId.hashCode());
		result = prime * result + ((travelProposal == null) ? 0 : travelProposal.hashCode());
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
		TravelExpress other = (TravelExpress) obj;
		if (Double.doubleToLongBits(commission) != Double.doubleToLongBits(other.commission))
			return false;
		if (express == null) {
			if (other.express != null)
				return false;
		} else if (!express.equals(other.express))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(premium) != Double.doubleToLongBits(other.premium))
			return false;
		if (noOfPassenger != other.noOfPassenger)
			return false;
		if (Double.doubleToLongBits(noOfUnit) != Double.doubleToLongBits(other.noOfUnit))
			return false;
		if (product == null) {
			if (other.product != null)
				return false;
		} else if (!product.equals(other.product))
			return false;
		if (recorder == null) {
			if (other.recorder != null)
				return false;
		} else if (!recorder.equals(other.recorder))
			return false;
		if (Double.doubleToLongBits(sumInsured) != Double.doubleToLongBits(other.sumInsured))
			return false;
		if (tempId == null) {
			if (other.tempId != null)
				return false;
		} else if (!tempId.equals(other.tempId))
			return false;
		if (travelProposal == null) {
			if (other.travelProposal != null)
				return false;
		} else if (!travelProposal.equals(other.travelProposal))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
